package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.sql.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 에고버스: 레어 이상 펫의 자동 줍기. 1초(20틱)마다 소환 중인 펫만 돌며, 펫 주변 반경
 * ({@link MCPetsAPI#pickupRadius}: 레어 3 ~ 미스틱 6칸) 안의 떨어진 아이템을 펫 가방에 넣는다.
 *
 * <p>렉 방지: 서버 전체 엔티티가 아니라 펫마다 {@code getNearbyEntitiesByType}(주변 청크만)으로 찾고,
 * 펫 하나가 한 번에 처리하는 아이템 수를 제한하며, 가방이 바뀐 펫만 한 번 직렬화한다.
 *
 * <p>줍지 않는 것: 다른 플레이어가 버린 아이템(주인이 버린 것은 3초 뒤 줍는다), 다른 사람 소유 아이템, 아직 주울 수 없는 아이템,
 * 최근 플레이어가 죽은 자리 근처의 아이템(남의 사망 아이템 보호).
 */
public final class PetAutoPickup implements Listener {

    private static final long PERIOD_TICKS = 20L;
    /** 펫 하나가 1초에 처리하는 최대 아이템 엔티티 수. */
    private static final int MAX_ITEMS_PER_PET = 16;
    /** 주인이 직접 버린 아이템을 줍기까지 기다리는 시간(틱). */
    private static final int OWN_DROP_DELAY_TICKS = 60;
    /** 사망 지점 보호 반경(블록)과 시간(ms). */
    private static final double DEATH_RADIUS = 12;
    private static final long DEATH_PROTECT_MS = 5 * 60 * 1000L;

    private record Death(UUID world, double x, double y, double z, long at) { }

    private final Deque<Death> deaths = new ArrayDeque<>();

    private PetAutoPickup() {
    }

    public static void start(final JavaPlugin plugin) {
        final PetAutoPickup task = new PetAutoPickup();
        Bukkit.getPluginManager().registerEvents(task, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, task::tick, PERIOD_TICKS, PERIOD_TICKS);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(final PlayerDeathEvent e) {
        final Location l = e.getPlayer().getLocation();
        deaths.addLast(new Death(l.getWorld().getUID(), l.getX(), l.getY(), l.getZ(), System.currentTimeMillis()));
    }

    private void tick() {
        final long now = System.currentTimeMillis();
        while (!deaths.isEmpty() && now - deaths.peekFirst().at() > DEATH_PROTECT_MS) deaths.pollFirst();

        for (final Map.Entry<UUID, List<Pet>> entry : new ArrayList<>(Pet.getActivePets().entrySet())) {
            final Player owner = Bukkit.getPlayer(entry.getKey());
            if (owner == null) continue;
            for (final Pet pet : new ArrayList<>(entry.getValue())) {
                try {
                    collect(owner, pet);
                } catch (final RuntimeException ex) {
                    Bukkit.getLogger().warning("[Kokonut_Pets] 자동 줍기 실패 (" + pet.getId() + "): " + ex);
                }
            }
        }
    }

    private void collect(final Player owner, final Pet pet) {
        if (!pet.isStillHere()) return;
        final int radius = MCPetsAPI.pickupRadius(pet);
        if (radius <= 0) return;
        final Entity body = pet.getActiveMob().getEntity().getBukkitEntity();
        final Location center = body.getLocation();
        final World world = center.getWorld();
        if (world == null) return;

        final double r2 = (double) radius * radius;
        final PickupFilters.Filter filter = PickupFilters.peek(owner.getUniqueId(), pet.getId());
        PetInventory bag = null;
        boolean changed = false;
        int handled = 0;
        for (final Item item : world.getNearbyEntitiesByType(Item.class, center, radius, radius, radius)) {
            if (handled >= MAX_ITEMS_PER_PET) break;
            if (!item.isValid() || item.getPickupDelay() > 0) continue;
            // 남이 버린 아이템은 줍지 않고, 주인이 버린 것은 3초 뒤부터 줍는다.
            final UUID thrower = item.getThrower();
            if (thrower != null && (!thrower.equals(owner.getUniqueId()) || item.getTicksLived() < OWN_DROP_DELAY_TICKS)) continue;
            if (item.getOwner() != null && !item.getOwner().equals(owner.getUniqueId())) continue;
            final Location at = item.getLocation();
            if (at.distanceSquared(center) > r2 || nearDeath(world, at)) continue;
            if (filter != null && !filter.allows(item.getItemStack())) continue;
            handled++;

            if (bag == null) {
                bag = PetInventory.get(pet);
                if (bag == null) return;
            }
            final ItemStack stack = item.getItemStack();
            int left = 0;
            for (final ItemStack rest : bag.getInventory().addItem(stack.clone()).values()) left += rest.getAmount();
            final int taken = stack.getAmount() - left;
            if (taken <= 0) break; // 가방이 가득 참
            changed = true;
            if (body instanceof LivingEntity living) living.playPickupItemAnimation(item, taken);
            if (left == 0) item.remove();
            else {
                stack.setAmount(left);
                item.setItemStack(stack);
            }
        }
        if (changed) PlayerData.get(owner.getUniqueId()).setPetInventory(bag);
    }

    private boolean nearDeath(final World world, final Location at) {
        if (deaths.isEmpty()) return false;
        final UUID id = world.getUID();
        final double r2 = DEATH_RADIUS * DEATH_RADIUS;
        for (final Death d : deaths) {
            if (!d.world().equals(id)) continue;
            final double dx = d.x() - at.getX(), dy = d.y() - at.getY(), dz = d.z() - at.getZ();
            if (dx * dx + dy * dy + dz * dz <= r2) return true;
        }
        return false;
    }
}
