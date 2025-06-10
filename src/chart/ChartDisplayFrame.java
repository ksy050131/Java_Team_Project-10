package chart;

import data.UserData;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import routine.Routine;

import javax.swing.*;
import java.awt.BorderLayout; // BorderLayout 임포트 추가
import java.awt.FlowLayout;  // FlowLayout 임포트 추가
import java.time.LocalDate;
import java.util.List;

public class ChartDisplayFrame extends JFrame {

    private UserData user;
    private ChartPanel chartPanel; // [추가] 버튼 이벤트에서 참조할 수 있도록 ChartPanel을 필드로 선언

    public ChartDisplayFrame(UserData user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("일일 루틴 완료 통계 (최근 7일)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 1. 데이터셋 생성
        ChartDatasetBuilder datasetBuilder = new ChartDatasetBuilder();
        List<Routine> userRoutines = (user != null && user.getRoutines() != null) ? user.getRoutines() : List.of();
        CategoryDataset dataset = datasetBuilder.createDailyCompletionDataset(
                userRoutines,
                LocalDate.now(),
                7,
                "완료된 루틴 수"
        );

        // 2. 차트 생성
        ChartFactoryManager chartFactory = new ChartFactoryManager();
        JFreeChart barChart = chartFactory.createBarChart(
                dataset,
                "최근 7일간 루틴 완료 현황",
                "날짜",
                "완료 개수"
        );

        // 3. 차트 커스터마이징
        ChartCustomizer.applyModernCleanTheme(barChart);

        // 4. ChartPanel 생성
        ChartPanelBuilder panelBuilder = new ChartPanelBuilder();
        // [수정] 생성된 ChartPanel을 지역 변수가 아닌 클래스 필드에 할당
        this.chartPanel = panelBuilder.createChartPanel(barChart);

        // 5. 프레임에 컴포넌트 추가
        // [추가] 차트 패널과 버튼 패널을 담을 메인 패널 생성
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(this.chartPanel, BorderLayout.CENTER);

        // [추가] 차트 초기화 버튼 생성
        JButton resetButton = new JButton("차트 뷰 초기화");
        resetButton.addActionListener(e -> {
            if (this.chartPanel != null) {
                // 이 메서드가 줌/이동 상태를 원래대로 복원합니다.
                this.chartPanel.restoreAutoBounds();
            }
        });

        // [추가] 버튼을 담을 하단 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(resetButton);

        // [추가] 메인 패널의 하단에 버튼 패널 추가
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // [수정] 프레임의 Content Pane을 메인 패널로 설정
        setContentPane(mainPanel);

        pack();
        setLocationRelativeTo(null);
    }
}