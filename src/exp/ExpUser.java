package exp;

import data.UserData;
import routine.RoutineManager;

public class ExpUser {
    private final UserData userData;
    private final RoutineManager routineManager;
    private final ExpManager expManager;

    public ExpUser(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(userData.getRoutines(), this::saveUserData);
        this.expManager = new ExpManager(userData);
    }

    public void completeRoutine(String routineId) {
        int earnedExp = routineManager.completeRoutine(routineId);
        if (earnedExp > 0) {
            expManager.addExpFromRoutine(
                    routineManager.getRoutineById(routineId).get()
            );
            saveUserData();
        }
    }

    private void saveUserData() {
        Database.updateUserData(userData);
    }

    public UserData getUserData() { return userData; }
    public RoutineManager getRoutineManager() { return routineManager; }
    public ExpManager getExpManager() { return expManager; }
}
