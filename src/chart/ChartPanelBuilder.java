package chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class ChartPanelBuilder {

    /**
     * JFreeChart 객체를 사용하여 ChartPanel을 생성하고 설정합니다.
     *
     * @param chart ChartPanel에 표시할 JFreeChart 객체
     * @return 설정된 ChartPanel 객체
     */
    public ChartPanel createChartPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600)); // 패널 기본 크기
        chartPanel.setMouseWheelEnabled(true); // 마우스 휠 줌 기능 활성화
        chartPanel.setDomainZoomable(true);   // X축 줌 가능
        chartPanel.setRangeZoomable(true);    // Y축 줌 가능
        // Tooltip 기본적으로 활성화되어 있음 (ChartFactory에서 true로 설정)
        return chartPanel;
    }

    /**
     * ChartPanel을 포함하는 JPanel을 생성합니다. (Frame에 추가하기 용이하도록)
     *
     * @param chartPanel 포함할 ChartPanel
     * @return ChartPanel을 포함하는 JPanel
     */
    public JPanel buildPanelWithChart(ChartPanel chartPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }
}