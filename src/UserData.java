import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserData {
    private String username;      // 이름
    private String userId;        // 아이디
    private String phoneNumber;   // 전화번호
    private String birthDate;     // 생일
    private String password;      // 비밀번호
    private int level;            // 레벨
    private int exp;              // 현재 경험치
    private int needExp;          // 필요한 경험치
    private List<String> checklist;       // 체크리스트 항목
    private List<Boolean> checklistState; // 체크리스트 상태
    private String updated;       // 마지막 업데이트 날짜 (바뀌면 체크리스트 상태 초기화를 위함)

    // 기존 생성자 (updated를 직접 입력해야 함)
    public UserData(String username, String userId, String phoneNumber, String birthDate, String password, 
                    int level, int exp, int needExp, String updated) {
        this.username = username;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.password = password;
        this.level = level;
        this.exp = exp;
        this.needExp = needExp;
        this.checklist = new ArrayList<>();
        this.checklistState = new ArrayList<>();
        this.updated = updated;
        checkDate();  // 날짜를 비교하고 필요 시 체크리스트 상태 초기화
    }

    // 새로운 생성자 (updated를 기본값으로 현재 날짜 설정)
    public UserData(String username, String userId, String phoneNumber, String birthDate, String password, 
                    int level, int exp, int needExp) {
        this(username, userId, phoneNumber, birthDate, password, level, exp, needExp, getCurrentDate());
    }

    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환하는 메소드
    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    // 마지막 업데이트 날짜와 현재 날짜를 비교하고, 다르면 checklistState를 모두 false로 설정
    private void checkDate() {
        String currentDate = getCurrentDate();
        if (!updated.equals(currentDate)) {
            // 날짜가 다르면 체크리스트 상태를 모두 false로 초기화
            for (int i = 0; i < checklistState.size(); i++) {
                checklistState.set(i, false);
            }
            updated = currentDate;  // updated를 현재 날짜로 갱신
        }
    }

    // getter와 setter
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

    public List<String> getChecklist() { return checklist; }
    public void setChecklist(List<String> checklist) { this.checklist = checklist; }

    public List<Boolean> getChecklistState() { return checklistState; }
    public void setChecklistState(List<Boolean> checklistState) { this.checklistState = checklistState; }

    public String getUpdated() { return updated; }
    public void setUpdated(String updated) { this.updated = updated; }
}
