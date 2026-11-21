package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.sql.Databases;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * 에고버스: 자동 줍기 필터. 플레이어·펫마다 모드(일반 / 화이트리스트 / 블랙리스트)와 아이템 목록을 가진다.
 *
 * <p>Kokonut-Core DB 테이블 {@code <접두사>mcpets_pickup_filter} 에 저장하고, 접속할 때 비동기로 불러와 메모리에
 * 둔다. 자동 줍기({@link PetAutoPickup})는 메모리만 읽는다. 바꿀 때마다 그 한 줄만 비동기로 저장한다.
 *
 * <p>아이템 구분: Nexo 아이템은 {@code nexo:<id>}, 바닐라는 {@code minecraft:<material>}.
 */
public final class PickupFilters implements Listener {

    public enum Mode { NORMAL, WHITELIST, BLACKLIST }

    /** {@code icon}: 화면에 그릴 바닐라 아이템 ID, {@code name}: Nexo 아이템 이름(바닐라는 빈 문자열, 화면이 번역 키로 보여 줌). */
    public record Entry(String key, String icon, String name) { }

    public enum AddResult { ADDED, EMPTY_HAND, DUPLICATE, FULL }

    public static final int MAX_ENTRIES = 42;

    public static final class Filter {
        private volatile Mode mode = Mode.NORMAL;
        private final List<Entry> entries = new CopyOnWriteArrayList<>();

        public Mode mode() { return mode; }
        public List<Entry> entries() { return List.copyOf(entries); }

        boolean contains(final String key) {
            for (final Entry e : entries) if (e.key().equals(key)) return true;
            return false;
        }

        /** 이 아이템을 주워도 되는지. */
        public boolean allows(final ItemStack stack) {
            return switch (mode) {
                case NORMAL -> true;
                case WHITELIST -> contains(key(stack));
                case BLACKLIST -> !contains(key(stack));
            };
        }
    }

    private static final Map<UUID, Map<String, Filter>> CACHE = new ConcurrentHashMap<>();
    private static String table = "mcpets_pickup_filter";

    private PickupFilters() {
    }

