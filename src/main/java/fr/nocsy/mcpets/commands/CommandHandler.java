package fr.nocsy.mcpets.commands;

import fr.nocsy.mcpets.commands.mcpets.MCPetsCommand;
import fr.nocsy.mcpets.commands.mounts.MountsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class CommandHandler implements CommandExecutor {

    public static ArrayList<CCommand> commands = new ArrayList<>();

    public static void init(JavaPlugin plugin) {
        commands.clear();
        commands.add(new MCPetsCommand());
        commands.add(new MountsCommand());
        for (CCommand c : commands) {
            plugin.getCommand(c.getName()).setExecutor(new CommandHandler());
            plugin.getCommand(c.getName()).setTabCompleter(c.getCompleter());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (CCommand cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(command.getName())) {
                // 에고버스: 한국어 하위 명령어를 원래 인자로 바꿔 넘긴다.
                String[] normalized = cmd instanceof MountsCommand ? KoreanArgs.mount(args) : KoreanArgs.pet(args);
                cmd.execute(sender, command, label, normalized);
            }
        }

        return true;
    }
}
