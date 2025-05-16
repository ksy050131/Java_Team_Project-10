/**
 * 사용자의 행동을 관리하는 중간 매니저 클래스.
 * - RoutineManager와 ExpManager를 조율
 * - 데이터 저장 트리거
 */
public class User {
    private UserData userData;
    private RoutineManager routineManager;
    private ExpManager expManager;

    public User(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(
                userData.getRoutines(),
                this::saveUserData // 루틴 변경 시 저장 콜백 등록
        );
        this.expManager = new ExpManager(userData);
    }

    /**
     * 루틴 완료 처리
     * @param routineId 완료할 루틴의 UUID
     */
    public void completeRoutine(String routineId) {
        int earnedExp = routineManager.completeRoutine(routineId); // 루틴 완료 처리
        if (earnedExp > 0) {
            expManager.addExp(earnedExp); // 경험치 추가
            saveUserData(); // 변경사항 저장
        }
    }

    /** UserData를 JSON으로 저장 */
    private void saveUserData() {
        Database.updateUserData(userData);
    }

    // Getter들 (UI/외부 접근용)
    public UserData getUserData() { return userData; }
    public RoutineManager getRoutineManager() { return routineManager; }
    public ExpManager getExpManager() { return expManager; }
}