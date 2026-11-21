package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.commands.CCommand;
import fr.nocsy.mcpets.commands.tabcompleters.MCPetsCommandTabCompleter;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MCPetsCommand implements CCommand {

    @Override
    public String getName() {
        return "mcpets";
    }

    @Override
    public String getPermission() {
        return PPermission.USE.getPermission();
    }

    @Override
    public TabCompleter getCompleter() {
        return new MCPetsCommandTabCompleter();
    }

    public String getAdminPermission() {
        return PPermission.ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(getPermission())) {

            if (sender instanceof Player && args.length == 0) {
                // 에고버스: Kokonut_UI 가 등록돼 있으면 그 화면으로 연다.
                fr.nocsy.mcpets.api.MCPetsAPI.openMenu((Player) sender);
                return;
            }

            AArgument[] outcomes = {
                    new ArgumentCategory(sender, args),
                    new ArgumentClearStats(sender, args),
                    new ArgumentDebug(sender, args),
                    new ArgumentItem(sender, args),
                    new ArgumentInventory(sender, args),
                    new ArgumentMount(sender, args),
                    new ArgumentMounts(sender, args),
                    new ArgumentMenu(sender, args),
                    new ArgumentName(sender, args),
                    new ArgumentOpen(sender, args),
                    new ArgumentPetFood(sender, args),
                    new ArgumentReload(sender, args),
                    new ArgumentRevoke(sender, args),
                    new ArgumentSignalStick(sender, args),
                    new ArgumentSpawn(sender, args),
                    new ArgumentEditor(sender, args),
            };

            for(AArgument argument : outcomes) {
                if(argument.conditionsVerified()) {
                    argument.commandEffect();
                    return;
                }
            }

            // Check if the argument name matched but with wrong number of args
            for(AArgument argument : outcomes) {
                if(argument.nameMatchesButIncomplete()) {
                    if (argument.getUsage() != null) {
                        fr.nocsy.mcpets.utils.Chat.send(sender, "§c인자가 부족해요. 사용법: §e" + argument.getUsage());
                    } else {
                        Language.USAGE.sendMessage(sender);
                    }
                    return;
                }
            }

            if (sender.hasPermission(getAdminPermission()))
                Language.USAGE.sendMessage(sender);
        }
        else {
            Language.NO_PERM.sendMessage(sender);
        }
    }
}
