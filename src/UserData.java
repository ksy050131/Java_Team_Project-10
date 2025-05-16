import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 모든 사용자 데이터를 포함하는 통합 클래스.
 * - JSON 직렬화/역직렬화 대상
 * - RoutineManager, ExpManager와 공유되는 데이터 저장
 */
public class UserData {
    // 기본 정보
    private String username;
    private String userId;
    private String phoneNumber;
    private String birthDate;
    private String password; // SHA-256 해시 저장

    // RPG 시스템
    private int level;
    private int exp;
    private int needExp;

    // 루틴 데이터
    private List<Routine> routines = new ArrayList<>();

    // 마지막 업데이트 날짜 (일일 리셋용)
    private String lastUpdateDate;

    public UserData(String username, String userId, String phoneNumber,
                    String birthDate, String password, int level,
                    int exp, int needExp) {
        this.username = username;
        this.userId = userId;
        // ... 다른 필드 초기화
        this.lastUpdateDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Getter/Setter (예시 1개)
    /** 레벨 설정 (ExpManager에서 사용) */
    public void setLevel(int level) {
        this.level = level;
    }

    /** 루틴 목록 반환 (RoutineManager에서 사용) */
    public List<Routine> getRoutines() {
        return routines;
    }
}