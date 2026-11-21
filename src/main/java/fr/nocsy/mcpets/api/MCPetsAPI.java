package fr.nocsy.mcpets.api;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MCPetsAPI {

    /**
     * Get plugin instance
     */
    public static MCPets getPluginInstance() {
        return MCPets.getInstance();
    }

    /**
     * Returns pet object instance | not an active pet instance
     */
    public static Pet getObjectPet(final String id) {
        return Pet.getFromId(id);
    }

    /**
     * Returns the first active pet for the player if they have one.
     * Returns null if no pet is attached to the player.
     * @deprecated Use getActivePetsForPlayer() for multiple pets support
     */
    @Deprecated
    public static Pet getActivePet(final UUID playerUUID) {
        final List<Pet> pets = Pet.getActivePets().get(playerUUID);
        return (pets != null && !pets.isEmpty()) ? pets.getFirst() : null;
    }

    /**
     * Returns all active pets for the player.
     */
    public static List<Pet> getActivePetsForPlayer(final UUID playerUUID) {
        return Pet.getActivePetsForOwner(playerUUID);
    }

    /**
     * Returns the map where Key=UuidPlayer | Value=List<Pet>
     * @deprecated API signature changed - returns Map<UUID, List<Pet>> now
     */
    @Deprecated
    public static Map<UUID, List<Pet>> getActivePets() {
        return Pet.getActivePets();
    }

    /**
     * Returns a list of object pet instance | not active pets
     */
    public static List<Pet> getObjectPets() {
        return Pet.getObjectPets();
    }

    /**
     * Get the list of pet that are available to the specified player (permission based)
     */
    public static List<Pet> getAvailablePets(final Player p) {
        return Pet.getAvailablePets(p);
    }

    /**
     * Set the active pet of the player
     * Returns a value giving what happened after calling the method
     */
    public static int setActivePet(final Pet pet, final Player p, final boolean checkPermission) {
        pet.setCheckPermission(checkPermission);
        return pet.spawn(p.getLocation(), true);
    }

    // ---- 에고버스: 외부 메뉴(Kokonut_UI) 연동 ----

    /** /펫·펫 우클릭을 대신 여는 화면: (플레이어, 처음 고를 펫 ID 또는 null). true 면 기본 상자 메뉴를 열지 않는다. */
    private static volatile java.util.function.BiPredicate<Player, String> menuOverride;

    /** 펫 화면을 다른 플러그인 화면으로 바꾼다. null 이면 기본 상자 메뉴로 되돌린다. */
    public static void setMenuOverride(final java.util.function.BiPredicate<Player, String> opener) {
        menuOverride = opener;
    }

    /** /펫 화면: 등록된 외부 화면이 열리면 그것, 아니면 기본 상자 메뉴. */
    public static void openMenu(final Player p) {
        if (!openOverride(p, null)) openDefaultMenu(p);
    }

    /** 외부 화면을 {@code petId} 펫을 고른 상태로 연다. 외부 화면이 없거나 못 열면 false. */
    public static boolean openOverride(final Player p, final String petId) {
        final java.util.function.BiPredicate<Player, String> opener = menuOverride;
        if (opener == null) return false;
        try {
            return opener.test(p, petId);
        } catch (RuntimeException | LinkageError e) {
            MCPets.getLog().warning("외부 펫 화면을 열지 못해 기본 메뉴를 엽니다: " + e);
            return false;
        }
    }

    /** 원래 MCPets 상자 메뉴. */
    public static void openDefaultMenu(final Player p) {
        new fr.nocsy.mcpets.data.inventories.PetMenu(p, 0).open(p);
    }

    /** 플레이어가 소환할 수 있는 펫의 화면용 정보 (MCPets 순서). 소환 중이면 그 개체의 능력치. */
    public static List<PetInfo> infos(final Player p) {
        fr.nocsy.mcpets.data.sql.PlayerData.get(p.getUniqueId());
        final List<Pet> active = getActivePetsForPlayer(p.getUniqueId());
        final List<PetInfo> out = new java.util.ArrayList<>();
        for (final Pet pet : getAvailablePets(p)) {
            final Pet live = active.stream().filter(a -> a.getId().equals(pet.getId())).findFirst().orElse(null);
            out.add(info(live != null ? live : pet, live != null));
        }
        return out;
    }

    private static PetInfo info(final Pet pet, final boolean active) {
        final ItemStack icon = pet.buildItem(pet.getIcon(), false);
        final org.bukkit.inventory.meta.ItemMeta meta = icon == null ? null : icon.getItemMeta();
        final net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer legacy =
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection();
        final String name = meta != null && meta.hasDisplayName() ? legacy.serialize(meta.displayName()) : pet.getId();
        final List<String> description = new java.util.ArrayList<>();
        if (meta != null && meta.lore() != null) for (final net.kyori.adventure.text.Component line : meta.lore()) description.add(legacy.serialize(line));
        final String nickname = active && pet.getCurrentName() != null && !pet.getCurrentName().isBlank() ? pet.getCurrentName() : null;

        final fr.nocsy.mcpets.data.livingpets.PetStats ps = pet.getPetStats();
        PetInfo.Status status = PetInfo.Status.READY;
        int timeLeft = 0;
        PetInfo.Stats stats = null;
        if (ps != null && ps.getCurrentLevel() != null) {
            if (ps.isRespawnTimerRunning()) {
                status = PetInfo.Status.DEAD;
                timeLeft = ps.getRespawnTimer().getRemainingTime();
            } else if (ps.isRevokeTimerRunning()) {
                status = PetInfo.Status.REVOKED;
                timeLeft = ps.getRevokeTimer().getRemainingTime();
            }
            final fr.nocsy.mcpets.data.livingpets.PetLevel level = ps.getCurrentLevel();
            final fr.nocsy.mcpets.data.livingpets.PetLevel next = ps.getNextLevel();
            final boolean max = next == null || next == level || next.getLevelId().equals(level.getLevelId());
            double health = ps.getCurrentHealth();
            if (health == 0 && ps.getRespawnTimer() != null && !ps.getRespawnTimer().isRunning()) health = ps.getRespawnHealth();
            stats = new PetInfo.Stats(plainLevel(level.getLevelName()), rarity(pet, level).name(), max ? null : plainLevel(next.getLevelName()), health, level.getMaxHealth(),
                    level.getRegeneration(), (int) Math.round(100 * (ps.getDamageModifier() - 1)),
                    (int) Math.round(100 * (ps.getResistanceModifier() - 1)), (int) Math.round(100 * (ps.getPower() - 1)),
                    ps.getExperience(), level.getExpThreshold(), max ? level.getExpThreshold() : next.getExpThreshold());
        }
        final fr.nocsy.mcpets.data.config.GlobalConfig global = fr.nocsy.mcpets.data.config.GlobalConfig.getInstance();
        final kr.gomsv.kokonutCore.api.item.ItemRarity petRarity = rarity(pet);
        return new PetInfo(pet.getId(), icon(pet), petRarity == null ? null : petRarity.name(), name, nickname, List.copyOf(description), active, status, timeLeft, stats, pickupRadius(pet),
                global.isNameable(), pet.hasSkins(), pet.getInventorySize() > 0, global.isMountable() && pet.isMountable(),
                pet.getSignals() != null && !pet.getSignals().isEmpty() && pet.isEnableSignalStickFromMenu());
    }

    /**
     * 펫 등급 → Core 공통 등급(물고기·장신구와 같은 색). 마지막 등급을 MYTHIC 에 맞춘다:
     * 등급이 5개면 UNCOMMON ~ MYTHIC, 6개면 COMMON ~ MYTHIC.
     */
    public static kr.gomsv.kokonutCore.api.item.ItemRarity rarity(final Pet pet, final fr.nocsy.mcpets.data.livingpets.PetLevel level) {
        final kr.gomsv.kokonutCore.api.item.ItemRarity fixed = speciesRarity(pet);
        if (fixed != null) return fixed;
        final kr.gomsv.kokonutCore.api.item.ItemRarity[] all = kr.gomsv.kokonutCore.api.item.ItemRarity.values();
        final List<fr.nocsy.mcpets.data.livingpets.PetLevel> levels = pet.getPetLevels();
        if (levels == null || levels.isEmpty()) return all[0];
        int index = levels.indexOf(level);
        if (index < 0) for (int i = 0; i < levels.size(); i++) if (levels.get(i).getLevelId().equals(level.getLevelId())) index = i;
        final int offset = Math.max(0, all.length - levels.size());
        return all[Math.clamp(Math.max(0, index) + offset, 0, all.length - 1)];
    }

    /** 펫 설정 {@code Rarity}(종류 고정 등급), 없거나 잘못된 이름이면 null. */
    public static kr.gomsv.kokonutCore.api.item.ItemRarity speciesRarity(final Pet pet) {
        final String name = pet.getRarity();
        if (name == null || name.isBlank()) return null;
        try {
            return kr.gomsv.kokonutCore.api.item.ItemRarity.valueOf(name.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (final IllegalArgumentException unknown) {
            return null;
        }
    }

    /** 펫의 등급: 종류 고정 등급, 없으면 레벨로 정한 등급, 레벨도 없으면 null. */
    public static kr.gomsv.kokonutCore.api.item.ItemRarity rarity(final Pet pet) {
        final kr.gomsv.kokonutCore.api.item.ItemRarity fixed = speciesRarity(pet);
        if (fixed != null) return fixed;
        final fr.nocsy.mcpets.data.livingpets.PetStats ps = pet.getPetStats();
        return ps == null || ps.getCurrentLevel() == null ? null : rarity(pet, ps.getCurrentLevel());
    }

    /** 자동 줍기 반경(블록): 레어 3칸부터 등급마다 1칸씩 (에픽 4, 레전더리 5, 미스틱 6). 그 아래 등급은 0. */
    public static int pickupRadius(final Pet pet) {
        final kr.gomsv.kokonutCore.api.item.ItemRarity r = rarity(pet);
        if (r == null) return 0;
        final int rare = kr.gomsv.kokonutCore.api.item.ItemRarity.RARE.ordinal();
        return r.ordinal() < rare ? 0 : 3 + (r.ordinal() - rare);
    }

    /** 등급 이름에서 색 코드를 뺀 글자 (색은 {@link #rarity} 로 정한다). */
    private static String plainLevel(final String name) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(fr.nocsy.mcpets.utils.Utils.toComponent(name));
    }

    /** MythicMob 기본 엔티티의 스폰알 (예: WOLF → wolf_spawn_egg). 모르면 egg. */
    private static String icon(final Pet pet) {
        try {
            final String type = io.lumine.mythic.bukkit.MythicBukkit.inst().getMobManager().getMythicMob(pet.getMythicMobName())
                    .map(io.lumine.mythic.api.mobs.MythicMob::getEntityTypeString).orElse(null);
            if (type != null) {
                final org.bukkit.Material egg = org.bukkit.Material.matchMaterial(type.toLowerCase(java.util.Locale.ROOT) + "_spawn_egg");
                if (egg != null) return egg.getKey().getKey();
            }
        } catch (final RuntimeException | LinkageError ignored) {
            // MythicMobs API mismatch: fall back
        }
        return "egg";
    }

    /** 펫을 소환하고 결과를 채팅으로 알린다 (기본 메뉴에서 클릭한 것과 같다). */
    public static void summon(final Player p, final Pet objectPet) {
        objectPet.copy().spawnWithMessage(p);
    }

    /** 플레이어의 소환 중인 펫, 없으면 null. 이후 이름 변경 등 채팅 흐름이 이 펫을 대상으로 하도록 표시한다. */
    private static Pet live(final Player p, final String petId) {
        final Pet active = getActivePetsForPlayer(p.getUniqueId()).stream()
                .filter(a -> a.getId().equals(petId)).findFirst().orElse(null);
        if (active != null) active.setLastInteractedWith(p);
        return active;
    }

    /** 소환 중인 펫을 회수한다. 없으면 false. */
    public static boolean revoke(final Player p, final String petId) {
        final Pet active = live(p, petId);
        if (active == null) return false;
        fr.nocsy.mcpets.listeners.PetInteractionMenuListener.revoke(p, active);
        return true;
    }

    /** 채팅으로 새 이름을 받는다 (안내 문구 포함). 소환 중이 아니면 false. */
    public static boolean rename(final Player p, final String petId) {
        if (live(p, petId) == null) return false;
        fr.nocsy.mcpets.listeners.PetInteractionMenuListener.changeName(p);
        return true;
    }

    /** 스킨 선택 화면(상자). 소환 중이 아니면 false. */
    public static boolean skins(final Player p, final String petId) {
        final Pet active = live(p, petId);
        if (active == null) return false;
        fr.nocsy.mcpets.listeners.PetInteractionMenuListener.skins(p, active);
        return true;
    }

    /** 펫 가방(상자). 소환 중이 아니면 false. */
    public static boolean inventory(final Player p, final String petId) {
        final Pet active = live(p, petId);
        if (active == null) return false;
        fr.nocsy.mcpets.listeners.PetInteractionMenuListener.inventory(p, active);
        return true;
    }

    /** 펫에 탄다. 소환 중이 아니면 false. */
    public static boolean mount(final Player p, final String petId) {
        final Pet active = live(p, petId);
        if (active == null) return false;
        fr.nocsy.mcpets.listeners.PetInteractionMenuListener.mount(p, active);
        return true;
    }

    /** 신호봉을 준다. 소환 중이 아니면 false. */
    public static boolean signalStick(final Player p, final String petId) {
        final Pet active = live(p, petId);
        if (active == null) return false;
        active.giveStickSignals(p);
        return true;
    }

    // ---- 에고버스: 자동 줍기 필터 ----

    /** 펫의 줍기 필터 (없으면 일반 모드로 만든다). */
    public static fr.nocsy.mcpets.data.livingpets.PickupFilters.Filter pickupFilter(final Player p, final String petId) {
        return fr.nocsy.mcpets.data.livingpets.PickupFilters.get(p.getUniqueId(), petId);
    }

    public static void setPickupMode(final Player p, final String petId, final fr.nocsy.mcpets.data.livingpets.PickupFilters.Mode mode) {
        fr.nocsy.mcpets.data.livingpets.PickupFilters.setMode(p.getUniqueId(), petId, mode);
    }

    /** 손에 든 아이템을 목록에 넣는다. */
    public static fr.nocsy.mcpets.data.livingpets.PickupFilters.AddResult addHeldPickupItem(final Player p, final String petId) {
        return fr.nocsy.mcpets.data.livingpets.PickupFilters.add(p.getUniqueId(), petId, p.getInventory().getItemInMainHand());
    }

    public static void removePickupItem(final Player p, final String petId, final String key) {
        fr.nocsy.mcpets.data.livingpets.PickupFilters.remove(p.getUniqueId(), petId, key);
    }

    public static void clearPickupItems(final Player p, final String petId) {
        fr.nocsy.mcpets.data.livingpets.PickupFilters.clear(p.getUniqueId(), petId);
    }

    /** 고르기 화면: 인벤토리·펫 가방의 아이템 종류와 목록 포함 여부. */
    public static List<fr.nocsy.mcpets.data.livingpets.PickupFilters.Candidate> pickupCandidates(final Player p, final String petId, final int limit) {
        return fr.nocsy.mcpets.data.livingpets.PickupFilters.candidates(p, petId, limit);
    }

    /** 고르기 화면에서 누른 아이템을 목록에 넣거나 뺀다. */
    public static fr.nocsy.mcpets.data.livingpets.PickupFilters.AddResult togglePickupItem(final Player p, final String petId, final String key) {
        return fr.nocsy.mcpets.data.livingpets.PickupFilters.toggle(p, petId, key);
    }
}
