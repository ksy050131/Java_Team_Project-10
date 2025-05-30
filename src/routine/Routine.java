package routine;

import java.util.UUID; // 고유 ID 생성을 위해 추가

public class Routine {
    private String id; // 각 루틴을 구별할 고유 ID
    private String content; // 루틴 내용
    private int difficulty; // 난이도 (1~5 등)
    private boolean completed; // 완료 여부

    // 기본 생성자
    public Routine() {
        id = "";
        content = "";
        difficulty = 0;
        completed = false; // 기본값은 미완료
    }

    // 생성자 - 새 루틴 생성 시 사용
    public Routine(String content, int difficulty) {
        this();
        this.id = UUID.randomUUID().toString(); // 고유 ID 자동 생성
        this.content = content;
        this.difficulty = difficulty;
    }

    public Routine(String content) {
        this();
        this.content = content;
    }

    // Getters and Setters
    public String getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public boolean isCompleted() { return completed; }

    public void markAsCompleted() {
        if (!this.completed) {
            this.completed = true;
            System.out.println("루틴 '" + content + "' 완료!");
        }
    }

    public void markAsUncompleted() {
        completed = false;
    }

    @Override
    public String toString() {
        return "Routine{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", completed=" + completed +
                ", difficulty=" + difficulty +
                '}';
    }
}
