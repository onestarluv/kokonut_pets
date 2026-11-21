package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.editor.*;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.utils.PetMath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditorConversationListener implements Listener {

    public void syncOpenEditor(Player p, EditorState newState) {
        final UUID uuid = p.getUniqueId();
        // Run it sync otherwise it will not open
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null)
                    return;
                Editor editor = Editor.getEditor(player);
                if (newState != null)
                    editor.setState(newState);
                editor.openEditor();
            }
        }.runTask(MCPets.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listen_conversation(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        EditorConversation conversation = EditorConversation.getConversation(p);

        if (conversation == null)
            return;

        e.setCancelled(true);

        String entry = e.getMessage();

        // If they wanna abandon the process
        if (entry.equalsIgnoreCase("quit")) {
            conversation.quit();
            syncOpenEditor(p, null);
            return;
        }

        // If the output type isn't respected, ask for another one.
        if (!conversation.entryMatch(entry)) {
            if (conversation.getEditorItem().getType().equals(EditorExpectationType.MYTHICMOB)) {
                fr.nocsy.mcpets.utils.Chat.send(p, "§cMythicMob §e" + entry + "§c 이(가) 없어요. MythicMobs 몹 파일에서 올바른 ID를 확인해 주세요.");
            }
            else if (conversation.getEditorItem().getType().equals(EditorExpectationType.SKILL)) {
                fr.nocsy.mcpets.utils.Chat.send(p, "§c스킬 §e" + entry + "§c 이(가) 없어요. MythicMobs 스킬 파일에서 올바른 ID를 확인해 주세요.");
            }
            else if (conversation.getEditorItem().getType().equals(EditorExpectationType.PET_CREATE)) {
                fr.nocsy.mcpets.utils.Chat.send(p, "§c펫 §e" + entry + "§c 은(는) 이미 있어요. 다른 ID를 써 주세요.");
            }
            else {
                fr.nocsy.mcpets.utils.Chat.send(p, "§c이 항목에 넣을 값의 형식: §6" + conversation.getEditorItem().getType().getName().replace("_", " "));
                fr.nocsy.mcpets.utils.Chat.send(p, "§c알맞은 형식으로 다시 입력해 주세요.");
                if (conversation.getEditorItem().getType().equals(EditorExpectationType.ITEM_ID_OR_MATERIAL)) {
                    fr.nocsy.mcpets.utils.Chat.send(p, "§b사용할 수 있는 아이템 ID:");
                    fr.nocsy.mcpets.utils.Chat.send(p, "§7" + ItemsListConfig.getInstance().getItems().keySet());
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.PET_ID) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE)) {
                    fr.nocsy.mcpets.utils.Chat.send(p, "§b사용할 수 있는 펫 ID:");
                    fr.nocsy.mcpets.utils.Chat.send(p, "§7" + Pet.getObjectPets().stream().map(Pet::getId).toList());
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.OPERATOR_TYPE) ) {
                    fr.nocsy.mcpets.utils.Chat.send(p, "§b사용할 수 있는 연산:");
                    fr.nocsy.mcpets.utils.Chat.send(p, "§7" + Arrays.toString(PetMath.values()));
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.MOUNT_TYPE) ) {
                    fr.nocsy.mcpets.utils.Chat.send(p, "§b사용할 수 있는 탈것 종류:");
                    fr.nocsy.mcpets.utils.Chat.send(p, "§7[walking, flying]");
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.ANNOUNCEMENT_TYPE)) {
                    fr.nocsy.mcpets.utils.Chat.send(p, "§b사용할 수 있는 알림 방식:");
                    fr.nocsy.mcpets.utils.Chat.send(p, "§7" + Arrays.toString(PetAnnouncement.values()));
                }
                else if (conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_TYPE)) {
                    fr.nocsy.mcpets.utils.Chat.send(p, "§b사용할 수 있는 먹이 종류:");
                    fr.nocsy.mcpets.utils.Chat.send(p, "§7" + Arrays.toString(PetFoodType.values()));
                }
            }
            return;
        }

        // This time we have the right value, let's parse it
        Object value = conversation.output(entry);

        // Fetch the edited field and save it
        EditorItems editorItem = conversation.getEditorItem();
        editorItem.setValue(value);
        editorItem.save(p);

        conversation.end();

        // If we just created a pet, we reload MCPets
        if (conversation.getEditorItem().getType().equals(EditorExpectationType.PET_CREATE)) {
            Pet pet = Pet.getFromId(value.toString());
            if (pet == null)
                return;
            EditorEditing editing = EditorEditing.get(p);
            editing.setPetId(pet.getId());
            syncOpenEditor(p, EditorState.PET_EDITOR_EDIT);
            return;
        }

        // Open back the editor
        syncOpenEditor(p, null);
    }
}
