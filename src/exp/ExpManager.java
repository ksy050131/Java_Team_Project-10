package exp;

import data.UserData;
import routine.Routine;
import java.util.Random;

public class ExpManager {
    private final UserData userData;
    private final Random random = new Random();

    public ExpManager(UserData userData) {
        this.userData = userData;
    }

    public void addExpFromRoutine(Routine routine) {
        // 1. 기본 경험치 (난이도에 비례)
        int baseExp = calculateBaseExp(routine.getDifficulty());
        
        // 2. 랜덤 보너스 (0~20% 추가)
        double randomBonus = 1.0 + (random.nextDouble() * 0.2);
        int expWithBonus = (int) Math.round(baseExp * randomBonus);
        
        // 3. STREAK 보너스 적용
        if (routine.getType() == Routine.RoutineType.STREAK) {
            expWithBonus += getStreakBonusExp((StreakRoutine) routine);
        }
        
        // 4. 난이도 가중치 추가 적용
        double weight = calculateDifficultyWeight(routine.getDifficulty());
        int finalExp = (int) Math.round(expWithBonus * weight);
        
        userData.setExp(userData.getExp() + finalExp);
        
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

    // 기본 경험치 계산 (난이도 1~5에 따라 10~50)
    private int calculateBaseExp(int difficulty) {
        return difficulty * 10;
    }

    // 난이도 가중치 계산 (1.0 ~ 1.8)
    private double calculateDifficultyWeight(int difficulty) {
        return 1.0 + (difficulty * 0.2);
    }

    // STREAK 보너스 경험치 계산
    private int getStreakBonusExp(StreakRoutine routine) {
        int streak = routine.getStreakCount();
        if (streak >= 7) return 50; // 7일 연속: 50 추가
        if (streak >= 3) return 20; // 3일 연속: 20 추가
        return 0;
    }

    // 레벨업 관련 메서드 (기존과 동일)
    private void checkLevelUp() { /* ... */ }
    private void levelUp() { /* ... */ }
    private int calculateNextNeedExp(int level) { /* ... */ }
    public void printStatus() { /* ... */ }
}
