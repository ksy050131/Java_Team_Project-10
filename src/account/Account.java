package account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Account {
    private LoginData currentUser;

    // 비밀번호 암호화
    private String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 로그인 처리
    public boolean login(String userId, String password) {
        LoginData user = Database.findId(userId);
        if (user != null && user.getPassword().equals(encrypt(password))) {
            currentUser = user;
            return true;
        }
        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        return false;
    }

    // 회원가입 처리
    public boolean register(String username, String userId, String password, String phoneNumber, String birthDate) {
        if (Database.isUserExists(userId)) {
            System.out.println("이미 존재하는 아이디입니다.");
            return false;
        }

        //레벨과 경험치통 수정
        /**
         * comment: logindata와 userdata를 분리해놓은 상태인데
         * 여기서 레벨, 경험치를 수정해야할 필요가 있을까?
         * logindata 와 userdata를 연동하는 방법을 찾아보는 것이 좋을 듯
         */
        LoginData newUser = new LoginData(username, userId, phoneNumber, birthDate, encrypt(password), 0, 0, 100);
        boolean isUploaded = Database.uploadLoginData(newUser);  // 사용자 데이터 업로드
        if (isUploaded) {
            System.out.println("회원가입이 완료되었습니다.");
            return true;
        } else {
            System.out.println("회원가입에 실패했습니다.");
            return false;
        }
    }

    // 비밀번호 변경 처리 (본인 인증 후)
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        LoginData user = Database.findId(userId);
        if (user != null && user.getPassword().equals(encrypt(oldPassword))) {
            user.setPassword(encrypt(newPassword));  // 비밀번호 변경
            Database.updateLoginData(user);  // 변경된 정보 저장
            System.out.println("비밀번호가 성공적으로 변경되었습니다.");
            return true;
        }
        System.out.println("현재 비밀번호가 잘못되었습니다.");
        return false;
    }

    // 아이디 찾기
    public String findId(String phoneNumber, String birthDate, String name) {
        return Database.findId(phoneNumber, birthDate, name);
    }

    // 프로그램 시작
    public void start() {
        Scanner scanner = new Scanner(System.in);  // Scanner 객체 생성
        while (true) {
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("3. 아이디 찾기");
            System.out.println("4. 비밀번호 변경");
            System.out.println("5. 종료");
            System.out.print("선택: ");
            String input = scanner.nextLine();

            // 잘못된 입력에 대한 처리
            try {
                int choice = Integer.parseInt(input);
                if (choice == 1) {
                    System.out.print("아이디: ");
                    String userId = scanner.nextLine();
                    System.out.print("비밀번호: ");
                    String password = scanner.nextLine();
                    if (login(userId, password)) {
                        System.out.println("로그인 성공!");
                        //다음 메뉴 진입
                    } else {
                        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
                    }
                } else if (choice == 2) {
                    System.out.print("이름: ");
                    String username = scanner.nextLine();
                    System.out.print("아이디: ");
                    String userId = scanner.nextLine();
                    System.out.print("비밀번호: ");
                    String password = scanner.nextLine();
                    System.out.print("전화번호: ");
                    String phoneNumber = scanner.nextLine();
                    System.out.print("생년월일 (YYYY-MM-DD): ");
                    String birthDate = scanner.nextLine();
                    register(username, userId, password, phoneNumber, birthDate);
                } else if (choice == 3) {
                    System.out.print("본명: ");
                    String name = scanner.nextLine();
                    System.out.print("전화번호: ");
                    String phoneNumber = scanner.nextLine();
                    System.out.print("생년월일 (YYYY-MM-DD): ");
                    String birthDate = scanner.nextLine();
                    String userId = findId(phoneNumber, birthDate, name);
                    if (userId != null) {
                        System.out.println("아이디: " + userId);
                    } else {
                        System.out.println("아이디를 찾을 수 없습니다.");
                    }
                } else if (choice == 4) {
                    System.out.print("아이디: ");
                    String userId = scanner.nextLine();
                    System.out.print("현재 비밀번호: ");
                    String oldPassword = scanner.nextLine();
                    System.out.print("새로운 비밀번호: ");
                    String newPassword = scanner.nextLine();
                    changePassword(userId, oldPassword, newPassword);
                } else if (choice == 5) {
                    System.out.println("프로그램을 종료합니다.");
                    break;
                } else {
                    System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
}
