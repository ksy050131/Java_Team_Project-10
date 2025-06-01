/**
 * 사용자의 행동을 관리하는 중간 매니저 클래스.
 * - RoutineManager와 ExpManager를 조율
 * - 데이터 저장 트리거
 */
package exp;

import data.Database;
import data.UserData;
import routine.RoutineManager;

public class ExpUser {
    private UserData userData;
    private RoutineManager routineManager;
    private ExpManager expManager;

    public ExpUser(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(userData.getRoutines(), this::saveUserData);
        this.expManager = new ExpManager(userData);
    }

    /**
     * 루틴 완료 처리
     * @param routineId 완료할 루틴의 UUID (또는 인덱스)
     */
    public void completeRoutine(String routineId) {
        int earnedExp = routineManager.completeRoutine(routineId);
        if (earnedExp > 0) {
            expManager.addExp(earnedExp);
            saveUserData();
        }
    }

    private void saveUserData() {
        Database.updateUserData(userData);
    }

    // Getter 메서드들 (app.MainApp 등 외부에서 호출용)
    public UserData getUserData() {
        return userData;
    }

    public RoutineManager getRoutineManager() {
        return routineManager;
    }

    public ExpManager getExpManager() {
        return expManager;
    }
}
