package fr.nocsy.mcpets.commands.tabcompleters;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MCPetsCommandTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender,
                                                @NotNull Command cmd,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        ArrayList<String> completed = new ArrayList<>();
        // 에고버스: 한국어 하위 명령어를 원래 인자로 바꿔 판단하고, 보여 주는 이름은 한국어.
        final String[] typed = args;
        args = fr.nocsy.mcpets.commands.KoreanArgs.pet(args);

        if (cmd.getName().equalsIgnoreCase("mcpets")) {

            if (commandSender instanceof Player) {

                Player p = ((Player)commandSender);

                // User-facing commands available to all players
                if (args.length == 1) {
                    completed.addAll(fr.nocsy.mcpets.commands.KoreanArgs.PET_PLAYER);
                }
                else if (args.length == 2 && args[0].equalsIgnoreCase("menu")) {
                    completed.addAll(Pet.getActivePetsForOwner(p.getUniqueId())
                            .stream()
                            .filter(pet -> !pet.isMountable())
                            .map(Pet::getId)
                            .collect(Collectors.toList()));
                }

                if (!p.hasPermission(PPermission.ADMIN.getPermission()))
                    return completed.stream()
                            .filter(s -> s.toLowerCase().startsWith(typed[typed.length - 1].toLowerCase()))
                            .sorted()
                            .collect(Collectors.toList());

                if (args.length == 1) {
                    completed.addAll(fr.nocsy.mcpets.commands.KoreanArgs.PET_ADMIN);
                }

                else if (args.length == 2) {
                    switch (args[0].toLowerCase()) {
                        case "spawn", "signalstick":
                            completed.addAll(Pet.getObjectPets().stream().map(Pet::getId).toList());
                            break;
                        case "open": case "inventory", "revoke":
                            completed.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                            break;
                        case "item":
                            completed.addAll(ItemsListConfig.getInstance().listKeys());
                            completed.addAll(fr.nocsy.mcpets.commands.KoreanArgs.ITEM_ACTIONS);
                            break;
                        case "clearstats":
                            completed.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                            completed.addAll(Pet.getObjectPets().stream().map(Pet::getId).toList());
                            break;
                        case "petfood":
                            completed.addAll(PetFoodConfig.getInstance().list().stream().map(PetFood::getId).toList());
                            break;
                        case "category":
                            completed.addAll(Category.getCategories().stream().map(Category::getId).toList());
                            break;
                        default: {}
                    }
                }

                else if (args.length == 3) {
                    switch (args[0].toLowerCase()) {
                        case "spawn", "category":
                            completed.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                            break;
                        case "signalstick", "clearstats":
                            completed.addAll(Pet.getObjectPets().stream().map(Pet::getId).toList());
                            break;
                        case "inventory":
                            completed.addAll(Pet.getObjectPets().stream()
                                    .filter(pet -> pet.getInventorySize() > 0)
                                    .map(Pet::getId)
                                    .toList());
                            break;
                        case "item":
                            if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("remove"))
                                completed.addAll(ItemsListConfig.getInstance().listKeys());
                            break;
                        default: {}
                    }
                }

                else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("spawn")) {
                        completed.add("true");
                        completed.add("false");
                    }
                }

                else if (args.length == 5) {
                    if (args[0].equalsIgnoreCase("spawn")) {
                        completed.add("-s");
                    }
                }
            }
        }

        Collections.sort(completed);

        // Filter results based on what the player has typed so far
        String partial = typed[typed.length - 1].toLowerCase();
        return completed.stream()
                .filter(s -> s.toLowerCase().startsWith(partial))
                .collect(Collectors.toList());
    }
}
