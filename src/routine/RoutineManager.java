package routine;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RoutineManager {
    private final List<Routine> routines;
    private final Runnable saveCallback;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RoutineManager(List<Routine> routines, Runnable saveCallback) {
        this.routines = routines;
        this.saveCallback = saveCallback;
        startDailyResetScheduler();
    }

    public Routine addRoutine(String content, int difficulty, Routine.RoutineType type) {
        Routine routine = new Routine(content, difficulty, type);
        routines.add(routine);
        saveCallback.run();
        return routine;
    }

    public int completeRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (!routine.isCompleted()) {
                routine.markAsCompleted();
                saveCallback.run();
                return 1; // 성공
            }
        }
        return 0; // 실패
    }

    public void resetAllDailyRoutines() {
        for (Routine routine : routines) {
            if (routine.getType() == Routine.RoutineType.DAILY ||
                    routine.getType() == Routine.RoutineType.STREAK) {
                routine.resetForNewDay();
            }
        }
        saveCallback.run();
    }

    private void startDailyResetScheduler() {
        long initialDelay = calculateInitialDelay();

        scheduler.scheduleAtFixedRate(
                this::resetAllDailyRoutines,
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        );
    }

    private long calculateInitialDelay() {
        LocalTime now = LocalTime.now();
        LocalTime target = LocalTime.MIDNIGHT.plusMinutes(1);

        if (now.isBefore(target)) {
            return Duration.between(now, target).getSeconds();
        } else {
            // 수정: plusHours(24) 사용
            return Duration.between(now, target.plusHours(24)).getSeconds();
        }
    }

    public Optional<Routine> getRoutineById(String id) {
        return routines.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Routine> getAllRoutines() {
        return routines;
    }

    public boolean updateRoutineContent(String id, String newContent) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            routine.setContent(newContent);
            saveCallback.run();
            return true;
        }
        return false;
    }

    public boolean deleteRoutine(String id) {
        boolean removed = routines.removeIf(r -> r.getId().equals(id));
        if (removed) {
            saveCallback.run();
            return true;
        }
        return false;
    }
}