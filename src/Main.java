import account.Account;
import data.DataBase;
import data.UserData;
import routine.Routine;
import routine.RoutineManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("사용자 이름 입력: ");
        String username = scanner.nextLine();
        System.out.print("비밀번호 입력: ");
        String password = scanner.nextLine();

        Account account = new Account(username, password);
        UserData user = new UserData(username);
        DataBase.registerUser(user);

        System.out.println("로그인 완료! 루틴을 추가해보세요.");

        while (true) {
            System.out.println("\n1. 루틴 추가\n2. 루틴 목록 보기\n3. 루틴 완료\n4. 경험치 상태 보기\n5. 종료");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 개행 처리

            switch (choice) {
                case 1:
                    System.out.print("루틴 제목 입력: ");
                    String title = scanner.nextLine();
                    Routine newRoutine = new Routine(title);
                    user.addRoutine(newRoutine);
                    System.out.println("루틴이 추가되었습니다.");
                    break;

                case 2:
                    RoutineManager.listRoutines(user);
                    break;

                case 3:
                    RoutineManager.listRoutines(user);
                    System.out.print("완료할 루틴 번호 입력: ");
                    int index = scanner.nextInt() - 1;
                    if (index >= 0 && index < user.getRoutineList().size()) {
                        RoutineManager.completeRoutine(user, user.getRoutineList().get(index));
                    } else {
                        System.out.println("잘못된 번호입니다.");
                    }
                    break;

                case 4:
                    user.getExpManager().printStatus();
                    break;

                case 5:
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println("올바른 메뉴를 선택하세요.");
            }
        }
    }
}
