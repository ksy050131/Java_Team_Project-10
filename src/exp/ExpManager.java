/**
 * 경험치와 레벨업을 관리하는 클래스.
 * - 루틴 완료 시 경험치 추가
 * - 피보나치 수열로 레벨업 필요 경험치 계산
 * - 현재 상태 출력 기능
 */
public class ExpManager {
    private UserData userData;

    public ExpManager(UserData userData) {
        this.userData = userData; // UserData와 연동
    }

    /**
     * 경험치 추가 및 레벨업 체크
     * @param exp 획득한 경험치 (Routine의 rewardExp에서 전달)
     */
    public void addExp(int exp) {
        userData.setExp(userData.getExp() + exp);
        checkLevelUp(); // 경험치 누적 후 레벨업 조건 확인
    }

    /** 레벨업 조건 충족 시 자동으로 레벨업 */
    private void checkLevelUp() {
        while (userData.getExp() >= userData.getNeedExp()) {
            levelUp(); // 필요 경험치 도달 시 레벨업
        }
    }

    /** 레벨업 처리 로직 */
    private void levelUp() {
        userData.setExp(userData.getExp() - userData.getNeedExp()); // 잔여 경험치 계산
        userData.setLevel(userData.getLevel() + 1); // 레벨 증가
        userData.setNeedExp(calculateNextNeedExp(userData.getLevel())); // 다음 필요 경험치 갱신
    }

    /**
     * 피보나치 수열 기반 필요 경험치 계산
     * @param level 현재 레벨
     * @return 다음 레벨업에 필요한 경험치
     */
    private int calculateNextNeedExp(int level) {
        if (level <= 1) return 100; // 레벨 1 기본값
        int a = 0, b = 1;
        for (int i = 0; i < level; i++) {
            int temp = a + b;
            a = b;
            b = temp; // 피보나치 수열 계산
        }
        return b * 100; // 100, 200, 300, 500, 800...
    }

    /** 현재 레벨과 경험치 출력 */
    public void printStatus() {
        System.out.printf("Lv.%d | EXP: %d/%d\n",
                userData.getLevel(), userData.getExp(), userData.getNeedExp());
    }
}