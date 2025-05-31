package routine;

import java.time.LocalDate;
import java.util.UUID;

public class Routine {
    private final String id;
    private String content;
    private int difficulty;
    private boolean completed;
    private LocalDate lastCompletedDate;
    private RoutineType type;
    private int streakCount;

    public enum RoutineType {
        NORMAL, DAILY, STREAK
    }

    public Routine(String content, int difficulty, RoutineType type) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("루틴 내용은 필수입니다.");
        }
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.difficulty = Math.max(1, Math.min(5, difficulty));
        this.type = type;
        this.completed = false;
        this.streakCount = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public int getDifficulty() { return difficulty; }
    public boolean isCompleted() { return completed; }
    public RoutineType getType() { return type; }
    public int getStreakCount() { return streakCount; }
    public LocalDate getLastCompletedDate() { return lastCompletedDate; }

    // Setters
    public void setContent(String content) { this.content = content; }
    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(5, difficulty));
    }
    public void setType(RoutineType type) { this.type = type; }

    public void markAsCompleted() {
        if (completed) return;

        completed = true;
        LocalDate today = LocalDate.now();

        if (type == RoutineType.DAILY || type == RoutineType.STREAK) {
            if (type == RoutineType.STREAK) {
                if (lastCompletedDate != null && lastCompletedDate.plusDays(1).equals(today)) {
                    streakCount++;
                } else if (lastCompletedDate == null || !lastCompletedDate.equals(today)) {
                    streakCount = 1;
                }
            }
            lastCompletedDate = today;
        }

        System.out.println("✔ " + content + " 완료!");
    }

    public void markAsUncompleted() {
        completed = false;
    }

    public void resetForNewDay() {
        LocalDate today = LocalDate.now();

        if (type == RoutineType.DAILY || type == RoutineType.STREAK) {
            if (lastCompletedDate != null && !lastCompletedDate.equals(today)) {
                completed = false;

                if (type == RoutineType.STREAK &&
                        !lastCompletedDate.plusDays(1).equals(today)) {
                    streakCount = 0;
                }

                System.out.println("🔁 " + content + " 초기화됨");
            }
        }
    }
}