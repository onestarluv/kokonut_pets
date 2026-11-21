package fr.nocsy.mcpets.utils;

import kr.gomsv.kokonutCore.api.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

/**
 * 에고버스: 펫 플러그인의 모든 채팅 안내는 Kokonut-Core {@link MessageUtil} 로 보낸다
 * (Core 시스템 머리말·클라이언트 시스템 채널). 펫 자체 접두사는 붙이지 않는다.
 */
public final class Chat {
    private Chat() {
    }

    /** § 색 코드 문자열. 빈 문자열은 보내지 않는다. */
    public static void send(final CommandSender recipient, final String message) {
        if (recipient == null || message == null || message.isEmpty()) return;
        MessageUtil.send(recipient, message);
    }

    public static void send(final CommandSender recipient, final Component message) {
        if (recipient == null || message == null) return;
        MessageUtil.send(recipient, message);
    }
}
