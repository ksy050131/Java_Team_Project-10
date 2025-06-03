package exp;

import data.UserData;
import title.TitleManager;
import routine.Routine;
import java.util.Random;

public class ExpManager {
    private final UserData userData;
    private final Random random = new Random();

    public ExpManager(UserData userData) {
        this.userData = userData;
    }

    public void addExpFromRoutine(Routine routine) {
        // 1. 기본 경험치 (난이도 * 10)
        int baseExp = routine.getDifficulty() * 10;

        // 2. 랜덤 보너스 (0~20% 추가)
        double randomBonus = 1.0 + (random.nextDouble() * 0.2);
        int expWithBonus = (int) Math.round(baseExp * randomBonus);

        // 3. STREAK 보너스 적용
        if (routine.getType() == Routine.RoutineType.STREAK) {
            if (routine.getStreakCount() >= 7) {
                expWithBonus += 50;
            } else if (routine.getStreakCount() >= 3) {
                expWithBonus += 20;
            }
        }

        // 4. 난이도 가중치 적용 (1.0 ~ 2.0)
        double weight = 1.0 + (routine.getDifficulty() * 0.2);
        int finalExp = (int) Math.round(expWithBonus * weight);

        // 경험치 추가
        userData.setExp(userData.getExp() + finalExp);

        // 누적 경험치 업데이트
        userData.setTotalExp(userData.getTotalExp() + finalExp);

        // 누적 경험치 기반 칭호 체크
        TitleManager.checkExpTitles(userData);

        System.out.printf(
                "[+] %d EXP = [기본 %d + 랜덤 보너스 %.0f%% + 스트릭 보너스 %d] × 난이도 가중치 %.1f\n",
                finalExp,
                baseExp,
                (randomBonus - 1.0) * 100,
                expWithBonus - baseExp,
                weight
        );

        checkLevelUp();
    }

    private void checkLevelUp() {
        while (userData.getExp() >= userData.getNeedExp()) {
            levelUp();
        }
    }

    private void levelUp() {
        int currentLevel = userData.getLevel();
        userData.setExp(userData.getExp() - userData.getNeedExp());
        userData.setLevel(currentLevel + 1);

        // 레벨 10 달성 시 초기화
        if (currentLevel + 1 == 10) {
            // 레벨 10 달성 시 고인물 칭호 체크 (레벨 초기화 횟수 증가 전)
            TitleManager.checkVeteranTitle(userData);
        }

        // 레벨 11로 진입 시 (즉, 10레벨을 달성하고 다음 레벨업 시) 초기화
        if (userData.getLevel() == 11) {
            resetToLevelOne();
        } else {
            userData.setNeedExp(calculateNextNeedExp(userData.getLevel()));
        }

        System.out.printf("🎉 레벨 업! Lv.%d (필요 EXP: %d)\n",
                userData.getLevel(), userData.getNeedExp());
    }

    private void resetToLevelOne() {
        // 회차 증가
        userData.setCycle(userData.getCycle() + 1);
        // 레벨 초기화 횟수 증가
        userData.incrementLevelResetCount();

        // 레벨, 경험치, 필요 경험치 초기화
        userData.setLevel(1);
        userData.setExp(0);
        userData.setNeedExp(100);

        // 회차 기반 칭호 업데이트
        TitleManager.checkCycleTitle(userData);

        System.out.printf("\n🚀 10레벨 달성! %d회차로 재시작합니다. (총 %d회 초기화)\n",
                userData.getCycle(), userData.getLevelResetCount());
    }

    private int calculateNextNeedExp(int level) {
        if (level <= 1) return 100;
        long a = 0, b = 1;
        for (int i = 0; i < level; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return (int) Math.min(b * 100, Integer.MAX_VALUE);
    }

    public void printStatus() {
        System.out.printf("Lv.%d | EXP: %d/%d\n",
                userData.getLevel(), userData.getExp(), userData.getNeedExp());
    }
}
