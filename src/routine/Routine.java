<<<<<<< HEAD
package routine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.bson.Document; // MongoDB 연동을 위해 추가

public class Routine {
    private String id;
    private String content;
    private int difficulty;
    private boolean completed;
    protected String dateCreated;
    protected String dateMarkedCompleted; // 루틴이 완료된 날짜 (protected로 변경 또는 getter 제공)
    // DailyRoutine에서 접근하거나, JSON에 포함시키기 위함.

    // 기본 생성자
    public Routine() {
        id = "";
        content = "";
        difficulty = 0;
        completed = false;
        dateCreated = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dateMarkedCompleted = null; // 초기화
    }

    // 생성자 - 새 루틴 생성 시 사용
    public Routine(String content, int difficulty) {
        this();
        this.id = UUID.randomUUID().toString(); // 고유 ID 자동 생성
        this.content = content;
        this.difficulty = difficulty;
    }

    public Routine(String content) {
        this(); // 기본 생성자 호출
        this.id = UUID.randomUUID().toString();
        this.content = content;
    }

    // Getters and Setters
    // id는 Setter를 만들지 않는 것이 좋습니다 (생성 시 할당).
    public String getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public boolean isCompleted() { return completed; }
    public String getDateCreated() { return dateCreated; }
    public String getDateMarkedCompleted() { return dateMarkedCompleted; }

    // 핵심 메서드
    public void markAsCompleted() {
        if (!this.completed) {
            this.completed = true;
            this.dateMarkedCompleted = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // 완료된 날짜 기록
            System.out.println("루틴 '" + content + "' 완료!");
        }
    }

    public void markAsUncompleted() {
        this.completed = false;
        this.dateMarkedCompleted = null; // 완료 날짜도 초기화
        // System.out.println("루틴 '" + content + "' 미완료 처리됨."); // 필요시 로그
    }

    /**
     * 새로운 날짜에 맞춰 루틴의 완료 상태를 업데이트합니다.
     * 기본 Routine(일회성)의 경우 이 메서드는 아무 작업도 수행하지 않습니다.
     * DailyRoutine과 같은 하위 클래스에서 이 메서드를 오버라이드하여
     * 매일 초기화되는 로직을 구현합니다.
     */
    public void P_updateStatusForNewDay() {
        // 기본 루틴은 일회성이므로 상태 업데이트 로직이 없음
    }


    @Override
    public String toString() {
        return "Routine{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", completed=" + completed +
                ", difficulty=" + difficulty +
                (dateMarkedCompleted != null ? ", completedDate=" + dateMarkedCompleted : "") +
                '}';
    }

    // MongoDB 저장용
    public Document toDocument() {
        return new Document("id", id)
                .append("content", content)
                .append("difficulty", difficulty)
                .append("completed", completed)
                .append("dateCreated", dateCreated)
                .append("dateMarkedCompleted", dateMarkedCompleted);
    }

    // MongoDB 복원용
    public static Routine fromDocument(Document doc) {
        Routine routine = new Routine();
        routine.id = doc.getString("id");
        routine.content = doc.getString("content");
        routine.difficulty = doc.getInteger("difficulty", 0);
        routine.completed = doc.getBoolean("completed", false);
        routine.dateCreated = doc.getString("dateCreated");
        routine.dateMarkedCompleted = doc.getString("dateMarkedCompleted");
        return routine;
    }
}