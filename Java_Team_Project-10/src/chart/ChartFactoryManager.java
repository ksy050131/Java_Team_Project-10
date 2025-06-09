package chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

public class ChartFactoryManager {

    /**
     * CategoryDataset을 사용하여 바 차트를 생성합니다.
     *
     * @param dataset           차트에 사용될 데이터셋
     * @param title             차트 제목
     * @param categoryAxisLabel X축(카테고리 축) 레이블
     * @param valueAxisLabel    Y축(값 축) 레이블
     * @return 생성된 JFreeChart 객체
     */
    public JFreeChart createBarChart(CategoryDataset dataset, String title, String categoryAxisLabel, String valueAxisLabel) {
        return ChartFactory.createBarChart(
                title,              // 차트 제목
                categoryAxisLabel,  // X축 레이블
                valueAxisLabel,     // Y축 레이블
                dataset,            // 데이터셋
                PlotOrientation.VERTICAL, // 차트 방향 (수직)
                true,               // 범례 표시 여부
                true,               // 툴팁 표시 여부
                false               // URL 생성 여부
        );
    }
}