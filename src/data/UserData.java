package data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import routine.Routine;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 모든 사용자 데이터를 포함하는 통합 클래스.
 * - JSON 직렬화/역직렬화 대상
 * - RoutineManager, ExpManager와 공유되는 데이터 저장
 */

/**
 * getExp, setExp 등 LoginData와 중복되는 필드가 많아 UserData가
 * LoginData를 상속하는 것으로 수정 -> 더 다듬을 필요가 있음.
 * 각각 class의 기능을 명확하게 정의할 필요가 있음.
 *
 * ->LoginData UserData 병합완료
 */

//현재 저장된 임시데이터들은 아이디와 비밀번호가 같습니다. id:user1 -> pw:user1

public class UserData {
    // 사용자 본인인증 데이터
    private String username; // 사용자 이름(본명)
    private String birthDate;
    private String password;

    // 사용자 로그인 데이터
    private String userId;
    private String phoneNumber;

    // RPG 시스템
    private int level;
    private int exp;
    private int needExp;

    // 루틴 데이터
    private List<Routine> routines = new ArrayList<>();
    private String lastUpdateDate;

    // 기본 생성자
    public UserData() {}

    // 칭호 시스템 관련 필드
    private int totalExp = 0;
    private int cycle = 0; // 회차 (레벨 10 달성 시 증가)
    private String currentTitle = ""; // 현재 칭호
    private List<String> ownedTitles = new ArrayList<>(); // 소지 칭호 목록
    private int levelResetCount = 0; // 레벨 초기화 횟수 (10레벨 달성 횟수)

    public UserData(String username, String userId, String phoneNumber,
                    String birthDate, String password, int level,
                    int exp, int needExp, int totalExp, int cycle,
                    String currentTitle, List<String> ownedTitles,
                    List<Routine> routines) {

        this.username = username;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.password = password;
        this.level = level;
        this.exp = exp;
        this.needExp = needExp;
        this.totalExp = totalExp;
        this.cycle = cycle;
        this.currentTitle = currentTitle;
        this.ownedTitles = ownedTitles;
        this.routines = routines;
        this.lastUpdateDate = getCurrentDate();
    }

    /**
     * MongoDB 연동을 위해 추가됨 (toDocument/fromDocument)
     * - routines도 Document 리스트로 저장
     */
    public Document toDocument() {
        List<Document> routineDocs = routines.stream()
                .map(Routine::toDocument)
                .collect(Collectors.toList());

        Document doc = new Document("userId", userId)
                .append("username", username)
                .append("phoneNumber", phoneNumber)
                .append("birthDate", birthDate)
                .append("password", password)
                .append("level", level)
                .append("exp", exp)
                .append("needExp", needExp)
                .append("totalExp", totalExp)
                .append("cycle", cycle)
                .append("currentTitle", currentTitle)
                .append("ownedTitles", ownedTitles)
                .append("lastUpdateDate", lastUpdateDate)
                .append("routines", routineDocs); // 루틴 저장
        return doc;
    }

    public static UserData fromDocument(Document doc) {
        UserData user = new UserData();
        user.setUserId(doc.getString("userId"));
        user.setUsername(doc.getString("username"));
        user.setPhoneNumber(doc.getString("phoneNumber"));
        user.setBirthDate(doc.getString("birthDate"));
        user.setPassword(doc.getString("password"));
        user.setLevel(doc.getInteger("level", 0));
        user.setExp(doc.getInteger("exp", 0));
        user.setNeedExp(doc.getInteger("needExp", 0));
        user.setTotalExp(doc.getInteger("totalExp", 0));
        user.setCycle(doc.getInteger("cycle", 0));
        user.setCurrentTitle(doc.getString("currentTitle"));
        user.setOwnedTitles((List<String>) doc.get("ownedTitles"));
        user.setLastUpdateDate(doc.getString("lastUpdateDate"));

        // 루틴 목록 복구
        List<Document> routineDocs = (List<Document>) doc.get("routines");
        if (routineDocs != null) {
            List<Routine> restoredRoutines = routineDocs.stream()
                    .map(Routine::fromDocument)
                    .collect(Collectors.toList());
            user.setRoutines(restoredRoutines);
        }

        return user;
    }

    // Getters
    public String getUsername() { return username; }
    public String getUserId() { return userId; }
    private static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
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
    public void setUserId(String userId) { this.userId = userId; }
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
