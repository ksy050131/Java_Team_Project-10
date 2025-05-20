package account;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import data.UserData;

public class Database {
    private static final String FILE_PATH = "database.json";
    private static Gson gson = new Gson();

    /**
     * logindata save, load, update, upload 기능
     * + userdata save, load, update 기능 추가
     */

    // UserData 저장과 구분하기 위해 이름 변경
    public static List<LoginData> loadLoginData() {
        List<LoginData> users = new ArrayList<>();
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
                LoginData user = gson.fromJson(jsonData, LoginData.class);
                if (user != null) {
                    users.add(user); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void saveLoginData(List<LoginData> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (LoginData user : users) {
                user.setUpdated(user.getUpdated()); 
                String jsonData = gson.toJson(user);
                writer.write(jsonData + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //사용자 등록
    public static boolean uploadLoginData(LoginData userData) {
        List<LoginData> users = loadLoginData();
        
        // 중복된 아이디가 있는지 확인
        for (LoginData user : users) {
            if (user.getUserId().equals(userData.getUserId())) {
                System.out.println("이미 존재하는 아이디입니다.");
                return false;
            }
        }

        users.add(userData);  // 새 사용자 추가
        saveLoginData(users);  // 모든 사용자 데이터를 파일에 저장
        return true;
    }
    
    //사용자 업데이트
    public static boolean updateLoginData(LoginData updatedUser) {
        List<LoginData> users = loadLoginData();
        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updatedUser.getUserId())) {
                users.set(i, updatedUser); // 기존 유저 데이터 수정
                found = true;
                break;
            }
        }

        if (found) {
            saveLoginData(users);
            return true;
        } else {
            System.out.println("해당 아이디의 사용자를 찾을 수 없습니다.");
            return false;
        }
    }

    // 아이디로 사용자 찾기
    public static LoginData findId(String userId) {
        List<LoginData> users = loadLoginData();
        for (LoginData user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    // 아이디 존재 여부 확인
    public static boolean isUserExists(String userId) {
        return findId(userId) != null;
    }

    // 아이디 찾기
    public static String findId(String phoneNumber, String birthDate, String name) {
        List<LoginData> users = loadLoginData();
        for (LoginData user : users) {
            if (user.getPhoneNumber().equals(phoneNumber) && user.getBirthDate().equals(birthDate) && user.getUsername().equals(name)) {
                return user.getUserId();
            }
        }
        return null;
    }

    // UserData 관련 메소드

    // UserData 저장
    public static void saveUserData(List<UserData> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.json"))) {
            for (UserData user : users) {
                String jsonData = gson.toJson(user);
                writer.write(jsonData + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // UserData load
    public static List<UserData> loadUserData() {
        List<UserData> users = new ArrayList<>();
        File file = new File("user_data.json");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.json"))) {
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

        // !found 일 때 (아이디를 찾지 못했을 때) 해당 아이디를 저장하도록 수정
        if (!found) {
            System.out.println("해당 아이디의 사용자를 찾을 수 없습니다.");
            users.add(updatedUser);
            System.out.println("사용자 추가 완료");
        }

        saveUserData(users);
        return true;
    }

    // userId를 통해 user_data.json 파일에서 사용자를 찾음.
    public static UserData findUserData(String userId) {
        List<UserData> users = loadUserData();
        for (UserData user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
}