    public static void start(final JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new PickupFilters(), plugin);
    }

    /** Databases.init 에서 Core DB 가 준비되면 호출. 테이블을 만들고 접속 중인 플레이어를 불러온다. */
    public static void initDatabase(final String prefix) {
        table = prefix + "mcpets_pickup_filter";
        Databases.getMySQL().query("CREATE TABLE IF NOT EXISTS " + table
                + " (uuid VARCHAR(36) NOT NULL, pet_id VARCHAR(191) NOT NULL, mode VARCHAR(16) NOT NULL,"
                + " items MEDIUMTEXT, PRIMARY KEY (uuid, pet_id))");
        for (final Player p : Bukkit.getOnlinePlayers()) load(p.getUniqueId());
    }

    /** 필터 (없으면 일반 모드로 만든다). 메인 스레드 안전. */
    public static Filter get(final UUID owner, final String petId) {
        return CACHE.computeIfAbsent(owner, k -> new ConcurrentHashMap<>()).computeIfAbsent(petId, k -> new Filter());
    }

    /** 있으면 필터, 없으면 null (일반 모드). 자동 줍기용. */
    public static Filter peek(final UUID owner, final String petId) {
        final Map<String, Filter> map = CACHE.get(owner);
        return map == null ? null : map.get(petId);
    }

    public static void setMode(final UUID owner, final String petId, final Mode mode) {
        get(owner, petId).mode = mode;
        save(owner, petId);
    }

    public static AddResult add(final UUID owner, final String petId, final ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return AddResult.EMPTY_HAND;
        final Filter f = get(owner, petId);
        final String key = key(stack);
        if (f.contains(key)) return AddResult.DUPLICATE;
        if (f.entries.size() >= MAX_ENTRIES) return AddResult.FULL;
        f.entries.add(entry(stack, key));
        save(owner, petId);
        return AddResult.ADDED;
    }

    private static Entry entry(final ItemStack stack, final String key) {
        String name = "";
        if (key.startsWith("nexo:") && stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName())
            name = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(stack.getItemMeta().displayName());
        return new Entry(key, stack.getType().getKey().getKey(), clean(name));
    }

    /** 고르기 화면의 한 칸: 아이템과 지금 목록에 있는지. */
    public record Candidate(Entry entry, boolean listed) { }

    /** 플레이어 인벤토리와 이 펫 가방에 있는 아이템을 종류별로 한 번씩 (인벤토리 순서, 최대 {@code limit}개). */
    public static List<Candidate> candidates(final Player player, final String petId, final int limit) {
        final Filter f = get(player.getUniqueId(), petId);
        final java.util.LinkedHashMap<String, ItemStack> found = new java.util.LinkedHashMap<>();
        for (final ItemStack it : player.getInventory().getContents()) collect(found, it);
        // 가방은 소환 중인 그 플레이어의 펫 개체에서만 읽는다 (공용 펫 원본에 주인을 넣지 않도록).
        for (final fr.nocsy.mcpets.data.Pet active : fr.nocsy.mcpets.data.Pet.getActivePetsForOwner(player.getUniqueId())) {
            if (!active.getId().equals(petId)) continue;
            final fr.nocsy.mcpets.data.inventories.PetInventory bag = fr.nocsy.mcpets.data.inventories.PetInventory.get(active);
            if (bag != null) for (final ItemStack it : bag.getInventory().getContents()) collect(found, it);
        }
        final List<Candidate> out = new ArrayList<>();
        for (final Map.Entry<String, ItemStack> e : found.entrySet()) {
            if (out.size() >= limit) break;
            out.add(new Candidate(entry(e.getValue(), e.getKey()), f.contains(e.getKey())));
        }
        return out;
    }

    private static void collect(final Map<String, ItemStack> found, final ItemStack it) {
        if (it == null || it.getType().isAir()) return;
        found.putIfAbsent(key(it), it);
    }

    /** 고르기 화면에서 누른 아이템: 목록에 있으면 빼고 없으면 넣는다. */
    public static AddResult toggle(final Player player, final String petId, final String key) {
        final Filter f = get(player.getUniqueId(), petId);
        if (f.contains(key)) {
            remove(player.getUniqueId(), petId, key);
            return AddResult.ADDED;
        }
        for (final Candidate c : candidates(player, petId, Integer.MAX_VALUE)) {
            if (!c.entry().key().equals(key)) continue;
            if (f.entries.size() >= MAX_ENTRIES) return AddResult.FULL;
            f.entries.add(c.entry());
            save(player.getUniqueId(), petId);
            return AddResult.ADDED;
        }
        return AddResult.EMPTY_HAND;
    }

    public static void remove(final UUID owner, final String petId, final String key) {
        get(owner, petId).entries.removeIf(e -> e.key().equals(key));
        save(owner, petId);
    }

    public static void clear(final UUID owner, final String petId) {
        get(owner, petId).entries.clear();
        save(owner, petId);
    }

    /** 아이템 구분 키: Nexo 아이템이면 nexo:id, 아니면 minecraft:material. */
    public static String key(final ItemStack stack) {
        if (MCPets.checkNexo()) {
            try {
                final String id = com.nexomc.nexo.api.NexoItems.idFromItem(stack);
                if (id != null) return "nexo:" + id;
            } catch (final RuntimeException | LinkageError ignored) {
                // Nexo 없음/버전 차이
            }
        }
        return stack.getType().getKey().toString();
    }

    private static String clean(final String s) {
        return s.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    // ---- DB ----

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final UUID id = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> load(id));
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        CACHE.remove(e.getPlayer().getUniqueId());
    }

    private static boolean db() {
        return GlobalConfig.getInstance().isDatabaseSupport() && Databases.getMySQL() != null;
    }

    /** 비동기 스레드에서 호출. */
    private static void load(final UUID owner) {
        if (!db()) return;
        final ResultSet rs = Databases.getMySQL().preparedQuery(
                "SELECT pet_id, mode, items FROM " + table + " WHERE uuid=?", owner.toString());
        if (rs == null) return;
        final Map<String, Filter> map = new ConcurrentHashMap<>();
        try {
            while (rs.next()) {
                final Filter f = new Filter();
                try {
                    f.mode = Mode.valueOf(rs.getString("mode"));
                } catch (final IllegalArgumentException | NullPointerException bad) {
                    f.mode = Mode.NORMAL;
                }
                final String items = rs.getString("items");
                if (items != null && !items.isEmpty()) {
                    for (final String line : items.split("\n")) {
                        final String[] p = line.split("\t", -1);
                        if (p.length >= 2) f.entries.add(new Entry(p[0], p[1], p.length > 2 ? p[2] : ""));
                    }
                }
                map.put(rs.getString("pet_id"), f);
            }
        } catch (final SQLException ex) {
            MCPets.getLog().log(Level.WARNING, "줍기 필터를 불러오지 못했습니다: " + owner, ex);
            return;
        }
        // 불러오는 동안 화면에서 바꾼 것이 있으면 그쪽을 남긴다.
        final Map<String, Filter> current = CACHE.computeIfAbsent(owner, k -> new ConcurrentHashMap<>());
        map.forEach(current::putIfAbsent);
    }

    private static void save(final UUID owner, final String petId) {
        if (!db()) return;
        final Filter f = get(owner, petId);
        final String mode = f.mode.name();
        final List<String> lines = new ArrayList<>();
        for (final Entry e : f.entries) lines.add(e.key() + "\t" + e.icon() + "\t" + e.name());
        final String items = String.join("\n", lines);
        Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () ->
                Databases.getMySQL().preparedQuery("INSERT INTO " + table + " (uuid, pet_id, mode, items) VALUES (?, ?, ?, ?)"
                        + " ON DUPLICATE KEY UPDATE mode=VALUES(mode), items=VALUES(items)",
                        owner.toString(), petId, mode, items));
    }

    /** 화면용: 바닐라 아이템 이름 번역 키 (모르면 null). */
    public static String translationKey(final String icon) {
        final Material m = Material.matchMaterial(icon == null ? "" : icon.toLowerCase(Locale.ROOT));
        return m == null ? null : m.translationKey();
    }
}
