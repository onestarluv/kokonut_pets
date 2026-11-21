package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Databases {

    @Getter
    @Setter
    public static MySQLDB mySQL;

    // 에고버스: Kokonut-Core DB 의 테이블 접두사를 쓴다 (init 에서 정해진다).
    private static String table = "mcpets_player_data";
    private static String activeTable = "mcpets_active_pet";
    private static ConcurrentHashMap<UUID, Object> playerLocks = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Active-pet record: remembers the pet a player had active when they quit,
    // used to restore it on the destination server after a Velocity cross-server
    // switch via the dedicated mcpets_active_pet table.
    // -------------------------------------------------------------------------

    private static final String PET_ID_DELIMITER = ",";

    private static final String SKIN_DELIMITER = ":";

    public static class ActivePetRecord {
        private final List<String> petIds;
        private final Map<String, String> skinIds;
        private final long updatedAt;

        public ActivePetRecord(List<String> petIds, Map<String, String> skinIds, long updatedAt) {
            this.petIds = petIds;
            this.skinIds = skinIds;
            this.updatedAt = updatedAt;
        }

        /** All active pet IDs stored in this record (may be multiple). */
        public List<String> getPetIds()  { return petIds; }
        /** Stable skin path id for a given pet ID, or null if the pet had no skin active. */
        public String getSkinId(String petId) { return skinIds.get(petId); }
        public long   getUpdatedAt() { return updatedAt; }
    }

    /**
     * 에고버스: Kokonut-Core 공용 DB 풀을 쓴다. 접속 설정(config.yml 의 MySQL 항목)은 없다.
     * Core DB 를 못 쓰면 YAML(PlayerData 폴더)로 저장한다.
     */
    public static boolean init() {
        final kr.gomsv.kokonutCore.api.database.CoreDatabase core =
                org.bukkit.Bukkit.getServicesManager().load(kr.gomsv.kokonutCore.api.database.CoreDatabase.class);
        if (core == null) {
            MCPets.getInstance().getLogger().severe("[Database] Kokonut-Core DB 서비스가 없어 YAML 로 저장합니다.");
            GlobalConfig.getInstance().setDatabaseSupport(false);
            return false;
        }
        Databases.setMySQL(new MySQLDB(core));
        if (!Databases.getMySQL().init()) {
            MCPets.getInstance().getLogger().severe("[Database] Kokonut-Core DB 에 연결하지 못해 YAML 로 저장합니다.");
            GlobalConfig.getInstance().setDatabaseSupport(false);
            return false;
        }
        table = getMySQL().tablePrefix() + "mcpets_player_data";
        activeTable = getMySQL().tablePrefix() + "mcpets_active_pet";
        GlobalConfig.getInstance().setDatabaseSupport(true);
        createSQLTables();
        fr.nocsy.mcpets.data.livingpets.PickupFilters.initDatabase(getMySQL().tablePrefix());
        MCPets.getInstance().getLogger().info("[Database] Kokonut-Core DB 사용: " + table + ", " + activeTable);
        return true;
    }

    public static void createSQLTables() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + table + " (id INT NOT NULL AUTO_INCREMENT, uuid TEXT, names TEXT, inventories LONGTEXT, data LONGTEXT, lastActivePet TEXT, primary key (id));");
        getMySQL().query("ALTER TABLE " + table + " MODIFY inventories LONGTEXT, MODIFY data LONGTEXT;");
        ResultSet rs = getMySQL().preparedQuery("SELECT count(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = 'lastActivePet'",
                table);
        try {
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                 getMySQL().query("ALTER TABLE " + table + " ADD lastActivePet TEXT;");
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to check lastActivePet column", e);
        }
        createActivePetTable();
    }

    private static void createActivePetTable() {
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + activeTable
                + " (uuid VARCHAR(36) NOT NULL, pet_id VARCHAR(255) NOT NULL,"
                + " updated_at BIGINT NOT NULL, PRIMARY KEY (uuid))");
    }

    public static boolean loadData() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        ResultSet playerData = getMySQL().query("SELECT * FROM " + table + ";");

        if (playerData == null)
            return true;

        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);

                synchronized (getLockForPlayer(uuid)) {
                    PlayerData pd = PlayerData.getEmpty(uuid);

                    // Unserialize the pet stats first, coz it influences the inventories
                    PetStats.remove(uuid);

                    for (String seria : playerData.getString("data").split(";;;")) {
                        PetStats stats = PetStats.unserialize(seria);
                        if (stats == null)
                            continue;
                        stats.launchTimers();
                        PetStats.register(stats);
                    }

                    // Unserialize the pet names; strip any legacy __active__ key silently
                    ConcurrentHashMap<String, String> names = unserializeData(playerData, "names");
                    names.remove("__active__");
                    pd.setMapOfRegisteredNames(names);

                    // Unserialize the pet inventories
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    try {
                        pd.setLastActivePet(playerData.getString("lastActivePet"));
                    } catch (SQLException e) {
                        // Column might not exist yet
                    }
                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        }
        catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to load player data from database", e1);
            return false;
        }

        return true;
    }

    private static Object getLockForPlayer(UUID playerUUID) {
        return playerLocks.computeIfAbsent(playerUUID, k -> new Object());
    }

    public static boolean loadData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        // Update the SQL query to fetch data for the specific player with the provided UUID
        ResultSet playerData = getMySQL().preparedQuery("SELECT * FROM " + table + " WHERE uuid=?", playerUUID.toString());
        return applyPlayerRows(playerUUID, playerData);
    }

    // ---- 에고버스: DB 작업 전용 스레드 (순서 보장), 접속 시 비동기 불러오기 ----

    /** 모든 DB 쓰기·읽기를 순서대로 처리하는 단일 스레드. 메인 스레드는 DB 를 기다리지 않는다. */
    private static final java.util.concurrent.ExecutorService EXECUTOR = java.util.concurrent.Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "Kokonut_Pets-DB");
        thread.setDaemon(true);
        return thread;
    });

    /** 접속 시 불러오기 진행 상태: 끝나면 메인 스레드에서 완료된다. */
    private static final Map<UUID, java.util.concurrent.CompletableFuture<Void>> LOADS = new ConcurrentHashMap<>();

    /** 플레이어 데이터를 비동기로 읽고, 메인 스레드에서 적용한다. 완료 future 를 돌려준다. */
    public static java.util.concurrent.CompletableFuture<Void> loadDataAsync(UUID playerUUID) {
        java.util.concurrent.CompletableFuture<Void> done = new java.util.concurrent.CompletableFuture<>();
        LOADS.put(playerUUID, done);
        if (!GlobalConfig.getInstance().isDatabaseSupport() || getMySQL() == null) {
            done.complete(null);
            return done;
        }
        EXECUTOR.execute(() -> {
            ResultSet rows = getMySQL().preparedQuery("SELECT * FROM " + table + " WHERE uuid=?", playerUUID.toString());
            if (!MCPets.getInstance().isEnabled()) {
                done.complete(null);
                return;
            }
            org.bukkit.Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
                try {
                    applyPlayerRows(playerUUID, rows);
                } finally {
                    done.complete(null);
                }
            });
        });
        return done;
    }

    /** 접속 시 불러오기가 끝나면 완료되는 future (진행 중인 것이 없으면 이미 완료). */
    public static java.util.concurrent.CompletableFuture<Void> whenLoaded(UUID playerUUID) {
        return LOADS.getOrDefault(playerUUID, java.util.concurrent.CompletableFuture.completedFuture(null));
    }

    private static boolean loading(UUID playerUUID) {
        java.util.concurrent.CompletableFuture<Void> load = LOADS.get(playerUUID);
        return load != null && !load.isDone();
    }

    public static void forgetLoad(UUID playerUUID) {
        LOADS.remove(playerUUID);
    }

    /** 조회 결과(플레이어 한 명)를 메모리에 적용한다. 메인 스레드에서 호출. */
    private static boolean applyPlayerRows(UUID playerUUID, ResultSet playerData) {

        if (playerData == null)
            return true;

        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);

                synchronized (getLockForPlayer(uuid)) {
                    PlayerData pd = PlayerData.getEmpty(uuid);

                    // Unserialize the pet stats first, coz it influences the inventories
                    PetStats.remove(uuid);

                    for (String seria : playerData.getString("data").split(";;;")) {
                        PetStats stats = PetStats.unserialize(seria);
                        if (stats == null)
                            continue;
                        stats.launchTimers();
                        PetStats.register(stats);
                    }

                    // Unserialize the pet names; strip any legacy __active__ key silently
                    ConcurrentHashMap<String, String> names = unserializeData(playerData, "names");
                    names.remove("__active__");
                    pd.setMapOfRegisteredNames(names);

                    // Unserialize the pet inventories
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    try {
                        pd.setLastActivePet(playerData.getString("lastActivePet"));
                    } catch (SQLException e) {
                        // Column might not exist yet
                    }
                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        }
        catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to load player data for " + playerUUID, e1);
            return false;
        }
        return true;
    }

    /** 저장할 내용 (호출한 스레드에서 미리 만든다). */
    private record Snapshot(UUID uuid, String names, String inventories, String data, String lastActivePet) { }

    private static Snapshot snapshot(UUID playerUUID) {
        PlayerData pd = PlayerData.getRegisteredData().get(playerUUID);
        if (pd == null) return null;
        String names = buildStringSerialized(pd.getMapOfRegisteredNames());
        String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());
        String lastActivePet = pd.getLastActivePet();
        if (lastActivePet == null) lastActivePet = "";
        StringBuilder data = new StringBuilder();
        for (PetStats stats : PetStats.getPetStats(playerUUID)) {
            data.append(stats.serialize()).append(";;;");
        }
        if (data.length() > 0)
            data = new StringBuilder(data.substring(0, data.length() - 3));
        return new Snapshot(playerUUID, names, inventories, data.toString(), lastActivePet);
    }

    /** DB 스레드에서 실행: 그 플레이어 한 줄만 바꾼다. */
    private static void write(Snapshot s) {
        getMySQL().preparedQuery("DELETE FROM " + table + " WHERE uuid=?", s.uuid().toString());
        getMySQL().preparedQuery("INSERT INTO " + table + " (uuid, names, inventories, data, lastActivePet) VALUES (?, ?, ?, ?, ?)",
                s.uuid().toString(), s.names(), s.inventories(), s.data(), s.lastActivePet());
    }

    /** DB 스레드에 넘기고 끝날 때까지 기다린다 (서버 종료·리로드용). */
    private static void writeAndWait(List<Snapshot> snapshots) {
        if (snapshots.isEmpty()) return;
        try {
            EXECUTOR.submit(() -> snapshots.forEach(Databases::write)).get(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "[Database] 저장을 끝내지 못했습니다", e);
        }
    }

    /**
     * 에고버스: 메모리에 있는 모든 플레이어를 저장하고 끝날 때까지 기다린다 (서버 종료, /펫 리로드).
     * 원본은 테이블을 TRUNCATE 한 뒤 다시 넣어서, 그 사이 다른 저장과 겹치면 데이터가 사라질 수 있었다.
     */
    public static void saveData() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        List<Snapshot> snapshots = new ArrayList<>();
        for (UUID uuid : new ArrayList<>(PlayerData.getRegisteredData().keySet())) {
            if (loading(uuid)) continue; // 아직 불러오는 중: DB 에 있는 쪽이 최신
            Snapshot s = snapshot(uuid);
            if (s != null) snapshots.add(s);
        }
        writeAndWait(snapshots);
    }

    /** 한 명을 저장하고 끝날 때까지 기다린다. 평소에는 {@link #savePlayerDataAsync} 를 쓴다. */
    public static void savePlayerData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        if (loading(playerUUID)) return;
        Snapshot s = snapshot(playerUUID);
        if (s != null) writeAndWait(List.of(s));
    }

    /** 한 명을 저장한다: 내용은 지금 만들고, DB 쓰기는 DB 스레드에서 순서대로. */
    public static void savePlayerDataAsync(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        // 접속 직후 아직 불러오는 중이면, 비어 있는 데이터로 DB 를 덮지 않게 불러오기가 끝난 뒤에 저장한다.
        if (loading(playerUUID)) {
            LOADS.get(playerUUID).thenRun(() -> {
                if (!MCPets.getInstance().isEnabled()) return;
                if (org.bukkit.Bukkit.isPrimaryThread()) savePlayerDataAsync(playerUUID);
                else org.bukkit.Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> savePlayerDataAsync(playerUUID));
            });
            return;
        }
        Snapshot s = snapshot(playerUUID);
        if (s != null) EXECUTOR.execute(() -> write(s));
    }

    // -------------------------------------------------------------------------
    // Active-pet persistence for Velocity cross-server switching
    // -------------------------------------------------------------------------

    /**
     * Upsert the active pets for a player into the dedicated table.
     * Each entry is stored as "petId:skinPathId" (or just "petId" if no skin),
     * comma-delimited in the pet_id column.
     */
    public static void saveActivePet(UUID uuid, List<String> petIds, Map<String, String> skinIds) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        final List<String> entries = new ArrayList<>();
        for (String petId : petIds) {
            String skinId = skinIds != null ? skinIds.get(petId) : null;
            entries.add(skinId != null ? petId + SKIN_DELIMITER + skinId : petId);
        }
        String joined = String.join(PET_ID_DELIMITER, entries);
        long now = System.currentTimeMillis();
        getMySQL().preparedQuery(
                "INSERT INTO " + activeTable + " (uuid, pet_id, updated_at) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE pet_id=?, updated_at=?",
                uuid.toString(), joined, now, joined, now);
    }

    /**
     * Load the active-pet record for a player, or null if none exists.
     * Called on join to check whether the player arrived via a cross-server switch.
     */
    public static ActivePetRecord loadActivePet(UUID uuid) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return null;
        ResultSet rs = getMySQL().preparedQuery(
                "SELECT pet_id, updated_at FROM " + activeTable + " WHERE uuid=?",
                uuid.toString());
        if (rs == null) return null;
        try {
            if (rs.next()) {
                String raw = rs.getString("pet_id");
                if (raw == null) return null;
                List<String> ids = new ArrayList<>();
                Map<String, String> skinIds = new HashMap<>();
                for (String entry : raw.split(PET_ID_DELIMITER)) {
                    entry = entry.trim();
                    if (entry.isEmpty()) continue;
                    if (entry.contains(SKIN_DELIMITER)) {
                        String[] parts = entry.split(SKIN_DELIMITER, 2);
                        ids.add(parts[0]);
                        skinIds.put(parts[0], parts[1]);
                    } else {
                        ids.add(entry);
                    }
                }
                return new ActivePetRecord(ids, skinIds, rs.getLong("updated_at"));
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to load active pet record for " + uuid, e);
        }
        return null;
    }

    /**
     * Delete the active-pet record for a player once it has been consumed
     * (pet spawned on destination) or when it is no longer needed.
     */
    public static void clearActivePet(UUID uuid) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        getMySQL().preparedQuery("DELETE FROM " + activeTable + " WHERE uuid=?", uuid.toString());
    }

    public static void closeConnection() {
        // 남은 DB 작업을 끝내고 스레드를 멈춘다.
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        mySQL.close();
    }

    private static String buildStringSerialized(Map<String,String> map) {
        String builder = "";
        for (String id : map.keySet()) {
            String seria = map.get(id);
            String seriaId = id + ";;" + seria;
            if (builder.isBlank())
                builder = seriaId;
            else
                builder = builder + ";;;" + seriaId;
        }
        return builder;
    }

    public static ConcurrentHashMap<String, String> unserializeData(ResultSet resultSet, String targetedColumn) throws SQLException {
        String targetedResults = resultSet.getString(targetedColumn);
        ConcurrentHashMap<String, String> outputMap = new ConcurrentHashMap<>();

        String[] seriaTable = targetedResults.split(";;;");

        for (String seriaContents : seriaTable) {
            // treats the case in which input is empty or wrongly formatted
            if (seriaContents == null || !seriaContents.contains(";;"))
                continue;

            String[] seriaData = seriaContents.split(";;");
            try {
                String pet_id = seriaData[0];
                String content = seriaData[1];
                outputMap.put(pet_id, content);
            }
            catch (IndexOutOfBoundsException ex) {
                MCPets.getInstance().getLogger().log(Level.SEVERE, "[Database] Index out of bound for deserialization: " + seriaContents, ex);
            }
        }

        return outputMap;
    }
}
