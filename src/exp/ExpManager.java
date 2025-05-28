package exp;

import data.UserData;
import routine.Routine;
import routine.StreakRoutine;

public class ExpManager {
    private final UserData userData;

    public ExpManager(UserData userData) {
        this.userData = userData;
    }

    public void addExpFromRoutine(Routine routine) {
        int baseExp = routine.getBaseExp();
        int totalExp = baseExp;

        if (routine instanceof StreakRoutine) {
            totalExp += ((StreakRoutine) routine).getStreakBonusExp();
        }

        userData.setExp(userData.getExp() + totalExp);
        System.out.printf("[+] %d EXP (기본 %d + 보너스 %d)\n",
                totalExp, baseExp, totalExp - baseExp);
        checkLevelUp();
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