package chart;

import data.UserData; // 사용자의 UserData 클래스 임포트
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import routine.Routine;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.List;

public class ChartDisplayFrame extends JFrame {

    private UserData user;

    public ChartDisplayFrame(UserData user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("일일 루틴 완료 통계 (최근 7일)"); // 프레임 제목
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫을 때 동작

        // 1. 데이터셋 생성
        ChartDatasetBuilder datasetBuilder = new ChartDatasetBuilder();
        List<Routine> userRoutines = (user != null && user.getRoutines() != null) ? user.getRoutines() : List.of();
        CategoryDataset dataset = datasetBuilder.createDailyCompletionDataset(
                userRoutines,
                LocalDate.now(), // 오늘을 기준으로
                7,               // 최근 7일
                "완료된 루틴 수"  // 시리즈 이름
        );

        // 2. 차트 생성
        ChartFactoryManager chartFactory = new ChartFactoryManager();
        JFreeChart barChart = chartFactory.createBarChart(
                dataset,
                "최근 7일간 루틴 완료 현황", // 차트 제목
                "날짜",                 // X축 레이블
                "완료 개수"             // Y축 레이블
        );

        // 3. 차트 커스터마이징
        ChartCustomizer.applyModernCleanTheme(barChart);

        // 4. ChartPanel 생성
        ChartPanelBuilder panelBuilder = new ChartPanelBuilder();
        ChartPanel chartPanel = panelBuilder.createChartPanel(barChart);

        // 5. 프레임에 ChartPanel 추가
        setContentPane(panelBuilder.buildPanelWithChart(chartPanel));

        pack(); // 컴포넌트 크기에 맞게 프레임 크기 조절
        setLocationRelativeTo(null); // 화면 중앙에 표시
        // setVisible(true); // MainAppGUI에서 호출 예정
    }

    // MainAppGUI에서 이 frame 인스턴스를 직접 setVisible(true) 할 것이므로,
    // 별도의 showFrame() 메서드나 main 메서드는 여기서는 생략합니다.
    // 테스트용 main 메서드 (선택 사항)
    /*
    public static void main(String[] args) {
        // 테스트를 위한 가짜 UserData 생성
        UserData testUser = new UserData();
        // testUser.setUserId("testUser");
        // List<Routine> testRoutines = new ArrayList<>();
        // // 여기에 테스트용 Routine 객체들을 추가합니다. (완료된 루틴, 날짜 지정 등)
        // // 예:
        // Routine r1 = new Routine("Test Routine 1", 1);
        // r1.markAsCompleted(); // 오늘 날짜로 완료됨
        // testRoutines.add(r1);
        //
        // Routine r2 = new Routine("Test Routine 2", 2);
        // // r2.setDateMarkedCompleted("2025-05-31"); // 과거 날짜로 설정 (Routine 클래스에 setDateMarkedCompleted가 있다면)
        // // r2.markAsCompleted(); // 이 경우 markAsCompleted가 날짜를 오늘로 덮어쓰므로, 날짜를 수동 설정할 방법 필요
        // // 또는 Routine 생성자나 별도 메서드로 완료 날짜를 지정할 수 있어야 함.
        // // 현재 Routine 클래스는 markAsCompleted() 호출 시 오늘 날짜로 고정됨.
        // // 테스트를 위해서는 과거 날짜로 완료된 루틴을 만들 수 있어야 함.
        // // 여기서는 Routine.java의 dateMarkedCompleted를 직접 수정하거나,
        // // 해당 필드를 설정할 수 있는 메서드가 있다고 가정.
        // testUser.setRoutines(testRoutines);

        SwingUtilities.invokeLater(() -> {
            ChartDisplayFrame frame = new ChartDisplayFrame(testUser);
            frame.setVisible(true);
        });
    }
    */
}