package app;

import account.Account;
import data.Database;
import data.Gemini;
import data.UserData;
import exp.ExpUser;
import routine.DailyRoutine;
import routine.Routine;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MainAppConsole {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Account account = new Account();

    public static void main(String[] args) {
        System.out.println("=== 루틴 코치 콘솔 버전 시작 ===");
        System.out.println("개발자: 김철수, 이영희, 박지민");
        System.out.println("버전: 2.0 (통합 루틴 시스템)\n");

        UserData userData = loginFlow();
        if (userData == null) return;

        UserData storedData = Database.findUserDataById(userData.getUserId());

        if (storedData == null) {
            List<UserData> allUsers = Database.loadUserData();
            allUsers.add(userData);
            Database.saveUserData(allUsers);
        } else {
            userData = storedData;
        }
        routineMenu(userData);

        System.out.println("프로그램을 종료합니다.");
    }

    private static UserData loginFlow() {
        while (true) {
            System.out.println("\n[로그인 메뉴]");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("0. 종료");
            System.out.print("선택: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    System.out.print("아이디: ");
                    String id = scanner.nextLine();
                    System.out.print("비밀번호: ");
                    String pw = scanner.nextLine();
                    if (account.login(id, pw)) {
                        System.out.println("로그인 성공!");
                        return account.getUser();
                    } else {
                        System.out.println("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
                    }
                }
                case "2" -> {
                    System.out.print("이름: ");
                    String name = scanner.nextLine();
                    System.out.print("아이디: ");
                    String id = scanner.nextLine();
                    System.out.print("비밀번호: ");
                    String pw = scanner.nextLine();
                    System.out.print("전화번호: ");
                    String phone = scanner.nextLine();
                    System.out.print("생년월일 (YYYY-MM-DD): ");
                    String birth = scanner.nextLine();
                    if (account.register(name, id, pw, phone, birth)) {
                        System.out.println("회원가입 성공! 로그인 해주세요.");
                    }
                }
                case "0" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return null;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private static void routineMenu(UserData userData) {
        ExpUser expUser = new ExpUser(userData);
        System.out.println("\n환영합니다, " + userData.getUsername() + "님!");

        while (true) {
            System.out.println("\n[메인 메뉴]");
            expUser.getExpManager().printStatus();
            System.out.println("1. 루틴 목록 보기");
            System.out.println("2. 일반 루틴 추가");
            System.out.println("3. 일일 루틴 추가");
            System.out.println("4. 루틴 완료 처리");
            System.out.println("5. 루틴 삭제");
            System.out.println("6. 비밀번호 변경");
            System.out.println("7. 회원탈퇴");
            System.out.println("0. 로그아웃");
            System.out.print("선택: ");
            String input = scanner.nextLine();

            try {
                switch (input) {
                    case "1" -> showRoutines(expUser);
                    case "2" -> addRoutine(expUser);
                    case "3" -> addDailyRoutine(expUser);
                    case "4" -> completeRoutine(expUser);
                    case "5" -> deleteRoutine(expUser);
                    case "6" -> changePassword(expUser);
                    case "7" -> deleteAccount(expUser);
                    case "0" -> {
                        Database.updateUserData(userData);
                        System.out.println("로그아웃합니다.");
                        return;
                    }
                    default -> System.out.println("잘못된 입력입니다.");
                }
            } catch (IOException e) {
                System.out.println("데이터 처리 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }
    }

    private static void showRoutines(ExpUser expUser) throws IOException {
        List<Routine> routines = expUser.getUserData().getRoutines();
        if (routines.isEmpty()) {
            System.out.println("\n등록된 루틴이 없습니다.");
            return;
        }

        System.out.println("\n[루틴 목록]");
        System.out.println("--------------------------------------------");
        for (int i = 0; i < routines.size(); i++) {
            Routine r = routines.get(i);
            String type = (r instanceof DailyRoutine) ? "[일일]" : "[일반]";
            System.out.printf("%d. %s %s (난이도: %d)\n",
                    i + 1, type, r.getContent(), r.getDifficulty());
            System.out.printf("   상태: %s | ID: %s\n",
                    r.isCompleted() ? "완료" : "미완료", r.getId());

            if (r instanceof DailyRoutine dr) {
                System.out.printf("   연속 완료: %d일\n", dr.getStreakCount());
            }
            System.out.println("--------------------------------------------");
        }
    }

    private static void addRoutine(ExpUser expUser) throws IOException {
        System.out.print("\n루틴 내용 입력: ");
        String content = scanner.nextLine();

        if (content.isBlank()) {
            System.out.println("루틴 내용은 비울 수 없습니다.");
            return;
        }

        int difficulty = new Gemini().getDif(content);
        expUser.getRoutineManager().addRoutine(content, difficulty);

        System.out.println("\n일반 루틴이 추가되었습니다!");
        System.out.println("난이도: " + difficulty);
    }

    private static void addDailyRoutine(ExpUser expUser) throws IOException {
        System.out.print("\n루틴 내용 입력: ");
        String content = scanner.nextLine();

        if (content.isBlank()) {
            System.out.println("루틴 내용은 비울 수 없습니다.");
            return;
        }

        int difficulty = new Gemini().getDif(content);
        expUser.getRoutineManager().addDailyRoutine(content, difficulty);

        System.out.println("\n일일 루틴이 추가되었습니다!");
        System.out.println("난이도: " + difficulty);
    }

    private static void completeRoutine(ExpUser expUser) throws IOException {
        List<Routine> routines = expUser.getUserData().getRoutines();
        if (routines.isEmpty()) {
            System.out.println("\n완료할 루틴이 없습니다.");
            return;
        }

        showRoutines(expUser);
        System.out.print("\n완료할 루틴 번호 선택: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice >= 0 && choice < routines.size()) {
                expUser.completeRoutine(routines.get(choice).getId());
            } else {
                System.out.println("잘못된 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요.");
        }
    }

    private static void deleteRoutine(ExpUser expUser) throws IOException {
        List<Routine> routines = expUser.getUserData().getRoutines();
        if (routines.isEmpty()) {
            System.out.println("\n삭제할 루틴이 없습니다.");
            return;
        }

        showRoutines(expUser);
        System.out.print("\n삭제할 루틴 번호 선택: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice >= 0 && choice < routines.size()) {
                Routine routine = routines.get(choice);
                if (expUser.getRoutineManager().deleteRoutine(routine.getId())) {
                    System.out.println("\n'" + routine.getContent() + "' 루틴이 삭제되었습니다.");
                }
            } else {
                System.out.println("잘못된 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요.");
        }
    }

    private static void changePassword(ExpUser expUser) throws IOException {
        System.out.println("\n[비밀번호 변경]");
        System.out.print("현재 비밀번호: ");
        String oldPassword = scanner.nextLine();
        System.out.print("새로운 비밀번호: ");
        String newPassword = scanner.nextLine();

        if (account.changePassword(expUser.getUserData().getUserId(), oldPassword, newPassword)) {
            expUser.getUserData().setPassword(account.encrypt(newPassword));
            System.out.println("비밀번호가 변경되었습니다.");
        } else {
            System.out.println("비밀번호 변경에 실패했습니다.");
        }
    }

    private static void deleteAccount(ExpUser expUser) throws IOException {
        System.out.println("\n[회원 탈퇴]");
        System.out.println("경고: 계정을 삭제하면 모든 데이터가 영구적으로 삭제됩니다.");
        System.out.print("계속하시려면 비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        if (account.deleteAccount(expUser.getUserData().getUserId(), password)) {
            System.out.println("계정이 삭제되었습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } else {
            System.out.println("비밀번호가 일치하지 않습니다.");
        }
    }
}