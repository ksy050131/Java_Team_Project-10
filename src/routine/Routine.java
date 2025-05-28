package routine;

import java.util.UUID;

public class Routine {
    private final String id;
    private String content;
    private int baseExp;
    private boolean completed;

    public Routine(String content, int baseExp) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("루틴 내용은 필수입니다.");
        }
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.baseExp = Math.max(10, baseExp);
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getBaseExp() { return baseExp; }
    public void setBaseExp(int baseExp) { this.baseExp = Math.max(0, baseExp); }
    public boolean isCompleted() { return completed; }

    public void markAsCompleted() {
        if (!completed) {
            completed = true;
            System.out.println("✔ " + content + " 완료!");
        }
    }

    public void resetCompletion() {
        completed = false;
    }
}