package chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

public class ChartCustomizer {

    // 한글 표시를 위한 기본 폰트 (시스템에 따라 "Malgun Gothic" 등이 필요할 수 있음)
    private static final String FONT_FAMILY = "SansSerif"; // 또는 "Malgun Gothic" 등

    /**
     * JFreeChart 객체에 스타일을 적용합니다.
     *
     * @param chart 스타일을 적용할 JFreeChart 객체
     */
    public static void applyModernCleanTheme(JFreeChart chart) {
        // 전반적인 안티앨리어싱 설정
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);

        // 차트 제목 설정
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        }

        // Plot 영역 설정
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE); // 플롯 배경색
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY); // 수직 격자선
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);  // 수평 격자선
        plot.setOutlineVisible(false); // 플롯 외곽선 숨김

        // X축 (Domain Axis) 설정
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        domainAxis.setTickLabelFont(new Font(FONT_FAMILY, Font.PLAIN, 10));
        domainAxis.setCategoryMargin(0.2); // 카테고리 간 간격
        domainAxis.setLowerMargin(0.02);   // 축 시작 부분 여백
        domainAxis.setUpperMargin(0.02);   // 축 끝 부분 여백

        // Y축 (Range Axis) 설정
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        rangeAxis.setTickLabelFont(new Font(FONT_FAMILY, Font.PLAIN, 10));
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Y축 눈금을 정수로 표시

        // 바 렌더러 설정
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false); // 바 외곽선 숨김
        // 모던한 파란색 계열 그라데이션 적용
        GradientPaint gp = new GradientPaint(
                0.0f, 0.0f, new Color(79, 129, 189), // 시작 색상 (어두운 파랑)
                0.0f, 0.0f, new Color(155, 187, 89)  // 끝 색상 (밝은 파랑) - 예시로 다른 색상 사용
        );
        // 실제로는 단일 시리즈이므로, 시리즈별 색상 설정보다는 아래처럼 기본 색상 설정
        // renderer.setSeriesPaint(0, new Color(79, 129, 189)); // 단일 시리즈의 색상
        renderer.setSeriesPaint(0, new Color(0, 128, 255)); // 좀 더 밝고 모던한 파란색
        renderer.setItemMargin(0.05); // 바 간의 간격 (카테고리 내)

        // 그림자 효과 비활성화
        renderer.setShadowVisible(false);

        // 범례 설정 (시리즈가 하나면 굳이 필요 없을 수 있음)
        if (chart.getLegend() != null) {
            chart.getLegend().setVisible(false); // 시리즈가 하나이므로 범례는 숨김
            // chart.getLegend().setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
            // chart.getLegend().setBorder(0, 0, 0, 0); // 범례 테두리 없앰
        }
    }
}