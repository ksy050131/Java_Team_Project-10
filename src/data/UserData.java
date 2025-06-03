package data;

import routine.Routine;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    // 칭호 시스템 관련 필드
    private int totalExp = 0;
    private int cycle = 0; // 회차 (레벨 10 달성 시 증가)
    private String currentTitle = "";
    private List<String> ownedTitles = new ArrayList<>();
    private int levelResetCount = 0; // 레벨 초기화 횟수 (10레벨 달성 횟수)

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

    // Getters
    public String getUsername() { return username; }
    public String getUserId() { return userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBirthDate() { return birthDate; }
    public String getPassword() { return password; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getNeedExp() { return needExp; }
    public List<Routine> getRoutines() { return routines; }
    public String getLastUpdateDate() { return lastUpdateDate; }
    public int getTotalExp() { return totalExp; }
    public int getCycle() { return cycle; }
    public String getCurrentTitle() { return currentTitle; }
    public List<String> getOwnedTitles() { return ownedTitles; }
    public int getLevelResetCount() { return levelResetCount; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setPassword(String password) { this.password = password; }
    public void setLevel(int level) { this.level = level; }
    public void setExp(int exp) { this.exp = exp; }
    public void setNeedExp(int needExp) { this.needExp = needExp; }
    public void setRoutines(List<Routine> routines) { this.routines = routines; }
    public void setLastUpdateDate(String lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }
    public void setTotalExp(int totalExp) { this.totalExp = totalExp; }
    public void setCycle(int cycle) { this.cycle = cycle; }
    public void setCurrentTitle(String currentTitle) { this.currentTitle = currentTitle; }
    public void setOwnedTitles(List<String> ownedTitles) {
        this.ownedTitles = ownedTitles;
    }
    public void setLevelResetCount(int levelResetCount) {
        this.levelResetCount = levelResetCount;
    }

    // 레벨 초기화 횟수 증가
    public void incrementLevelResetCount() {
        this.levelResetCount++;
    }
}
