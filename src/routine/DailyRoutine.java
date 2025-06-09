package routine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import org.bson.Document;
import java.time.LocalDate;

public class DailyRoutine extends Routine {
    private int streakCount;
    // [추가] 모든 완료 날짜를 저장할 리스트
    private List<String> completionDates;

    // 기본 생성자
    public DailyRoutine() {
        super();
        this.streakCount = 0;
        this.completionDates = new ArrayList<>(); // 리스트 초기화
    }

    // 기존 생성자
    public DailyRoutine(String content, int difficulty) {
        super(content, difficulty);
        this.streakCount = 0;
        this.completionDates = new ArrayList<>(); // 리스트 초기화
    }

    // --- Getter & Setter 추가 ---
    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    public List<String> getCompletionDates() { return completionDates; }
    public void setCompletionDates(List<String> completionDates) { this.completionDates = completionDates; }

    @Override
    public void markAsCompleted() {
        if (isCompleted()) return; // 이미 오늘 완료했다면 아무것도 안 함

        super.markAsCompleted(); // completed = true, dateMarkedCompleted = 오늘 날짜로 설정

        // [수정] 완료 날짜 리스트에 오늘 날짜 추가 (중복 방지)
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!this.completionDates.contains(today)) {
            this.completionDates.add(today);
        }

        // [수정] 스트릭 카운트를 리스트 기반으로 재계산
        recalculateStreak();

        System.out.println("✔ " + content + " 완료! (연속 " + streakCount + "일)");
    }

    /**
     * [오버라이드 및 수정] 완료 취소 시 스트릭 카운트를 재계산합니다.
     */
    @Override
    public void markAsUncompleted() {
        super.markAsUncompleted(); // completed = false, dateMarkedCompleted = null

        // [수정] 완료 날짜 리스트에서 오늘 날짜 제거
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.completionDates.remove(today);

        // [수정] 부모의 dateMarkedCompleted를 리스트의 마지막 날짜로 재설정
        if (!this.completionDates.isEmpty()) {
            // 날짜순으로 정렬 후 마지막 날짜를 가져옴
            Collections.sort(this.completionDates);

            this.dateMarkedCompleted = this.completionDates.get(this.completionDates.size() - 1);
        }

        // [수정] 스트릭 카운트를 리스트 기반으로 재계산
        recalculateStreak();

        System.out.println("✔ " + content + " 완료 취소. (현재 연속 " + streakCount + "일)");
    }

    /**
     * [추가] 완료 날짜 리스트를 기반으로 연속 완료일을 정확하게 재계산하는 메서드
     */
    private void recalculateStreak() {
        if (this.completionDates == null || this.completionDates.isEmpty()) {
            this.streakCount = 0;
            return;
        }

        // 날짜 문자열을 시간순으로 정렬
        Collections.sort(this.completionDates);

        int currentStreak = 0;
        LocalDate lastDate = null;

        // 최신 날짜부터 역순으로 탐색하며 연속된 날짜인지 확인
        for (int i = this.completionDates.size() - 1; i >= 0; i--) {
            try {
                LocalDate currentDate = LocalDate.parse(this.completionDates.get(i));
                if (lastDate == null) { // 가장 최신 날짜
                    currentStreak = 1;
                } else {
                    if (lastDate.minusDays(1).isEqual(currentDate)) { // 이전 날짜가 연속된 날짜이면
                        currentStreak++;
                    } else { // 연속이 깨지면 중단
                        break;
                    }
                }
                lastDate = currentDate;
            } catch (Exception e) {
                // 날짜 파싱 실패 시 해당 데이터는 건너뜀
                e.printStackTrace();
            }
        }

        this.streakCount = currentStreak;
    }


    @Override
    public Document toDocument() {
        Document doc = super.toDocument();
        doc.put("type", "DailyRoutine"); // 타입을 명시하여 일반 루틴과 구분
        doc.append("streakCount", streakCount);
        // [수정] 완료 날짜 리스트 저장
        doc.append("completionDates", completionDates);
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
        // dr.lastGainedExp = doc.getInteger("lastGainedExp", 0); // Routine.java에 lastGainedExp 필드가 없으므로 주석 처리
        dr.streakCount = doc.getInteger("streakCount", 0);

        // [수정] 완료 날짜 리스트 복원
        List<String> loadedDates = (List<String>) doc.get("completionDates");
        if (loadedDates != null) {
            dr.completionDates = new ArrayList<>(loadedDates);
        } else {
            dr.completionDates = new ArrayList<>();
        }

        return dr;
    }

    @Override
    public void P_updateStatusForNewDay() {
        // ... (기존 로직과 동일) ...
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            LocalDate today = LocalDate.parse(todayStr);
            if (isCompleted() && getDateMarkedCompleted() != null) {
                LocalDate markedDate = LocalDate.parse(getDateMarkedCompleted());
                if (markedDate.isBefore(today)) {
                    markAsUncompleted(); // 스트릭 계산 로직이 포함된 uncomplete 호출
                    System.out.println("일일 루틴 '" + getContent() + "'이(가) 새 날짜를 맞아 초기화되었습니다.");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}