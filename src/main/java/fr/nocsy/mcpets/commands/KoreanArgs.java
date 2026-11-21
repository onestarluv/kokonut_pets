package fr.nocsy.mcpets.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 에고버스: 한국어 하위 명령어. 입력은 원래 영어 인자로 바꿔 기존 처리 코드를 그대로 쓰고,
 * 탭 완성에는 한국어 이름만 보여 준다. 영어 인자도 계속 동작한다.
 */
public final class KoreanArgs {

    /** /펫 하위 명령어: 한국어 → 원래 인자. 순서 = 탭 완성 순서. */
    private static final Map<String, String> PET = new LinkedHashMap<>();
    /** /펫 아이템 의 두 번째 인자. */
    private static final Map<String, String> ITEM = new LinkedHashMap<>();
    /** /탈것 하위 명령어. */
    private static final Map<String, String> MOUNT = new LinkedHashMap<>();

    static {
        // 모든 플레이어
        PET.put("메뉴", "menu");
        PET.put("이름", "name");
        // 관리자
        PET.put("리로드", "reload");
        PET.put("편집기", "editor");
        PET.put("소환", "spawn");
        PET.put("열기", "open");
        PET.put("가방", "inventory");
        PET.put("회수", "revoke");
        PET.put("신호봉", "signalStick");
        PET.put("아이템", "item");
        PET.put("통계초기화", "clearStats");
        PET.put("먹이", "petFood");
        PET.put("카테고리", "category");
        PET.put("디버그", "debug");
        PET.put("탑승", "mount");
        PET.put("탈것", "mounts");

        ITEM.put("추가", "add");
        ITEM.put("삭제", "remove");
        ITEM.put("지급", "give");
        ITEM.put("목록", "list");

        MOUNT.put("메뉴", "menu");
    }

    /** 플레이어 누구나 쓰는 /펫 하위 명령어. */
    public static final List<String> PET_PLAYER = List.of("메뉴", "이름");
    /** 관리자 전용 /펫 하위 명령어. */
    public static final List<String> PET_ADMIN = List.of("리로드", "편집기", "소환", "열기", "가방", "회수", "신호봉",
            "아이템", "통계초기화", "먹이", "카테고리", "디버그");
    public static final List<String> ITEM_ACTIONS = List.copyOf(ITEM.keySet());
    public static final List<String> MOUNT_ARGS = List.copyOf(MOUNT.keySet());

    private KoreanArgs() {
    }

    /** /펫 인자를 원래 영어 인자로 바꾼 사본. */
    public static String[] pet(String[] args) {
        if (args.length == 0) return args;
        String[] out = args.clone();
        out[0] = PET.getOrDefault(args[0], args[0]);
        if (out.length > 1 && out[0].equalsIgnoreCase("item")) out[1] = ITEM.getOrDefault(args[1], args[1]);
        return out;
    }

    /** /탈것 인자를 원래 영어 인자로 바꾼 사본. */
    public static String[] mount(String[] args) {
        if (args.length == 0) return args;
        String[] out = args.clone();
        out[0] = MOUNT.getOrDefault(args[0], args[0]);
        return out;
    }

    /** 사용법 안내용: 원래 인자의 한국어 이름 (없으면 그대로). */
    public static String korean(String english) {
        for (Map.Entry<String, String> e : PET.entrySet()) if (e.getValue().equalsIgnoreCase(english)) return e.getKey();
        return english;
    }
}
