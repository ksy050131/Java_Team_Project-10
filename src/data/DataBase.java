package data;

import java.util.HashMap;

public class DataBase {
    private static HashMap<String, UserData> userMap = new HashMap<>();

    public static void registerUser(UserData userData) {
        userMap.put(userData.getUsername(), userData);
    }

    public static UserData getUserData(String username) {
        return userMap.get(username);
    }

    public static boolean userExists(String username) {
        return userMap.containsKey(username);
    }
}
