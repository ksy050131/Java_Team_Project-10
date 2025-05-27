package routine;

import java.util.UUID; // 고유 ID 생성을 위해 추가

public class Routine {
    private String id; // 각 루틴을 구별할 고유 ID
    private String content; // 루틴 내용
    private int rewardExp; // 완료 시 얻는 경험치 값
    private boolean completed; // 완료 여부

    // 기본 생성자
    public Routine() {
        id = "";
        content = "";
        rewardExp = 0;
        completed = false; // 기본값은 미완료
    }

    // 생성자 - 새 루틴 생성 시 사용
    public Routine(String content, int rewardExp) {
        this();
        this.id = UUID.randomUUID().toString(); // 고유 ID 자동 생성
        this.content = content;
        this.rewardExp = rewardExp;
    }

    public Routine(String content) {
        this();
        this.content = content;
    }

    // Getters and Setters
    // id는 Setter를 만들지 않는 것이 좋습니다 (생성 시 할당).
    public String getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getRewardExp() { return rewardExp; }
    public void setRewardExp(int rewardExp) { this.rewardExp = rewardExp; }
    public boolean isCompleted() { return completed; }

    // 핵심 메서드

    public void markAsCompleted() {
        if (!this.completed) {
            this.completed = true;
            System.out.println("루틴 '" + content + "' 완료!"); // 로그 또는 UI 피드백
            // 여기에 경험치 획득 로직을 직접 넣기보다는,
            // 이 메서드를 호출하는 쪽(예: RoutineManager)에서 경험치를 반환받아 처리하는 것이 좋습니다.
        }
    }
    public void markAsUncompleted() {
        completed = false;
    }

    @Override
    public String toString() { // 디버깅 및 간단한 출력용
        return "Routine{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", completed=" + completed +
                ", rewardExp=" + rewardExp +
                '}';
    }
}
