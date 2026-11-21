package fr.nocsy.mcpets.data.config;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import fr.nocsy.mcpets.utils.Utils;

import net.kyori.adventure.text.Component;

public enum Language {

    INVENTORY_PETS_MENU("§0☀ §4펫 §0☀"),
    INVENTORY_PETS_MENU_INTERACTIONS("§0☀ §4펫 §0☀"),

    INVENTORY_MOUNTS_MENU("§0☀ §4탈것 §0☀"),
    INVENTORY_MOUNTS_MENU_INTERACTIONS("§0☀ §4탈것 §0☀"),

    MOUNT_ITEM_NAME("§6타기"),
    MOUNT_ITEM_DESCRIPTION("§7클릭하면 펫에 올라타요"),

    RENAME_ITEM_NAME("§6이름 바꾸기"),
    RENAME_ITEM_DESCRIPTION("§7클릭하면 펫 이름을 바꿔요"),

    BACK_TO_PETMENU_ITEM_NAME("§c메뉴로 돌아가기"),
    BACK_TO_PETMENU_ITEM_DESCRIPTION("§7클릭하면 메뉴로 돌아가요"),

    INVENTORY_ITEM_NAME("§6가방"),
    INVENTORY_ITEM_DESCRIPTION("§7클릭하면 펫 가방을 열어요"),

    SKINS_ITEM_NAME("§6스킨"),
    SKINS_ITEM_DESCRIPTION("§7클릭하면 펫 스킨을 바꿔요"),

    EQUIPMENT_ITEM_NAME("§6장비"),
    EQUIPMENT_DESCRIPTION("§7클릭하면 펫 장비를 열어요"),

    NEXT_PAGE_ITEM_NAME("§6다음 페이지 §7(§e%currentPage%§8/§7%maxPage%)"),
    NEXT_PAGE_ITEM_DESCRIPTION("§e클릭§7하면 다음 페이지로 가요"),

    PREVIOUS_PAGE_ITEM_NAME("§6이전 페이지 §7(§e%currentPage%§8/§7%maxPage%)"),
    PREVIOUS_PAGE_ITEM_DESCRIPTION("§e클릭§7하면 이전 페이지로 가요"),

    NICKNAME("§9이름: §7%nickname%"),
    NICKNAME_ITEM_LORE("§c클릭하면 펫을 회수해요"),

