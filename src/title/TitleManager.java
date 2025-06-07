package title;

import data.UserData;
import java.util.ArrayList;
import java.util.List;

public class TitleManager {
    // 누적 경험치 기반 칭호 목록
    private static final String[] EXP_TITLES = {
            "루틴새싹", "꾸준이", "루틴러버", "하루장인",
            "일상마스터", "오늘도성공적", "생활요정", "고인물"
    };

    // 누적 경험치 임계값
    private static final int[] EXP_THRESHOLDS = {
            10000, 20000, 30000, 40000,
            50000, 60000, 70000, 71500
    };

    // 회차 기반 칭호 목록 (n회차)
    private static final String[] CYCLE_TITLES = {
            "1회차", "2회차", "3회차", "4회차", "5회차+"
    };

    // 누적 경험치 기반 칭호 체크
    public static void checkExpTitles(UserData user) {
        int totalExp = user.getTotalExp();

        for (int i = 0; i < EXP_THRESHOLDS.length; i++) {
            // 고인물 칭호는 특별 조건 (5회차 이상)이 필요
            if (i == EXP_TITLES.length - 1) {
                if (totalExp >= EXP_THRESHOLDS[i] && user.getCycle() >= 5 && !user.getOwnedTitles().contains(EXP_TITLES[i])) {
                    addTitle(user, EXP_TITLES[i]);
                }
            } else {
                if (totalExp >= EXP_THRESHOLDS[i] && !user.getOwnedTitles().contains(EXP_TITLES[i])) {
                    addTitle(user, EXP_TITLES[i]);
                }
            }
        }
    }

    // 회차 기반 칭호 체크
    public static void checkCycleTitle(UserData user) {
        int cycle = user.getCycle();
        String newTitle = null;

        if (cycle >= 5) {
            newTitle = CYCLE_TITLES[4]; // "5회차+"
        } else if (cycle > 0) {
            newTitle = CYCLE_TITLES[cycle - 1];
        }

        if (newTitle != null && !user.getOwnedTitles().contains(newTitle)) {
            addTitle(user, newTitle);
        }
    }

    // 고인물 칭호 체크 (레벨 초기화 4회 이상)
    public static void checkVeteranTitle(UserData user) {
        if (user.getCycle() >= 4 && !user.getOwnedTitles().contains("고인물")) {
            addTitle(user, "고인물");
        }
    }

    // 칭호 추가 (내부 메서드)
    private static void addTitle(UserData user, String title) {
        if (!user.getOwnedTitles().contains(title)) {
            user.getOwnedTitles().add(title);
            System.out.println("\n🎉 새로운 칭호 획득: " + title + "!");

            // 첫 칭호이면 자동 장착
            if (user.getCurrentTitle().isEmpty()) {
                user.setCurrentTitle(title);
                System.out.println("  - '" + title + "' 칭호를 장착했습니다!");
            }
        }
    }
}