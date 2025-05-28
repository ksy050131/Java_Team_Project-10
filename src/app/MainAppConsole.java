package app;

import account.Account;
import data.Database;
import data.UserData;
import routine.Routine;
import exp.ExpUser;
import java.util.List;
import java.util.Scanner;

/**
 * 콘솔 기반 실행 메인 흐름 클래스
 * 로그인, 루틴 관리, 경험치 시스템 전반 흐름을 담당
 */

public class MainAppConsole {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Account account = new Account();

    public static void main(String[] args) {
        System.out.println("=== 루틴 코치 콘솔 버전 시작 ===");

        UserData userData = loginFlow();
        if (userData == null) return;

        UserData storedData = Database.findUserDataById(userData.getUserId());

        if (storedData == null) {
            List<UserData> allUsers = Database.loadUserData();
            allUsers.add(userData);
            Database.saveUserData(allUsers);
        } else {
            userData = storedData; // 로그인 후 DB에서 가져온 전체 데이터를 사용자 객체에 반영
        }

        routineMenu(userData);

        System.out.println("저장 완료. 프로그램을 종료합니다.");
    }

    private static UserData loginFlow() {
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


    /**
     * 로그인 후 페이지에 비밀번호 변경 및 회원탈퇴 기능 추가
     * 환경설정 메뉴같은 걸 만들어서 비밀번호 변경과 회원탈퇴를 거기다가 집어넣어도 될 듯
     * 그 외에 다른 기능도 있으면 괜찮을 듯 ex) 캐릭터 설정
     *
     */
    private static void routineMenu(UserData userData) {
        ExpUser expUser = new ExpUser(userData);
        while (true) {
            System.out.println("\n[루틴 메뉴 - " + userData.getUsername() + "]");
            expUser.getExpManager().printStatus();

            System.out.println("1. 루틴 목록 보기");
            System.out.println("2. 루틴 추가");
            System.out.println("3. 루틴 완료");
            System.out.println("4. 루틴 삭제");
            System.out.println("5. 비밀번호 변경");
            System.out.println("6. 회원탈퇴");
            System.out.println("0. 로그아웃");
            System.out.print("선택: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    List<Routine> routines = userData.getRoutines();
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
                    Routine routine = new Routine(content, reward);
                    userData.getRoutines().add(routine);
                }
                case "3" -> {
                    List<Routine> routines = userData.getRoutines();

                    if (routines.isEmpty()) {
                        System.out.println("완료할 루틴이 없습니다.");
                        break;
                    }

                    System.out.println("== 루틴 목록 ==");
                    for (int i = 0; i < routines.size(); i++) {
                        Routine r = routines.get(i);
                        System.out.printf("%d. [%s] %s (EXP: %d)\n", i + 1, r.isCompleted() ? "✔" : " ", r.getContent(), r.getRewardExp());
                    }

                    System.out.print("완료할 루틴 번호 선택: ");
                    int choice = Integer.parseInt(scanner.nextLine()) - 1;

                    if (choice >= 0 && choice < routines.size()) {
                        Routine r = routines.get(choice);
                        if (!r.isCompleted()) {
                            expUser.completeRoutine(r.getId());  // 경험치 증가 + 레벨업 포함
                        } else {
                            System.out.println("이미 완료된 루틴입니다.");
                        }
                    } else {
                        System.out.println("잘못된 번호입니다.");
                    }
                }

                case "4" -> {
                    List<Routine> routines = userData.getRoutines();

                    if (routines.isEmpty()) {
                        System.out.println("삭제할 루틴이 없습니다.");
                        break;
                    }
                    System.out.println("== 루틴 목록 ==");
                    for (int i = 0; i < routines.size(); i++) {
                        Routine r = routines.get(i);
                        System.out.printf("%d. [%s] %s (EXP: %d)\n", i + 1, r.isCompleted() ? "✔" : " ", r.getContent(), r.getRewardExp());
                    }

                    System.out.print("삭제할 루틴 번호 선택: ");
                    int choice = Integer.parseInt(scanner.nextLine()) - 1;
                    if (choice >= 0 && choice < routines.size()) {
                        routines.remove(choice);
                        System.out.println("루틴이 삭제되었습니다.");
                    } else {
                        System.out.println("잘못된 번호입니다.");
                    }
                }

                case "5" -> {
                    System.out.print("현재 비밀번호: ");
                    String oldPassword = scanner.nextLine();
                    System.out.print("새로운 비밀번호: ");
                    String newPassword = scanner.nextLine();
                    account.changePassword(userData.getUserId(), oldPassword, newPassword);
                    userData.setPassword(account.encrypt(newPassword));
                }

                case "6" -> {
                    System.out.print("비밀번호: ");
                    String password = scanner.nextLine();
                    if (account.deleteAccount(userData.getUserId(), password)) {
                        System.out.println("데이터가 삭제되었습니다.");
                    }
                    return;
                }
                case "0" -> {
                    System.out.println("로그아웃합니다.");
                    boolean saved = Database.updateUserData(userData);
                    if (saved)
                        System.out.println("데이터가 저장되었습니다.");
                    else
                        System.out.println("저장에 실패하였습니다.");

                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }
}
