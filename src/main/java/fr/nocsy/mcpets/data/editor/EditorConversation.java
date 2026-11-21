package fr.nocsy.mcpets.data.editor;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorConversation {

    private static HashMap<UUID, EditorConversation> conversations = new HashMap<>();

    @Getter
    private Player player;

    @Getter
    private EditorItems editorItem;

    public EditorConversation(Player p, EditorItems editorItem) {
        this.player = p;
        this.editorItem = editorItem;
    }

    public static EditorConversation getConversation(Player p) {
        return conversations.get(p.getUniqueId());
    }

    public void start() {
        conversations.put(player.getUniqueId(), this);
        fr.nocsy.mcpets.utils.Chat.send(player, "§a채팅창에 \"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\" 항목에 넣을 값을 입력해 주세요.");

        if (editorItem.getType().equals(EditorExpectationType.STRING_LIST)) {
            fr.nocsy.mcpets.utils.Chat.send(player, "§e여러 값은 쉼표 §c,§e 로 구분해요. 예: §6SPELL,SHIELD");
        }
        fr.nocsy.mcpets.utils.Chat.send(player, "§a값을 바꾸지 않으려면 §cQuit§a 라고 입력해 주세요.");
    }

    public boolean entryMatch(String entry) {
        return editorItem.getType().matches(entry);
    }

    public Object output(String entry) {
        return editorItem.getType().parse(entry);
    }

    public void quit() {
        conversations.remove(player.getUniqueId());
        fr.nocsy.mcpets.utils.Chat.send(player, "§c값을 바꾸지 않았어요.");
    }

    public void end() {
        conversations.remove(player.getUniqueId());
        fr.nocsy.mcpets.utils.Chat.send(player, "§a\"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\" 값을 바꿨어요!");
        fr.nocsy.mcpets.utils.Chat.send(player, "§a변경 사항을 적용하려면 펫 플러그인을 §n다시 불러와§a 주세요.");
    }

    /**
     * Clear all active editor conversations.
     * Called on plugin disable to prevent {@link IllegalStateException} (zip file closed)
     * that occurs when a listener tries to load a class from an already unloaded JAR.
     */
    public static void clearAll() {
        for (EditorConversation conversation : conversations.values()) {
            if (conversation.getPlayer() != null && conversation.getPlayer().isOnline()) {
                fr.nocsy.mcpets.utils.Chat.send(conversation.getPlayer(), "§c플러그인을 다시 불러와서 편집 입력이 취소됐어요.");
            }
        }
        conversations.clear();
    }
}
