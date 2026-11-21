package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

/**
 * 에고버스: 무료 MythicMobs 는 숫자 자리의 {@code <pet.power>} 를 못 읽는다. 대신 이 메커닉이 스킬을 펫의 힘 값만큼
 * 반복한다. {@code petPowerSkill{s=스킬;interval=3}} — interval 틱 간격, 최소 1번, 최대 {@code max}(기본 10)번.
 */
@MythicMechanic(name = "petPowerSkill")
public class PetPowerSkillMechanic extends SkillMechanic implements INoTargetSkill {

    private final String skill;
    private final int interval;
    private final int max;

    public PetPowerSkillMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());
        MythicLineConfig config = event.getConfig();
        skill = config.getString(new String[]{"skill", "s"}, null);
        interval = Math.max(0, config.getInteger(new String[]{"interval", "i"}, 0));
        max = Math.max(1, config.getInteger(new String[]{"max", "m"}, 10));
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        if (skill == null) return SkillResult.INVALID_CONFIG;
        Entity caster = BukkitAdapter.adapt(data.getCaster().getEntity());
        Pet pet = Pet.getFromEntity(caster);
        if (pet == null || pet.getPetStats() == null || !pet.isStillHere()) return SkillResult.CONDITION_FAILED;
        var mythicSkill = MCPets.getMythicMobs().getSkillManager().getSkill(skill).orElse(null);
        if (mythicSkill == null) return SkillResult.INVALID_CONFIG;

        int times = (int) Math.clamp(Math.round(pet.getPetStats().getPower()), 1, max);
        for (int i = 0; i < times; i++) {
            Runnable run = () -> {
                if (!pet.isStillHere()) return;
                mythicSkill.execute(new SkillMetadataImpl(SkillTriggers.CUSTOM, pet.getActiveMob(), pet.getActiveMob().getEntity()));
            };
            if (i == 0 || interval == 0) run.run();
            else Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), run, (long) i * interval);
        }
        return SkillResult.SUCCESS;
    }
}