    SUMMONED("§7펫을 소환했어요!"),
    REVOKED("§7펫을 회수했어요."),
    REVOKED_FOR_NEW_ONE("§7새 펫을 소환하려고 이전 펫을 회수했어요."),
    REVOKED_UNKNOWN("§c펫을 소환하지 못했어요. 다음 중 하나가 원인일 수 있어요:" +
            "\n§7- 펫 설정에 적힌 §cMythicMob이 없어요§7 (/mm m spawn 으로 소환해 보세요)." +
            "\n§7- 월드 난이도가 §c평화로움 또는 쉬움§7이에요." +
            "\n§7- 이 §c구역에서 몹 소환이 막혀 있어요§7 (펫의 기반 몹이 적대적 몹일 수 있어요)." +
            "\n§7- §c스폰 보호 플러그인§7이 있어요. 다른 월드나 스폰에서 먼 곳에서 소환해 보세요." +
            "\n§7- §c같은 ID§7를 쓰는 펫이 또 있어요. ID가 겹치지 않게 해 주세요."),
    MYTHICMOB_NULL("§c펫을 소환하지 못했어요. 연결된 MythicMob 몹이나 파일이 없거나 지워졌어요."),
    NO_MOB_MATCH("§c펫을 소환하지 못했어요. 연결된 몹이 MythicMobs에 등록돼 있지 않아요."),
    NOT_ALLOWED("§c이 펫을 소환할 권한이 없어요."),
    OWNER_NOT_FOUND("§c펫을 소환하지 못했어요. 소환한 플레이어를 찾을 수 없어요."),
    REVOKED_BEFORE_CHANGES("§c변경 사항이 적용되기 전에 펫이 회수됐어요."),
    NOT_MOUNTABLE("§c이 펫은 탈 수 없어요."),
    ALREADY_MOUNTING("§c이미 무언가를 타고 있어요. 내린 뒤 다시 시도해 주세요."),
    NOT_MOUNTABLE_HERE("§c이 구역에서는 펫을 탈 수 없어요."),
    CANT_MOUNT_PET_YET("§c이 펫을 탈 권한이 없어요."),
    CANT_FOLLOW_HERE("§c이 구역에서는 펫이 따라올 수 없어요."),
    TYPE_NAME_IN_CHAT("§a채팅창에 펫 이름을 입력해 주세요."),
    IF_WISH_TO_REMOVE_NAME("§a이름을 지우려면 채팅창에 §c%tag%§a 라고 입력해 주세요."),
    NICKNAME_CHANGED_SUCCESSFULY("§a펫 이름을 바꿨어요!"),
    NICKNAME_NOT_CHANGED("§c이름이 비어 있어서 바꾸지 못했어요. 다시 입력해 주세요."),
    TAG_TO_REMOVE_NAME("없음"),
    ALREADY_INSIDE_VEHICULE("§7이미 무언가를 타고 있어요. 지금 탈것에서 내린 뒤 사용해 주세요."),
    PET_DOESNT_EXIST("§c없는 펫이에요. ID를 확인해 주세요."),
    PLAYER_NOT_CONNECTED("§c§6%player%§c 님은 접속해 있지 않아요."),
    BLACKLISTED_WORD("§c이름 변경을 취소했어요. %word% 은(는) 펫 이름에 쓸 수 없어요."),
    NO_ACTIVE_PET("§c소환한 펫이 없어요."),
    SPECIFY_PET("§c소환한 펫이 여러 마리예요. 어느 펫인지 골라 주세요: §e%pets%"),
    SIGNAL_STICK_GIVEN("§a명령 막대를 받았어요. 우클릭으로 명령을 내리고, 좌클릭으로 명령을 바꿔요."),
    SIGNAL_STICK_SIGNAL("§6현재 명령: §e%signal%"),
    LOOP_SPAWN("§c순간이동이 너무 잦아서 펫을 회수했어요."),
    REQUIRES_ITEM_IN_HAND("§c설정에 반영하려면 손에 아이템을 들고 있어야 해요."),
    ITEM_UPDATED("§a아이템을 바꿨어요. 키: §e%key%"),
    ITEM_DOESNT_EXIST("§c키 §e%key%§c 아이템이 없어요. 새로 추가하려면 §eadd§c 를 사용해 주세요."),
    KEY_DOESNT_EXIST("§c등록되지 않은 키예요."),
    KEY_REMOVED("§a키 아이템을 지웠어요."),
    KEY_ALREADY_EXISTS("§c이미 등록된 키예요. 이 키로 지금 아이템을 바꿀 수 있어요."),
    KEY_ADDED("§a키와 아이템을 추가했어요."),
    KEY_LIST("§a사용할 수 있는 키:"),

    RELOAD_SUCCESS("§a다시 불러왔어요."),
    HOW_MANY_PETS_LOADED("§a펫 %numberofpets%마리를 등록했어요."),


    USAGE("§c없는 명령어예요. §7/펫 으로 펫 메뉴를 열 수 있어요."),
    NO_PERM("§c이 명령어를 쓸 권한이 없어요."),
    BLACKLISTED_WORLD("§c이 월드에서는 펫을 쓸 수 없어요."),

    CATEGORY_MENU_TITLE("§0☀ §4펫 §8- 카테고리 선택 §0☀"),
    CATEGORY_DOESNT_EXIST("§c없는 카테고리예요."),

    PET_INVENTORY_TITLE("%pet%의 가방"),

    PET_INVENTORY_COULDNOT_OPEN("§c가방이 없어서 열 수 없어요."),

    PET_SKINS_TITLE("%pet%의 스킨"),

    SKIN_COULD_NOT_APPLY("§c펫에 스킨을 적용하지 못했어요."),
    SKIN_APPLIED("§a스킨을 바꿨어요!"),

    GLOBAL_RESPAWN_TIMER_RUNNING("§c아직 펫을 소환할 수 없어요. %timeLeft%초/%cooldown%초 기다려 주세요."),
    RESPAWN_TIMER_RUNNING("§c펫이 아직 회복 중이에요. %timeLeft%초/%cooldown%초 기다려 주세요."),
    REVOKE_TIMER_RUNNING("§c펫이 아직 회복 중이에요. %timeLeft%초/%cooldown%초 기다려 주세요."),

    PLAYER_OR_PET_DOESNT_EXIST("§c없는 펫이거나 서버에 접속한 적 없는 플레이어예요."),
    STATS_CLEARED("§a모든 성장 기록을 초기화했어요!"),
    STATS_CLEARED_FOR_PET_FOR_PLAYER("§a%player% 님의 %petId% 펫 성장 기록을 초기화했어요."),
    STATS_CLEARED_FOR_PET("§a%petId% 펫의 성장 기록을 모두 초기화했어요."),

