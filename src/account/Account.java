package account;

import data.UserData;
import data.Database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Account {
    private UserData User;

    // 비밀번호 뒤에 붙일 고정 키 (보안을 강화하는 용도)
    private static final String SECRET_KEY = "MySecretKey123!";

    public UserData getUser() {
        return User;
    }

    // 비밀번호 암호화 (비밀번호 뒤에 SECRET_KEY를 붙인 후 SHA-256 해시)
    public String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 비밀번호 뒤에 SECRET_KEY 붙이기
            String combined = password + SECRET_KEY;
            byte[] hash = md.digest(combined.getBytes());
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

    // 로그인
    public boolean login(String userId, String password) {
        UserData user = Database.findUserDataById(userId);
        if (user != null && user.getPassword().equals(encrypt(password))) {
            User = user;
            return true;
        }
        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        return false;
    }

    // 회원가입
    public boolean register(String username, String userId, String password, String phoneNumber, String birthDate) {
        if (Database.isUserExists(userId)) {
            System.out.println("이미 존재하는 아이디입니다.");
            return false;
        }

        // 비밀번호 암호화는 encrypt 메서드가 처리
        UserData newUser = new UserData(username, userId, phoneNumber, birthDate, encrypt(password), 0, 0, 100, new ArrayList<>());
        List<UserData> allUsers = Database.loadUserData();
        allUsers.add(newUser);
        Database.saveUserData(allUsers);

        System.out.println("회원가입이 완료되었습니다.");
        return true;
    }

    // 비밀번호 변경1
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        UserData user = Database.findUserDataById(userId);
        if (user != null && user.getPassword().equals(encrypt(oldPassword))) {
            user.setPassword(encrypt(newPassword));
            Database.updateUserData(user);
            System.out.println("비밀번호가 성공적으로 변경되었습니다.");
            return true;
        }
        System.out.println("현재 비밀번호가 잘못되었습니다.");
        return false;
    }
    // 비밀번호 변경2 추가예정 (잊어버렸을 때 사용)

    // 아이디찾기 기능(잊어버렸을 때 시용)
    public String findId(String phoneNumber, String birthDate, String name) {
        return Database.findId(phoneNumber, birthDate, name);
    }

    // 회원탈퇴
    public boolean deleteAccount(String userId, String password) {
        UserData user = Database.findUserDataById(userId);
        if (user != null && user.getPassword().equals(encrypt(password))) {
            List<UserData> users = Database.loadUserData();
            users.removeIf(u -> u.getUserId().equals(userId));
            Database.saveUserData(users);
            System.out.println("회원 탈퇴가 완료되었습니다.");
            return true;
        }
        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        return false;
    }

    // 기능 테스트
    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("======");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("3. 아이디 찾기");
            System.out.println("4. 비밀번호 변경");
            System.out.println("5. 회원탈퇴");
            System.out.println("6. 종료");
            System.out.print("선택: ");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> {
                        System.out.print("아이디: ");
                        String userId = scanner.nextLine();
                        System.out.print("비밀번호: ");
                        String password = scanner.nextLine();
                        if (login(userId, password)) {
                            System.out.println("로그인 성공!");
                        }
                    }
                    case 2 -> {
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
                    }
                    case 3 -> {
                        System.out.print("본명: ");
                        String name = scanner.nextLine();
                        System.out.print("전화번호: ");
                        String phoneNumber = scanner.nextLine();
                        System.out.print("생년월일 (YYYY-MM-DD): ");
                        String birthDate = scanner.nextLine();
                        String userId = findId(phoneNumber, birthDate, name);
                        System.out.println(userId != null ? "아이디: " + userId : "아이디를 찾을 수 없습니다.");
                    }
                    case 4 -> {
                        System.out.print("아이디: ");
                        String userId = scanner.nextLine();
                        System.out.print("현재 비밀번호: ");
                        String oldPassword = scanner.nextLine();
                        System.out.print("새로운 비밀번호: ");
                        String newPassword = scanner.nextLine();
                        changePassword(userId, oldPassword, newPassword);
                    }
                    case 6 -> {
                        System.out.println("프로그램을 종료합니다.");
                        scanner.close();
                        return;
                    }
                    case 5 -> {
                        System.out.print("아이디: ");
                        String userId = scanner.nextLine();
                        System.out.print("비밀번호: ");
                        String password = scanner.nextLine();
                        deleteAccount(userId, password);
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
}
