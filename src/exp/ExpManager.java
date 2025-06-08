package exp;

import data.UserData;
import routine.DailyRoutine;
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
        if (routine instanceof DailyRoutine dr) {
            if (dr.getStreakCount() >= 7) {
                expWithBonus += 50;
            } else if (dr.getStreakCount() >= 3) {
                expWithBonus += 20;
            }
        }

        // 4. 난이도 가중치 적용 (1.0 ~ 2.0)
        double weight = 1.0 + (routine.getDifficulty() * 0.2);
        int finalExp = (int) Math.round(expWithBonus * weight);

        // 경험치 추가
        userData.setExp(userData.getExp() + finalExp);
        // 경험치 routine 클래스에 저장 (추후 차감 시에 반영하기 위해)
        routine.setLastGainedExp(finalExp);

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

    // 루틴 완료 취소 시 exp 차감을 위한 메소드
    public void removeExpFromRoutine(Routine routine) {
        int expToRemove = routine.getLastGainedExp();
        userData.setExp(Math.max(0, userData.getExp() - expToRemove));
        System.out.printf("[-] %d EXP 차감 (완료 취소)\n", expToRemove);

        // 레벨 하락 처리
        while (userData.getExp() < 0 && userData.getLevel() > 1) {
            levelDown();
        }

        // 음수 경험치는 0으로 보정
        if (userData.getExp() < 0) {
            userData.setExp(0);
        }
    }

    private void levelDown() {
        userData.setLevel(userData.getLevel() - 1);
        userData.setNeedExp(calculateNextNeedExp(userData.getLevel()));
        // 경험치는 레벨업 시 차감됐던 만큼 다시 채워줌
        userData.setExp(userData.getNeedExp() + userData.getExp());
        System.out.printf("🔻 레벨 다운! Lv.%d (필요 EXP: %d)\n",
                userData.getLevel(), userData.getNeedExp());
    }

    private void checkLevelUp() {
        while (userData.getExp() >= userData.getNeedExp()) {
            levelUp();
        }
    }

    private void levelUp() {
        userData.setExp(userData.getExp() - userData.getNeedExp());
        userData.setLevel(userData.getLevel() + 1);
        userData.setNeedExp(calculateNextNeedExp(userData.getLevel()));
        System.out.printf("🎉 레벨 업! Lv.%d (필요 EXP: %d)\n",
                userData.getLevel(), userData.getNeedExp());
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