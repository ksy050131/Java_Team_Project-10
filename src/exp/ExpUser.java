/**
 * 사용자의 행동을 관리하는 중간 매니저 클래스.
 * - RoutineManager와 ExpManager를 조율
 * - 데이터 저장 트리거
 */
package exp;

import data.Database;
import data.UserData;
import routine.RoutineManager;
//
public class ExpUser {
    private final UserData userData;
    private final RoutineManager routineManager;
    private final ExpManager expManager;

    public ExpUser(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(
                userData.getRoutines(),
                this::saveUserData
        );
        this.expManager = new ExpManager(userData);
    }

    public void completeRoutine(String routineId) {
        // RoutineManager를 통해 루틴 완료 처리
        int result = routineManager.completeRoutine(routineId);
        if (result > 0) {
            // ExpManager를 통해 경험치 추가
            routineManager.getRoutineById(routineId).ifPresent(routine -> {
                expManager.addExpFromRoutine(routine);
                saveUserData();
            });
        }
    }

    private void saveUserData() {
        Database.updateUserData(userData);
    }

    public UserData getUserData() { return userData; }
    public RoutineManager getRoutineManager() { return routineManager; }
    public ExpManager getExpManager() { return expManager; }
}