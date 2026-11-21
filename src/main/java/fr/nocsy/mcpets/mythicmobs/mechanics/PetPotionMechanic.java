package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * 에고버스: 펫 힘 값에 비례하는 포션 효과. 무료 MythicMobs 는 {@code d=<pet.power>*100} 같은 계산을 못 해서 만든 것.
 * {@code petPotion{type=strength;level=2;perPower=100}} — 지속시간 = 힘 × perPower 틱.
 */
@MythicMechanic(name = "petPotion")
public class PetPotionMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final PotionEffectType type;
    private final int amplifier;
    private final int perPower;

    public PetPotionMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());
        MythicLineConfig config = event.getConfig();
        String name = config.getString(new String[]{"type", "t"}, "strength").toLowerCase(java.util.Locale.ROOT);
        type = Registry.EFFECT.get(NamespacedKey.minecraft(name));
        amplifier = Math.max(0, config.getInteger(new String[]{"level", "lvl", "l"}, 1) - 1);
        perPower = Math.max(1, config.getInteger(new String[]{"perPower", "pp"}, 100));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (type == null) return SkillResult.INVALID_CONFIG;
        Entity caster = BukkitAdapter.adapt(data.getCaster().getEntity());
        Pet pet = Pet.getFromEntity(caster);
        if (pet == null || pet.getPetStats() == null) return SkillResult.CONDITION_FAILED;
        if (!(BukkitAdapter.adapt(target) instanceof LivingEntity living)) return SkillResult.CONDITION_FAILED;
        int ticks = (int) Math.max(1, Math.round(pet.getPetStats().getPower() * perPower));
        living.addPotionEffect(new PotionEffect(type, ticks, amplifier, false, false));
        return SkillResult.SUCCESS;
    }
}
