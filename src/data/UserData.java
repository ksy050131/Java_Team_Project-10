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
    private int totalExp; // 총 누적 경험치
    private int cycle;    // 반복 횟수

    // 칭호
    private String currentTitle;      // 현재 칭호
    private List<String> ownedTitles; // 소지 칭호 목록

    // 루틴 데이터
    private List<Routine> routines = new ArrayList<>();
    private String lastUpdateDate;

    // 기본 생성자
    public UserData() {}

    // 모든 필드를 초기화하는 생성자
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

    private static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Getter / Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

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

    public int getTotalExp() { return totalExp; }
    public void setTotalExp(int totalExp) { this.totalExp = totalExp; }

    public int getCycle() { return cycle; }
    public void setCycle(int cycle) { this.cycle = cycle; }

    public String getCurrentTitle() { return currentTitle; }
    public void setCurrentTitle(String currentTitle) { this.currentTitle = currentTitle; }

    public List<String> getOwnedTitles() { return ownedTitles; }
    public void setOwnedTitles(List<String> ownedTitles) { this.ownedTitles = ownedTitles; }

    public List<Routine> getRoutines() { return routines; }
    public void setRoutines(List<Routine> routines) { this.routines = routines; }

    public String getLastUpdateDate() { return lastUpdateDate; }
    public void setLastUpdateDate(String lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }
}
