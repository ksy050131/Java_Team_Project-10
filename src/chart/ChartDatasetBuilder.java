package chart;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import routine.Routine; // 사용자의 Routine 클래스 임포트

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChartDatasetBuilder {

    private static final DateTimeFormatter DATE_FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_FORMATTER_MM_DD = DateTimeFormatter.ofPattern("MM-dd");

    /**
     * 최근 N일간의 날짜별 루틴 완료 횟수 데이터를 포함하는 CategoryDataset을 생성합니다.
     *
     * @param allRoutines 사용자의 모든 루틴 목록
     * @param endDate     차트의 마지막 날짜 (보통 오늘)
     * @param days        표시할 일 수 (예: 7일)
     * @param seriesName  데이터 시리즈의 이름 (범례에 표시됨)
     * @return 생성된 CategoryDataset
     */
    public CategoryDataset createDailyCompletionDataset(List<Routine> allRoutines, LocalDate endDate, int days, String seriesName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 날짜별 완료된 루틴 수 계산
        Map<LocalDate, Long> completionsByDate = allRoutines.stream()
                .filter(Routine::isCompleted)
                .filter(r -> r.getDateMarkedCompleted() != null && !r.getDateMarkedCompleted().isEmpty())
                .collect(Collectors.groupingBy(
                        r -> {
                            try {
                                return LocalDate.parse(r.getDateMarkedCompleted(), DATE_FORMATTER_YYYY_MM_DD);
                            } catch (DateTimeParseException e) {
                                System.err.println("날짜 파싱 오류: " + r.getDateMarkedCompleted() + " - " + e.getMessage());
                                return null; // 파싱 실패 시 null 반환하여 필터링에서 제외
                            }
                        },
                        Collectors.counting()
                ));

        // TreeMap을 사용하여 날짜순으로 정렬된 모든 날짜에 대한 데이터를 보장 (0 포함)
        Map<LocalDate, Long> rangedCompletions = new TreeMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            rangedCompletions.put(currentDate, completionsByDate.getOrDefault(currentDate, 0L));
        }

        // 데이터셋에 추가 (MM-dd 형식으로)
        for (Map.Entry<LocalDate, Long> entry : rangedCompletions.entrySet()) {
            dataset.addValue(entry.getValue(), seriesName, entry.getKey().format(DATE_FORMATTER_MM_DD));
        }

        return dataset;
    }
}