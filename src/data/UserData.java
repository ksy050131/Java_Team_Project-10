package data;

import routine.Routine;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserData {
    private String username;
    private String userId;
    private String phoneNumber;
    private String birthDate;
    private String password;
    private int level;
    private int exp;
    private int needExp;
    private List<Routine> routines;
    private String lastUpdateDate;

    public UserData(String username, String userId, String phoneNumber,
                    String birthDate, String password, int level,
                    int exp, int needExp, List<Routine> routines) {
        this.username = username;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.password = password;
        this.level = level;
        this.exp = exp;
        this.needExp = needExp;
        this.routines = routines != null ? routines : new ArrayList<>();
        this.lastUpdateDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserId() { return userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }
    public int getNeedExp() { return needExp; }
    public void setNeedExp(int needExp) { this.needExp = needExp; }
    public List<Routine> getRoutines() { return routines; }
    public void setRoutines(List<Routine> routines) { this.routines = routines; }
    public String getLastUpdateDate() { return lastUpdateDate; }
    public void setLastUpdateDate(String lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }
}



