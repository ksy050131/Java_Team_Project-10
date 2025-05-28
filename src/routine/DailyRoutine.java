package routine;

import java.time.LocalDate;

public class DailyRoutine extends Routine {
    private LocalDate lastCompletedDate;

    public DailyRoutine(String content, int baseExp) {
        super(content, baseExp);
    }

    @Override
    public void markAsCompleted() {
        LocalDate today = LocalDate.now();
        if (lastCompletedDate == null || !lastCompletedDate.equals(today)) {
            super.markAsCompleted();
            lastCompletedDate = today;
        }
    }

    public void resetIfNewDay() {
        LocalDate today = LocalDate.now();
        if (lastCompletedDate != null && !lastCompletedDate.equals(today)) {
            resetCompletion();
        }
    }
}