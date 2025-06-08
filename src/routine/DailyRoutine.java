package routine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.bson.Document;

public class DailyRoutine extends Routine {
    private int streakCount;

    // 기본 생성자 (역직렬화 및 자바 리플렉션용)
    public DailyRoutine() {
        super();
        this.streakCount = 0;
    }

    // 기존 생성자
    public DailyRoutine(String content, int difficulty) {
        super(content, difficulty);
        this.streakCount = 0;
    }

    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    @Override
    public void markAsCompleted() {
        if (completed) return;

        completed = true;
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

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
        dateMarkedCompleted = today;

        System.out.println("✔ " + content + " 완료!");
    }

    @Override
    public Document toDocument() {
        // 상위 클래스의 toDocument 호출 후 추가 필드를 붙임
        Document doc = super.toDocument();
        doc.put("type", "DailyRoutine");
        doc.append("streakCount", streakCount);
        return doc;
    }

    public static DailyRoutine fromDocument(Document doc) {
        DailyRoutine dr = new DailyRoutine();
        dr.id = doc.getString("id");
        dr.content = doc.getString("content");
        dr.difficulty = doc.getInteger("difficulty", 0);
        dr.completed = doc.getBoolean("completed", false);
        dr.dateCreated = doc.getString("dateCreated");
        dr.dateMarkedCompleted = doc.getString("dateMarkedCompleted");
        dr.lastGainedExp = doc.getInteger("lastGainedExp", 0);
        dr.streakCount = doc.getInteger("streakCount", 0);
        return dr;
    }

    /**
     * 새로운 날짜에 맞춰 루틴의 완료 상태를 업데이트(초기화)합니다.
     * 이 메서드는 일반적으로 하루에 한 번, 애플리케이션 시작 시 또는 자정 이후 첫 활동 시 호출됩니다.
     */
    @Override
    public void P_updateStatusForNewDay() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // 루틴이 완료되었고, 그 완료 날짜가 오늘 이전이라면 미완료 상태로 초기화합니다.
        if (isCompleted() && getDateMarkedCompleted() != null && getDateMarkedCompleted().compareTo(today) < 0) {
            markAsUncompleted(); // completed를 false로, dateMarkedCompleted를 null로 설정
            System.out.println("일일 루틴 '" + getContent() + "'이(가) 새 날짜를 맞아 초기화되었습니다.");
        }
        // 만약 어제 완료하지 못하고 오늘이 되었다면, 그냥 미완료 상태로 남아있게 됩니다.
        // (dateMarkedCompleted가 null이거나, isCompleted()가 false인 경우)
    }
}