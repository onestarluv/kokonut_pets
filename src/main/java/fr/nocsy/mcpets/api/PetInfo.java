package fr.nocsy.mcpets.api;

import java.util.List;

/**
 * 에고버스: 외부 화면(Kokonut_UI)이 그릴 펫 한 마리의 스냅샷. 문자열은 § 색 코드를 포함할 수 있다.
 *
 * @param id          펫 ID
 * @param icon        아이콘으로 쓸 바닐라 아이템 ID (MythicMob 기본 엔티티의 스폰알)
 * @param rarity      펫 등급 (ItemRarity 이름: 종류 고정 등급, 없으면 레벨로 정한 등급), 없으면 null
 * @param name        아이콘 이름 (펫 종류 이름)
 * @param nickname    플레이어가 붙인 이름, 없으면 null
 * @param description 아이콘 설명 줄
 * @param active      지금 소환 중인지
 * @param status      상태
 * @param timeLeft    {@code status} 가 DEAD·REVOKED 일 때 남은 초, 아니면 0
 * @param stats       레벨이 있는 펫의 능력치, 없으면 null
 * @param pickup      자동 줍기 반경(블록), 없으면 0
 * @param nameable    이름 변경 가능 (서버 설정)
 * @param skins       스킨이 있음
 * @param inventory   가방이 있음
 * @param mountable   탈 수 있음
 * @param signals     신호봉을 받을 수 있음
 */
public record PetInfo(String id, String icon, String rarity, String name, String nickname, List<String> description, boolean active,
                      Status status, int timeLeft, Stats stats, int pickup,
                      boolean nameable, boolean skins, boolean inventory, boolean mountable, boolean signals) {

    public enum Status { READY, DEAD, REVOKED }

    /**
     * @param levelName     현재 등급 이름
     * @param rarity        현재 등급의 Core 공통 등급 ({@code ItemRarity} 이름: COMMON ~ MYTHIC)
     * @param nextLevelName 다음 등급 이름, 최고 등급이면 null
     * @param health        현재 체력
     * @param maxHealth     최대 체력
     * @param regeneration  초당 회복
     * @param damage        공격 보정 % (0 = 기본)
     * @param resistance    방어 보정 % (0 = 기본)
     * @param power         힘 % (0 = 기본)
     * @param experience    누적 경험치
     * @param levelStart    현재 등급이 시작하는 경험치
     * @param levelEnd      다음 등급에 필요한 경험치 (최고 등급이면 levelStart 와 같음)
     */
    public record Stats(String levelName, String rarity, String nextLevelName, double health, double maxHealth, double regeneration,
                        int damage, int resistance, int power, double experience, double levelStart, double levelEnd) {
        public boolean maxLevel() {
            return nextLevelName == null;
        }
    }
}
