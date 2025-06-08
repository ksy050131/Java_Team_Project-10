package routine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.bson.Document;

public class Routine {
    protected String id;
    protected String content;
    protected int difficulty;
    protected boolean completed;
    protected String dateCreated;
    protected String dateMarkedCompleted; // 날짜 문자열 yyyy-MM-dd
    protected int lastGainedExp = 0; // 마지막으로 얻은 exp 저장 -> 취소 시 그대로 가져와서 차감하기 위해 사용

    // 기본 생성자 (역직렬화용)
    public Routine() {
        this.id = UUID.randomUUID().toString();
        this.dateCreated = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.completed = false;
    }

    // 기존 생성자
    public Routine(String content, int difficulty) {
        this();
        this.content = content;
        this.difficulty = difficulty;
    }

    // 생성자 오버로딩: 1개 인자(내용)
    public Routine(String content) {
        this(content, 1);  // 난이도 기본 1, 타입 NORMAL 지정
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public boolean isCompleted() { return completed; }
    public String getDateCreated() { return dateCreated; }
    public String getDateMarkedCompleted() { return dateMarkedCompleted; }
    public void setLastGainedExp(int lastGainedExp) { this.lastGainedExp = lastGainedExp; }
    public int getLastGainedExp() { return lastGainedExp; }

    public void markAsCompleted() {
        if (completed) return;

        completed = true;

        dateMarkedCompleted = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println("✔ " + content + " 완료!");
    }

    public void markAsUncompleted() {
        completed = false;
        dateMarkedCompleted = null;
    }

    // toDocument 메서드에 타입 Discriminator 추가
    public Document toDocument() {
        return new Document("type", "Routine")
                .append("id", id)
                .append("content", content)
                .append("difficulty", difficulty)
                .append("completed", completed)
                .append("dateCreated", dateCreated)
                .append("dateMarkedCompleted", dateMarkedCompleted)
                .append("lastGainedExp", lastGainedExp);
    }

    // fromDocument에서 타입에 따라 하위 클래스로 분기
    public static Routine fromDocument(Document doc) {
        String type = doc.getString("type");
        if ("DailyRoutine".equals(type)) {
            return DailyRoutine.fromDocument(doc);
        }

        Routine routine = new Routine();
        routine.id = doc.getString("id");
        routine.content = doc.getString("content");
        routine.difficulty = doc.getInteger("difficulty", 0);
        routine.completed = doc.getBoolean("completed", false);
        routine.dateCreated = doc.getString("dateCreated");
        routine.dateMarkedCompleted = doc.getString("dateMarkedCompleted");
        routine.lastGainedExp = doc.getInteger("lastGainedExp", 0);
        return routine;
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

    public void P_updateStatusForNewDay() {

    }
}