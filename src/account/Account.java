package account;

import data.UserData;
import data.Database;
import routine.Routine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Account {
    private UserData User;

    public UserData getUser() {
        return User;
    }

    // 비밀번호를 SHA-256으로 암호화 (내부에서 userId 기반 pepper 사용)
    public String encrypt(String password) {
        try {
            if (User == null) return null;  // 로그인 전 또는 사용자 정보 없음
            String userId = User.getUserId();
            String pepper = Database.getPepper(userId); // 사용자별 pepper 조회
            if (pepper == null) return null;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String combined = password + pepper;
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

    // 새로운 pepper 생성
    private String generatePepper() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 로그인
    public boolean login(String userId, String password) {
        UserData user = Database.findUserDataById(userId);
        if (user != null) {
            String pepper = Database.getPepper(userId);
            if (pepper == null) {
                System.out.println("암호화 키를 찾을 수 없습니다.");
                return false;
            }
            this.User = user;  // User 설정
            if (user.getPassword().equals(encrypt(password))) {
                return true;
            }
            this.User = null; // 실패 시 User 초기화
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

        String pepper = generatePepper(); // pepper 생성
        Database.savePepper(userId, pepper); // pepper 저장

        // 임시 User 설정 후 비밀번호 암호화
        this.User = new UserData(username,
                userId,
                phoneNumber,
                birthDate,
                "",
                1, 0,
                100, 0,
                0,
                "",
                new ArrayList<>(),
                new ArrayList<>());
        String encryptedPassword = encrypt(password);
        this.User = null;

        UserData newUser = new UserData(
                username, userId, phoneNumber, birthDate, encryptedPassword,
                1, 0, 100, 0, 0, "", new ArrayList<>(), new ArrayList<>()
        );

        List<UserData> allUsers = Database.loadUserData();
        allUsers.add(newUser);
        Database.saveUserData(allUsers);

        System.out.println("회원가입이 완료되었습니다.");
        return true;
    }

    // 비밀번호 변경 (로그인된 경우)
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        UserData user = Database.findUserDataById(userId);
        if (user != null) {
            this.User = user;
            if (user.getPassword().equals(encrypt(oldPassword))) {
                user.setPassword(encrypt(newPassword));
                Database.updateUserData(user);
                System.out.println("비밀번호가 성공적으로 변경되었습니다.");
                return true;
            }
            this.User = null;
        }
        System.out.println("현재 비밀번호가 잘못되었습니다.");
        return false;
    }

    // 비밀번호 변경 (비밀헌호를 모를 때, 로그아웃 상태에서 사용)
    public boolean changePassword2(String username, String phoneNumber, String birthDate, String newPassword) {
        String userId = Database.findId(phoneNumber, birthDate, username);
        if (userId == null) {
            System.out.println("일치하는 사용자 정보를 찾을 수 없습니다.");
            return false;
        }

        UserData user = Database.findUserDataById(userId);
        if (user == null || Database.getPepper(userId) == null) {
            System.out.println("사용자 정보 또는 암호화 키가 존재하지 않습니다.");
            return false;
        }

        this.User = user;
        user.setPassword(encrypt(newPassword));
        Database.updateUserData(user);
        this.User = null;

        System.out.println("비밀번호가 성공적으로 재설정되었습니다.");
        return true;
    }

    // 아이디 찾기
    public String findId(String phoneNumber, String birthDate, String name) {
        return Database.findId(phoneNumber, birthDate, name);
    }

    // 회원 탈퇴
    public boolean deleteAccount(String userId, String password) {
        UserData user = Database.findUserDataById(userId);
        if (user != null) {
            this.User = user;
            if (user.getPassword().equals(encrypt(password))) {
                List<UserData> users = Database.loadUserData();
                users.removeIf(u -> u.getUserId().equals(userId));

                try {
                    Database.saveUserData(users);
                } catch (Exception e) {
                    System.err.println("회원 데이터 저장 중 오류 발생: " + e.getMessage());
                    return false;
                }

                Database.deletePepper(userId);
                System.out.println("회원 탈퇴가 완료되었습니다.");
                return true;
            }
            this.User = null;
        }
        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        return false;
    }


    // 시작 메뉴
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
                    case 5 -> {
                        System.out.print("아이디: ");
                        String userId = scanner.nextLine();
                        System.out.print("비밀번호: ");
                        String password = scanner.nextLine();
                        deleteAccount(userId, password);
                    }
                    case 6 -> {
                        System.out.println("프로그램을 종료합니다.");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
}
