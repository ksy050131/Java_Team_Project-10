package routine;

import java.util.List;
import java.util.Optional;
// UUID는 Routine 클래스 내부에서 사용되므로 RoutineManager에서는 직접적인 UUID 임포트가 필수는 아닐 수 있음

public class RoutineManager {

    private List<Routine> routines;
    private Runnable saveUserDataCallback;

    public RoutineManager(List<Routine> routines, Runnable saveUserDataCallback) {
        this.routines = routines;
        this.saveUserDataCallback = saveUserDataCallback;
        // 생성 시점에 모든 루틴의 상태를 한 번 업데이트 해주는 것이 좋을 수 있습니다.
        updateAllRoutinesForNewDay();
    }

    // 새 일회성 루틴 추가 (기존 메서드)
    public Routine addRoutine(String content, int rewardExp) {
        Routine newRoutine = new Routine(content, rewardExp);
        this.routines.add(newRoutine);
        triggerSave();
        System.out.println("새 할 일 '" + content + "' 추가됨 (ID: " + newRoutine.getId() + ")");
        return newRoutine;
    }

    // 새 일회성 루틴 추가 (기존 메서드)
    public Routine addRoutine(String content) {
        Routine newRoutine = new Routine(content);
        this.routines.add(newRoutine);
        triggerSave();
        System.out.println("새 할 일 '" + content + "' 추가됨 (ID: " + newRoutine.getId() + ")");
        return newRoutine;
    }

    // 새 일일 루틴 추가
    public DailyRoutine addDailyRoutine(String content, int rewardExp) {
        DailyRoutine newDailyRoutine = new DailyRoutine(content, rewardExp);
        this.routines.add(newDailyRoutine); // List<Routine>에 DailyRoutine 객체 추가 (다형성)
        triggerSave();
        System.out.println("새 일일 루틴 '" + content + "' 추가됨 (ID: " + newDailyRoutine.getId() + ")");
        return newDailyRoutine;
    }


    // ID로 루틴 조회 (기존과 동일)
    public Optional<Routine> getRoutineById(String id) {
        return this.routines.stream()
                .filter(routine -> routine.getId().equals(id))
                .findFirst();
    }

    // 전체 루틴 목록 조회 (기존과 동일)
    public List<Routine> getAllRoutines() {
        return this.routines;
    }

    // 루틴 내용 수정 (기존과 동일)
    public boolean updateRoutineContent(String id, String newContent) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            routineOpt.get().setContent(newContent);
            triggerSave();
            System.out.println("ID '" + id + "' 할 일 내용 변경됨.");
            return true;
        }
        System.out.println("오류: ID '" + id + "' 할 일을 찾을 수 없어 내용을 변경할 수 없습니다.");
        return false;
    }

    // 루틴 보상 경험치 수정 (기존과 동일)
    public boolean updateRoutineRewardExp(String id, int newRewardExp) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            routineOpt.get().setRewardExp(newRewardExp);
            triggerSave();
            System.out.println("ID '" + id + "' 할 일 보상 경험치 변경됨.");
            return true;
        }
        System.out.println("오류: ID '" + id + "' 할 일을 찾을 수 없어 보상 경험치를 변경할 수 없습니다.");
        return false;
    }

    // 루틴 삭제 (기존과 동일)
    public boolean deleteRoutine(String id) {
        boolean removed = this.routines.removeIf(routine -> routine.getId().equals(id));
        if (removed) {
            triggerSave();
            System.out.println("ID '" + id + "' 할 일 삭제됨.");
        } else {
            System.out.println("오류: ID '" + id + "' 할 일을 찾을 수 없어 삭제할 수 없습니다.");
        }
        return removed;
    }

    // 루틴 완료 처리 (기존과 동일 - 다형성에 의해 Routine/DailyRoutine 모두 처리 가능)
    public int completeRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (!routine.isCompleted()) {
                routine.markAsCompleted(); // 여기서 Routine 또는 DailyRoutine의 markAsCompleted 호출
                triggerSave();
                return routine.getRewardExp();
            } else {
                System.out.println("할 일 '" + routine.getContent() + "'은(는) 이미 완료되었습니다.");
                return 0;
            }
        }
        System.out.println("오류: ID가 '" + id + "'인 할 일을 찾을 수 없어 완료 처리할 수 없습니다.");
        return 0;
    }

    // 루틴 미완료 처리 (기존과 동일)
    public boolean uncompleteRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (routine.isCompleted()) {
                routine.markAsUncompleted();
                triggerSave();
                System.out.println("할 일 '" + routine.getContent() + "' 미완료 처리됨.");
                return true;
            } else {
                System.out.println("할 일 '" + routine.getContent() + "'은(는) 이미 미완료 상태입니다.");
                return false;
            }
        }
        System.out.println("오류: ID가 '" + id + "'인 할 일을 찾을 수 없어 미완료 처리할 수 없습니다.");
        return false;
    }

    /**
     * 관리 중인 모든 루틴에 대해 새 날짜 기준으로 상태를 업데이트합니다.
     * (예: 일일 루틴 초기화)
     * 이 메서드 호출 후 변경사항이 있을 수 있으므로 저장을 트리거합니다.
     */
    public void updateAllRoutinesForNewDay() {
        boolean changed = false;
        if (routines == null) return;

        for (Routine routine : routines) {
            boolean beforeCompleted = routine.isCompleted();
            String beforeDate = routine.getDateMarkedCompleted();

            routine.P_updateStatusForNewDay(); // 각 루틴의 타입에 맞는 초기화 로직 호출

            // 상태 변경 여부 확인 (단순화된 체크, 실제로는 더 정교한 비교가 필요할 수 있음)
            if (beforeCompleted != routine.isCompleted() || (beforeDate != null && !beforeDate.equals(routine.getDateMarkedCompleted()))) {
                changed = true;
            }
        }

        if (changed) {
            triggerSave(); // 상태가 변경된 루틴이 하나라도 있으면 전체 UserData 저장
            System.out.println("모든 루틴의 새 날짜 기준 상태 업데이트 완료.");
        }
    }

    private void triggerSave() {
        if (saveUserDataCallback != null) {
            saveUserDataCallback.run();
        }
    }
}