    PET_TAMING_PROGRESS("§7길들이기 §a%progress%% §7- %progressbar%"),
    PET_COULD_NOT_EVOLVE("§7§c진화한 펫을 이미 갖고 있어서§7 진화하지 못했어요."),
    PETFOOD_DOESNT_EXIST("§c없는 펫 먹이예요."),
    PETUNLOCK_NOPERM("§c이 아이템으로 펫을 해금할 권한이 없어요."),
    PETUNLOCKED("§a%petName% 펫을 해금했어요. 축하해요!"),
    PETUNLOCKED_ALREADY("§c§6%petName%§c 펫은 이미 갖고 있어요."),

    PET_ALREADY_TAMED("§c이미 길들인 펫이에요."),
    PET_DOESNT_EAT("§c이 펫은 그 먹이를 먹을 수 없어요."),
    PET_FOOD_ON_COOLDOWN("§c%timeleft%초 뒤에 이 먹이를 다시 먹을 수 있어요."),

    PET_STATUS_ALIVE("§a소환 가능"),
    PET_STATUS_REVOKED("§c소환 불가 §7(%timeleft%초 남음)"),
    PET_STATUS_DEAD("§c쓰러짐 §7(%timeleft%초 남음)"),

    PET_STATS("§6✦ 펫 정보 ✦" +
            "\n§7상태: %status%" +
            "\n§6등급 §7- §6%levelname%" +
            "\n " +
            "\n§f%health%§7/§f%maxhealth% §c❤" +
            "\n§7체력 회복: %regeneration% ❤/초" +
            "\n§7피해 배율: §f%damagemodifier%%" +
            "\n§7저항 배율: §f%resistancemodifier%%" +
            "\n§7위력: §f%power%%" +
            "\n " +
            "\n§7경험치: §a%experience%/%threshold% xp" +
            "\n%progressbar%"),

    PET_STATS_EVOLUTION_ALREADY_OWNED("§c진화한 펫을 이미 갖고 있어요."),
    PET_STATS_MAX_LEVEL("§7최고 등급이에요."),
    MAX_ACTIVE_PETS_REACHED("§c더 이상 펫을 소환할 수 없어요. 소환 가능한 수를 모두 채웠어요!"),
    PET_REPLACED_BY_NEW("§e%oldpet% 대신 %newpet% 을(를) 소환했어요!"),
    DEBUGGER_JOINING("§a디버거를 켰어요. 이제 디버그 메시지를 받아요."),
    DEBUGGER_LEAVE("§a디버거를 §7껐어요§a. 더 이상 디버그 메시지를 받지 않아요.");

    private String message;

    Language(String message) {
        this.message = message;
    }

    public void reload() {
        if (LanguageConfig.getInstance().getMap().containsKey(name().toLowerCase())) {
            message = LanguageConfig.getInstance().getMap().get(name().toLowerCase());
        }
    }

    public String getMessage() {
        String m = message;

        m = Utils.applyPlaceholders(null, m);
        return m;
    }

    public String getMessagePAPI() {
        String m = message;

        m = Utils.applyPlaceholders(null, m);
        return m;
    }

    public Component getComponent() {
        return Utils.toComponent(getMessage());
    }

    public Component getComponentWithPrefix() {
        return Utils.toComponentWithPrefix(getMessage());
    }

    public void sendMessage(Player p) {
        if (message.isEmpty()) return;
        // 에고버스: Core MessageUtil (시스템 머리말은 Core가 붙인다)
        fr.nocsy.mcpets.utils.Chat.send(p, getComponent());
    }

    public void sendMessage(CommandSender sender) {
        if (message.isEmpty()) return;
        fr.nocsy.mcpets.utils.Chat.send(sender, getComponent());
    }

    public void sendMessageFormatted(CommandSender sender, FormatArg... args) {
        if (message.isEmpty()) return;

        fr.nocsy.mcpets.utils.Chat.send(sender, Utils.toComponent(getMessageFormatted(args)));
    }

    public String getMessageFormatted(FormatArg... args) {
        String toSend = getMessage();
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }

        return toSend;
    }

    public Component getComponentFormatted(FormatArg... args) {
        return Utils.toComponent(getMessageFormatted(args));
    }

}
