package data;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;

/**
 * 자바와 database.json을 연결하는 클래스
 * Database클래스가 database의 데이터를 UserData로 로드
 * 사용자 인증과 같은 특수한 경우는 Database에서 관리
 *
 * 가능하다면 MongoDB와 연결할 예정
 */
public class Database {
    private static final String FILE_PATH = "database.json";
    private static final Gson gson = new Gson();

    // 모든 사용자 데이터 저장
    public static void saveUserData(List<UserData> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (UserData user : users) {
                String jsonData = gson.toJson(user);
                writer.write(jsonData + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 모든 사용자 데이터 로드
    public static List<UserData> loadUserData() {
        List<UserData> users = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String jsonData;
            while ((jsonData = reader.readLine()) != null) {
                UserData user = gson.fromJson(jsonData, UserData.class);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    // 개별 사용자 등록
    public static boolean uploadUserData(UserData userData) {
        List<UserData> users = loadUserData();

        for (UserData user : users) {
            if (user.getUserId().equals(userData.getUserId())) {
                System.out.println("이미 존재하는 아이디입니다.");
                return false;
            }
        }

        users.add(userData);
        saveUserData(users);
        return true;
    }

    // 개별 사용자 업데이트
    public static boolean updateUserData(UserData updatedUser) {
        List<UserData> users = loadUserData();
        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updatedUser.getUserId())) {
                users.set(i, updatedUser);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("해당 아이디의 사용자를 찾을 수 없습니다.");
            users.add(updatedUser);
            System.out.println("사용자 추가 완료");
        }

        saveUserData(users);
        return true;
    }

    // userId로 사용자 찾기
    public static UserData findUserDataById(String userId) {
        List<UserData> users = loadUserData();
        for (UserData user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    // 이름, 전화번호, 생년월일로 아이디 찾기
    public static String findId(String phoneNumber, String birthDate, String name) {
        List<UserData> users = loadUserData();
        for (UserData user : users) {
            if (user.getPhoneNumber().equals(phoneNumber)
                && user.getBirthDate().equals(birthDate)
                && user.getUsername().equals(name)) {
                return user.getUserId();
            }
        }
        return null;
    }

    // 사용자 존재 여부
    public static boolean isUserExists(String userId) {
        return findUserDataById(userId) != null;
    }
}
