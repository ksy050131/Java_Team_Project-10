package routine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.bson.Document;

public class Routine {
    private String id;
    private String content;
    private int difficulty;
    private boolean completed;
    protected String dateCreated;
    protected String dateMarkedCompleted; // 날짜 문자열 yyyy-MM-dd
    private RoutineType type;
    private int streakCount;
    private int lastGainedExp = 0; // 마지막으로 얻은 exp 저장 -> 취소 시 그대로 가져와서 차감하기 위해 사용

    public enum RoutineType {
        NORMAL, DAILY, STREAK
    }

    public Routine() {
        id = "";
        content = "";
        difficulty = 0;
        completed = false;
        dateCreated = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dateMarkedCompleted = null;
    }

    // 생성자 오버로딩: 3개 인자 버전
    public Routine(String content, int difficulty, RoutineType type) {
        this();
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.difficulty = difficulty;
        this.type = type;
        this.streakCount = 0;
    }

    // 생성자 오버로딩: 2개 인자 버전 (기본 RoutineType.NORMAL)
    public Routine(String content, int difficulty) {
        this(content, difficulty, RoutineType.NORMAL);
    }

    // 일회성 생성자
    public Routine(String content) {
        this(content, 1, RoutineType.NORMAL);  // 난이도 기본 1, 타입 NORMAL 지정
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public boolean isCompleted() { return completed; }
    public String getDateCreated() { return dateCreated; }
    public String getDateMarkedCompleted() { return dateMarkedCompleted; }
    public RoutineType getType() { return type; }
    public void setType(RoutineType type) { this.type = type; }
    public int getStreakCount() { return streakCount; }
    public void setLastGainedExp(int lastGainedExp) { this.lastGainedExp = lastGainedExp; }
    public int getLastGainedExp() { return lastGainedExp; }

    public void markAsCompleted() {
        if (completed) return;

        completed = true;
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (type == RoutineType.DAILY || type == RoutineType.STREAK) {
            if (type == RoutineType.STREAK) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date lastDate = dateMarkedCompleted == null ? null : sdf.parse(dateMarkedCompleted);
                    Date todayDate = sdf.parse(today);

                    if (lastDate != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(lastDate);
                        cal.add(Calendar.DATE, 1);
                        Date nextDay = cal.getTime();

                        if (sdf.format(nextDay).equals(today)) {
                            streakCount++;
                        } else if (!sdf.format(lastDate).equals(today)) {
                            streakCount = 1;
                        }
                    } else {
                        streakCount = 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dateMarkedCompleted = today;
        }

        System.out.println("✔ " + content + " 완료!");
    }

    public void markAsUncompleted() {
        completed = false;
        dateMarkedCompleted = null;
    }

    public void resetForNewDay() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (type == RoutineType.DAILY || type == RoutineType.STREAK) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date lastDate = dateMarkedCompleted == null ? null : sdf.parse(dateMarkedCompleted);
                Date todayDate = sdf.parse(today);

                if (lastDate != null && !sdf.format(lastDate).equals(today)) {
                    completed = false;

                    if (type == RoutineType.STREAK) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(lastDate);
                        cal.add(Calendar.DATE, 1);
                        Date nextDay = cal.getTime();

                        if (!sdf.format(nextDay).equals(today)) {
                            streakCount = 0;
                        }
                    }

                    System.out.println("🔁 " + content + " 초기화됨");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Document toDocument() {
        return new Document("id", id)
                .append("content", content)
                .append("difficulty", difficulty)
                .append("completed", completed)
                .append("dateCreated", dateCreated)
                .append("dateMarkedCompleted", dateMarkedCompleted)
                .append("type", type.name())
                .append("streakCount", streakCount);
    }

    public static Routine fromDocument(Document doc) {
        Routine routine = new Routine();
        routine.id = doc.getString("id");
        routine.content = doc.getString("content");
        routine.difficulty = doc.getInteger("difficulty", 0);
        routine.completed = doc.getBoolean("completed", false);
        routine.dateCreated = doc.getString("dateCreated");
        routine.dateMarkedCompleted = doc.getString("dateMarkedCompleted");

        /**
         * 예외 상황(DB에 이미 null 값이 있거나 필드가 누락된 데이터가 들어오는 경우
         * 를 대비하여 null 체크 후 기본값을 저장
          */

        String typeStr = doc.getString("type");
        if (typeStr == null) {
            routine.type = RoutineType.NORMAL;  // 기본값 지정
        } else {
            routine.type = RoutineType.valueOf(typeStr);
        }

        routine.streakCount = doc.getInteger("streakCount", 0);
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
