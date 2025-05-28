package routine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoutineManager {
    private final List<Routine> routines;
    private final Runnable saveCallback;

    public RoutineManager(List<Routine> routines, Runnable saveCallback) {
        this.routines = new ArrayList<>(routines);
        this.saveCallback = saveCallback;
    }

    public Routine addRoutine(String content, int baseExp) {
        Routine routine = new Routine(content, baseExp);
        routines.add(routine);
        saveCallback.run();
        return routine;
    }

    public DailyRoutine addDailyRoutine(String content, int baseExp) {
        DailyRoutine routine = new DailyRoutine(content, baseExp);
        routines.add(routine);
        saveCallback.run();
        return routine;
    }

    public StreakRoutine addStreakRoutine(String content, int baseExp) {
        StreakRoutine routine = new StreakRoutine(content, baseExp);
        routines.add(routine);
        saveCallback.run();
        return routine;
    }

    public Optional<Routine> getRoutineById(String id) {
        return routines.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Routine> getAllRoutines() {
        return new ArrayList<>(routines);
    }

    public boolean updateRoutine(String id, String newContent, int newBaseExp) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            routine.setContent(newContent);
            routine.setBaseExp(newBaseExp);
            saveCallback.run();
            return true;
        }
        return false;
    }

    public int completeRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (!routine.isCompleted()) {
                routine.markAsCompleted();
                saveCallback.run();
                return routine.getBaseExp();
            }
        }
        return 0;
    }

    public void resetAllDailyRoutines() {
        routines.stream()
                .filter(r -> r instanceof DailyRoutine)
                .forEach(r -> ((DailyRoutine) r).resetIfNewDay());
        saveCallback.run();
    }
}