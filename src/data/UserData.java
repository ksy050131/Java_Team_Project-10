package data;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import routine.Routine;
import account.LoginData;

/**
 * 모든 사용자 데이터를 포함하는 통합 클래스.
 * - JSON 직렬화/역직렬화 대상
 * - RoutineManager, ExpManager와 공유되는 데이터 저장
 */

/**
 * getExp, setExp 등 LoginData와 중복되는 필드가 많아 UserData가
 * LoginData를 상속하는 것으로 수정 -> 더 다듬을 필요가 있음.
 * 각각 class의 기능을 명확하게 정의할 필요가 있음.
 */

public class UserData extends LoginData {
    // 기본 정보(LoginData 과 중복 필드라 일단 제거)
//    private String username;
//    private String userId;
//    private String phoneNumber;
//    private String birthDate;
//    private String password; // SHA-256 해시 저장
//
//    // RPG 시스템
//    private int level;
//    private int exp;
//    private int needExp;

    // 루틴 데이터
    private List<Routine> routines = new ArrayList<>();

    // 마지막 업데이트 날짜 (일일 리셋용)
    private String lastUpdateDate;

    public UserData(String username, String userId, String phoneNumber,
                    String birthDate, String password, int level,
                    int exp, int needExp) {
        super(username, userId, phoneNumber, birthDate, password, level, exp, needExp);
        // ... 다른 필드 초기화
        this.lastUpdateDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Getter/Setter (예시 1개)
    /** 레벨 설정 (ExpManager에서 사용) */
//    public void setLevel(int level) {
//        this.level = level;
//    }

    /** 루틴 목록 반환 (RoutineManager에서 사용) */
    public List<Routine> getRoutines() {
        return routines;
    }

    // 사용자 아이디 설정 (필요한지 잘 모르겠음)
//    public void setUserId(String id) {
//        this.userId = id;
//    }
//
//    // 사용자 아이디 반환
//    public String getUserId() { return userId; }
}