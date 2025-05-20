
import account.Account;
import account.Database;
import account.LoginData;
import exp.ExpUser;
import data.UserData;
import routine.Routine;
import java.util.List;
import java.util.Scanner;

/**
 * 콘솔 기반 실행 메인 흐름 클래스
 * 로그인, 루틴 관리, 경험치 시스템 전반 흐름을 담당
 */
public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Account account = new Account();

    public static void main(String[] args) {
        System.out.println("=== 루틴 코치 콘솔 버전 시작 ===");

        LoginData loginData = loginFlow();
        if (loginData == null) return;

        // ** 루친 + exp 포함된 UserData 불러오기
        UserData userData = Database.findUserData(loginData.getUserId());

        // UserData로 변환
        if (userData == null) {
            userData = new UserData(
                    loginData.getUsername(),
                    loginData.getUserId(),
                    loginData.getPhoneNumber(),
                    loginData.getBirthDate(),
                    loginData.getPassword(),
                    loginData.getLevel(),
                    loginData.getExp(),
                    loginData.getNeedExp()
            );

            // 저장도 같이 함
            List<UserData> allUsers = Database.loadUserData();
            allUsers.add(userData);
            Database.saveUserData(allUsers);
        }

        // 루틴, EXP 관리 가능한 유저 객체 생성
        ExpUser user = new ExpUser(userData);

        routineMenu(user);
        System.out.println("저장 완료. 프로그램을 종료합니다.");
    }

    private static LoginData loginFlow() {
        while (true) {
            System.out.println("\n1. 로그인\n2. 회원가입\n0. 종료");
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
                        return account.getCurrentUser();
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
                    account.register(name, id, pw, phone, birth);
                }
                case "0" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return null;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private static void routineMenu(ExpUser user) {
        while (true) {
            System.out.println("\n[루틴 메뉴 - " + user.getUserData().getUsername() + "]");
            user.getExpManager().printStatus();
            System.out.println("1. 루틴 목록 보기");
            System.out.println("2. 루틴 추가");
            System.out.println("3. 루틴 완료");
            System.out.println("4. 루틴 삭제");
            System.out.println("0. 로그아웃");
            System.out.print("선택: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    List<Routine> routines = user.getRoutineManager().getAllRoutines();
                    if (routines.isEmpty()) {
                        System.out.println("루틴이 없습니다.");
                    } else {
                        System.out.println("== 루틴 목록 ==");
                        for (Routine r : routines) {
                            System.out.println("[" + (r.isCompleted() ? "✔" : " ") + "] "
                                    + r.getContent() + " (EXP: " + r.getRewardExp() + ", ID: " + r.getId() + ")");
                        }
                    }
                }
                case "2" -> {
                    System.out.print("루틴 내용 입력: ");
                    String content = scanner.nextLine();
                    System.out.print("보상 경험치 입력: ");
                    int reward = Integer.parseInt(scanner.nextLine());
                    user.getRoutineManager().addRoutine(content, reward);
                }
                case "3" -> {
                    List<Routine> routines = user.getRoutineManager().getAllRoutines();

                    if (routines.isEmpty()) {
                        System.out.println("완료할 루틴이 없습니다.");
                        break;
                    }
                    // 루틴 목록 출력
                    System.out.println("== 루틴 목록 ==");
                    for (int i = 0; i < routines.size(); i++) {
                        Routine r = routines.get(i);
                        System.out.printf("%d. [%s] %s (EXP: %d)\n", i + 1, r.isCompleted() ? "✔" : " ", r.getContent(), r.getRewardExp());
                    }

                    System.out.print("완료할 루틴 번호 선택: ");
                    int choice = Integer.parseInt(scanner.nextLine()) - 1;
                    if (choice >= 0 && choice < routines.size()) {
                        String routineId = routines.get(choice).getId();
                        user.completeRoutine(routineId);
                    } else {
                        System.out.println("잘못된 번호입니다.");
                    }
                }
                case "4" -> {
                    List<Routine> routines = user.getRoutineManager().getAllRoutines();

                    if (routines.isEmpty()) {
                        System.out.println("삭제할 루틴이 없습니다.");
                        break;
                    }
                    // 루틴 목록 출력
                    System.out.println("== 루틴 목록 ==");
                    for (int i = 0; i < routines.size(); i++) {
                        Routine r = routines.get(i);
                        System.out.printf("%d. [%s] %s (EXP: %d)\n", i + 1, r.isCompleted() ? "✔" : " ", r.getContent(), r.getRewardExp());
                    }

                    System.out.print("삭제할 루틴 번호 선택: ");
                    int choice = Integer.parseInt(scanner.nextLine()) - 1;
                    if (choice >= 0 && choice < routines.size()) {
                        String routineId = routines.get(choice).getId();
                        user.getRoutineManager().getRoutineById(routineId);
                    } else {
                        System.out.println("잘못된 번호입니다.");
                    }
                }
                case "0" -> {
                    System.out.println("로그아웃합니다.");

                    // 로그아웃 전 json파일에 저장
                    UserData userData = user.getUserData();
                    boolean saved = Database.updateUserData(userData);

                    if (saved) {
                        System.out.println("데이터가 저장되었습니다.");
                    } else {
                        System.out.println("저장에 실패하였습니다.");
                    }

                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }
}
