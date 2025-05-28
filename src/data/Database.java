package data;

import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String FILE_PATH = "database.json";
    private static final Gson gson = new Gson();

    private static void initializeFile() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            saveUserData(new ArrayList<>());
        }
    }

    public static void saveUserData(List<UserData> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (UserData user : users) {
                writer.write(gson.toJson(user) + "\n");
            }
        }
    }

    public static List<UserData> loadUserData() throws IOException {
        initializeFile();
        List<UserData> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UserData user = gson.fromJson(line, UserData.class);
                if (user != null) users.add(user);
            }
        }
        return users;
    }

    public static boolean uploadUserData(UserData userData) {
        try {
            List<UserData> users = loadUserData();
            if (users.stream().anyMatch(u -> u.getUserId().equals(userData.getUserId()))) {
                return false;
            }
            users.add(userData);
            saveUserData(users);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserData(UserData updatedUser) {
        try {
            List<UserData> users = loadUserData();
            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(updatedUser.getUserId())) {
                    users.set(i, updatedUser);
                    found = true;
                    break;
                }
            }
            if (!found) users.add(updatedUser);
            saveUserData(users);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserData findUserDataById(String userId) {
        try {
            return loadUserData().stream()
                    .filter(u -> u.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String findId(String phoneNumber, String birthDate, String name) {
        try {
            return loadUserData().stream()
                    .filter(u -> u.getPhoneNumber().equals(phoneNumber)
                            && u.getBirthDate().equals(birthDate)
                            && u.getUsername().equals(name))
                    .map(UserData::getUserId)
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isUserExists(String userId) {
        return findUserDataById(userId) != null;
    }
}
