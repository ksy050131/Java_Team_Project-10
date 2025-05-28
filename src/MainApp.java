import account.Account;
import data.Database;
import data.UserData;
import exp.ExpUser;
import routine.Routine;
import routine.StreakRoutine;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Account account = new Account();

    public static void main(String[] args) {
        try {
            // 매일 자정 루틴 초기화 스케줄러
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                System.out.println("\n[시스템] 날짜 변경 감지: " + LocalDate.now());
                try {
                    Database.loadUserData().forEach(user -> {
                        new ExpUser(user).getRoutineManager().resetAllDailyRoutines();
                    });
                } catch (IOException e) {
                    System.err.println("초기화 실패: " + e.getMessage());
                }
            }, 0, 1, TimeUnit.DAYS);

            UserData user = loginFlow();
            if (user != null) {
                ExpUser expUser = new ExpUser(user);
                routineMenu(expUser);
            }
        } catch (Exception e) {
            System.err.println("프로그램 오류: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // 로그인/회원가입 흐름
    private static UserData loginFlow() throws IOException {
        while (true) {
            System.out.println("\n=== 루틴 코치 ===");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("0. 종료");
            String input = safeInput("선택");

            switch (input) {
                case "1" -> {
                    String id = safeInput("아이디");
                    String pw = safeInput("비밀번호");
                    if (account.login(id, pw)) {
                        System.out.println("로그인 성공!");
                        return account.getUser();
                    }
                }
                case "2" -> {
                    String name = safeInput("이름");
                    String id = safeInput("아이디");
                    String pw = safeInput("비밀번호");
                    String phone = safeInput("전화번호");
                    String birth = safeInput("생년월일 (YYYY-MM-DD)");
                    account.register(name, id, pw, phone, birth);
                }
                case "0" -> {
                    return null;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 메인 루틴 메뉴
    private static void routineMenu(ExpUser expUser) throws IOException {
        while (true) {
            System.out.println("\n=== 메인 메뉴 (" + expUser.getUserData().getUsername() + ") ===");
            expUser.getExpManager().printStatus();

            System.out.println("\n1. 루틴 목록 보기");
            System.out.println("2. 일반 루틴 추가");
            System.out.println("3. 일일 루틴 추가");
            System.out.println("4. 연속 완료 루틴 추가");
            System.out.println("5. 루틴 완료 처리");
            System.out.println("6. 루틴 수정");
            System.out.println("7. 비밀번호 변경");
            System.out.println("0. 로그아웃");
            String choice = safeInput("선택");

            switch (choice) {
                case "1" -> showRoutines(expUser);
                case "2" -> addNormalRoutine(expUser);
                case "3" -> addDailyRoutine(expUser);
                case "4" -> addStreakRoutine(expUser);
                case "5" -> completeRoutine(expUser);
                case "6" -> updateRoutine(expUser);
                case "7" -> changePassword(expUser);
                case "0" -> {
                    Database.updateUserData(expUser.getUserData());
                    System.out.println("로그아웃합니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 루틴 목록 출력
    private static void showRoutines(ExpUser expUser) {
        List<Routine> routines = expUser.getUserData().getRoutines();
        if (routines.isEmpty()) {
            System.out.println("등록된 루틴이 없습니다.");
            return;
        }

        System.out.println("\n[루틴 목록]");
        for (int i = 0; i < routines.size(); i++) {
            Routine r = routines.get(i);
            String type = "";
            if (r instanceof StreakRoutine) type = "(연속 보상)";
            else if (r instanceof DailyRoutine) type = "(일일)";

            System.out.printf("%d. [%s] %s %s (EXP: %d, ID: %s)\n",
                    i + 1,
                    r.isCompleted() ? "✔" : " ",
                    r.getContent(),
                    type,
                    r.getBaseExp(),
                    r.getId()
            );

            if (r instanceof StreakRoutine) {
                System.out.printf("   └ 연속 %d일 완료\n", ((StreakRoutine) r).getStreakCount());
            }
        }
    }

    // 일반 루틴 추가
    private static void addNormalRoutine(ExpUser expUser) {
        String content = safeInput("루틴 내용");
        int exp = safeInputInt("기본 EXP");
        expUser.getRoutineManager().addRoutine(content, exp);
        System.out.println("일반 루틴이 추가되었습니다.");
    }

    // 일일 루틴 추가
    private static void addDailyRoutine(ExpUser expUser) {
        String content = safeInput("루틴 내용");
        int exp = safeInputInt("기본 EXP");
        expUser.getRoutineManager().addDailyRoutine(content, exp);
        System.out.println("일일 루틴이 추가되었습니다. 매일 자동 초기화됩니다.");
    }

    // 연속 완료 루틴 추가
    private static void addStreakRoutine(ExpUser expUser) {
        String content = safeInput("루틴 내용");
        int exp = safeInputInt("기본 EXP");
        expUser.getRoutineManager().addStreakRoutine(content, exp);
        System.out.println("연속 완료 루틴이 추가되었습니다. 3/7일 연속 시 보너스!");
    }

    // 루틴 완료 처리
    private static void completeRoutine(ExpUser expUser) {
        List<Routine> routines = expUser.getUserData().getRoutines();
        showRoutines(expUser);
        if (routines.isEmpty()) return;

        int idx = safeInputInt("완료할 루틴 번호") - 1;
        if (idx >= 0 && idx < routines.size()) {
            expUser.completeRoutine(routines.get(idx).getId());
        } else {
            System.out.println("잘못된 번호입니다.");
        }
    }

    // 루틴 수정
    private static void updateRoutine(ExpUser expUser) {
        String id = safeInput("수정할 루틴 ID");
        String content = safeInput("새 내용");
        int exp = safeInputInt("새 EXP");
        if (expUser.getRoutineManager().updateRoutine(id, content, exp)) {
            System.out.println("수정 완료!");
        } else {
            System.out.println("해당 ID의 루틴이 없습니다.");
        }
    }

    // 비밀번호 변경
    private static void changePassword(ExpUser expUser) {
        String oldPw = safeInput("현재 비밀번호");
        String newPw = safeInput("새 비밀번호");
        if (account.changePassword(expUser.getUserData().getUserId(), oldPw, newPw)) {
            expUser.getUserData().setPassword(account.encrypt(newPw));
        }
    }

    // 입력 유틸리티
    private static String safeInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    private static int safeInputInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(safeInput(prompt));
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요!");
            }
        }
    }
}