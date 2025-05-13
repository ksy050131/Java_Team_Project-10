package routine;

import data.UserData;
import exp.ExpManager;

public class RoutineManager {

    public static void completeRoutine(UserData user, Routine routine) {
        if (!routine.isCompleted()) {
            routine.markCompleted();
            user.getExpManager().addExp(50);  // 루틴 완료 시 50 경험치 부여
        } else {
            System.out.println("이미 완료된 루틴입니다.");
        }
    }

    public static void listRoutines(UserData user) {
        System.out.println("▶ " + user.getUsername() + "의 루틴 목록:");
        int i = 1;
        for (Routine routine : user.getRoutineList()) {
            System.out.println(i++ + ". " + routine.getTitle() + " - " + (routine.isCompleted() ? "완료" : "미완료"));
        }
    }
}
