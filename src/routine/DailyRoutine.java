package routine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyRoutine extends Routine {

    // DailyRoutine을 위한 특정 필드가 필요하다면 여기에 추가할 수 있지만,
    // 현재로서는 부모 클래스의 필드와 메서드, 그리고 아래의 초기화 로직으로 충분할 수 있습니다.

    // 생성자
    public DailyRoutine(String content, int rewardExp) {
        super(content, rewardExp); // 부모 클래스의 생성자 호출
        // DailyRoutine 생성 시 특별히 초기화할 내용이 있다면 여기에 추가
    }

    // Gson 등 JSON 라이브러리가 역직렬화 시 타입을 구분하고 올바르게 객체를 생성하기 위해
    // 기본 생성자가 필요할 수 있으며, 이 경우 부모의 기본 생성자도 호출됩니다.
    public DailyRoutine() {
        super();
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