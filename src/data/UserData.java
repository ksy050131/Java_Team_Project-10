package data;

import routine.Routine;
import exp.ExpManager;
import java.util.ArrayList;
import java.util.List;

public class UserData {
    private String username;
    private ExpManager expManager;
    private List<Routine> routineList;

    public UserData(String username) {
        this.username = username;
        this.expManager = new ExpManager();
        this.routineList = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public ExpManager getExpManager() {
        return expManager;
    }

    public List<Routine> getRoutineList() {
        return routineList;
    }

    public void addRoutine(Routine routine) {
        routineList.add(routine);
    }
}
