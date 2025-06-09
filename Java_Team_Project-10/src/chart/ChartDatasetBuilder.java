package chart;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import routine.DailyRoutine; // DailyRoutine 임포트 추가
import routine.Routine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream; // Stream 임포트 추가

public class ChartDatasetBuilder {

    private static final DateTimeFormatter DATE_FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_FORMATTER_MM_DD = DateTimeFormatter.ofPattern("MM-dd");

    public CategoryDataset createDailyCompletionDataset(List<Routine> allRoutines, LocalDate endDate, int days, String seriesName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate startDate = endDate.minusDays(days - 1);

        // [수정] DailyRoutine의 모든 완료 날짜와 일반 Routine의 완료 날짜를 모두 집계
        Map<LocalDate, Long> completionsByDate = allRoutines.stream()
                .flatMap(routine -> {
                    if (routine instanceof DailyRoutine) {
                        // DailyRoutine이면 모든 완료 날짜 목록을 스트림으로 반환
                        DailyRoutine dr = (DailyRoutine) routine;
                        return dr.getCompletionDates().stream();
                    } else {
                        // 일반 Routine이면 완료된 경우에만 단일 완료 날짜를 스트림으로 반환
                        if (routine.isCompleted() && routine.getDateMarkedCompleted() != null) {
                            return Stream.of(routine.getDateMarkedCompleted());
                        } else {
                            return Stream.empty();
                        }
                    }
                })
                .filter(dateStr -> dateStr != null && !dateStr.isEmpty())
                .collect(Collectors.groupingBy(
                        dateStr -> {
                            try {
                                return LocalDate.parse(dateStr, DATE_FORMATTER_YYYY_MM_DD);
                            } catch (DateTimeParseException e) {
                                System.err.println("날짜 파싱 오류: " + dateStr + " - " + e.getMessage());
                                return null;
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