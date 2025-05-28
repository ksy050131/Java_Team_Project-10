package routine;

import java.time.LocalDate;

public class StreakRoutine extends DailyRoutine {
    private int streakCount;
    private LocalDate lastStreakDate;

    public StreakRoutine(String content, int baseExp) {
        super(content, baseExp);
    }

    @Override
    public void markAsCompleted() {
        LocalDate today = LocalDate.now();
        if (lastStreakDate != null && lastStreakDate.plusDays(1).equals(today)) {
            streakCount++;
        } else {
            streakCount = 1;
        }
        lastStreakDate = today;
        super.markAsCompleted();
    }

    public int getStreakBonusExp() {
        if (streakCount >= 7) return getBaseExp() * 2;
        if (streakCount >= 3) return getBaseExp();
        return 0;
    }

    public int getStreakCount() { return streakCount; }
}