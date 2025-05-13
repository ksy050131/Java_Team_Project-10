import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Routine 클래스에서 ID 생성을 위해 사용

public class RoutineManager {

    private List<Routine> routines; // UserData 객체로부터 전달받은 루틴 리스트의 참조
    private Runnable saveUserDataCallback; // UserData 저장을 트리거할 콜백

    /**
     * 생성자
     * @param routines UserData 객체 내의 루틴 리스트
     * @param saveUserDataCallback 루틴 목록 변경 시 UserData 저장을 호출할 콜백
     */
    public RoutineManager(List<Routine> routines, Runnable saveUserDataCallback) {
        this.routines = routines; // 외부 리스트를 직접 참조 (UserData와 동기화)
        this.saveUserDataCallback = saveUserDataCallback;
    }

    // 새 루틴 추가 (내용과 보상 경험치 사용)
    public Routine addRoutine(String content, int rewardExp) {
        Routine newRoutine = new Routine(content, rewardExp); // Routine 클래스의 생성자 활용
        this.routines.add(newRoutine);
        triggerSave(); // 변경사항 저장 신호
        System.out.println("새 할 일 '" + content + "' 추가됨 (ID: " + newRoutine.getId() + ")");
        return newRoutine;
    }

    // 새 루틴 추가 (내용만 사용, 보상 경험치는 Routine 클래스의 기본값 사용)
    public Routine addRoutine(String content) {
        Routine newRoutine = new Routine(content); // Routine 클래스의 생성자 활용
        this.routines.add(newRoutine);
        triggerSave(); // 변경사항 저장 신호
        System.out.println("새 할 일 '" + content + "' 추가됨 (ID: " + newRoutine.getId() + ")");
        return newRoutine;
    }

    // ID로 루틴 조회
    public Optional<Routine> getRoutineById(String id) {
        return this.routines.stream()
                .filter(routine -> routine.getId().equals(id))
                .findFirst();
    }

    // 전체 루틴 목록 조회 (방어적 복사본 반환 고려 가능)
    public List<Routine> getAllRoutines() {
        // UserData의 리스트를 직접 수정할 수 있게 하려면 원본 반환,
        // 그렇지 않다면 new ArrayList<>(this.routines)로 복사본 반환
        return this.routines;
    }

    // 루틴 내용 수정
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

    // 루틴 보상 경험치 수정
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

    // 루틴 삭제
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

    // 루틴 완료 처리 (경험치 반환)
    public int completeRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (!routine.isCompleted()) { // 아직 완료되지 않은 경우에만 처리
                routine.markAsCompleted();
                triggerSave();
                return routine.getRewardExp(); // 완료된 루틴의 경험치 값 반환
            } else {
                System.out.println("할 일 '" + routine.getContent() + "'은(는) 이미 완료되었습니다.");
                return 0; // 이미 완료되었으면 0 XP 반환
            }
        }
        System.out.println("오류: ID가 '" + id + "'인 할 일을 찾을 수 없어 완료 처리할 수 없습니다.");
        return 0; // 루틴 못 찾으면 0 XP 반환
    }

    // 루틴 미완료 처리
    public boolean uncompleteRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (routine.isCompleted()) { // 완료된 경우에만 처리
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

    // 변경사항 저장 신호 (UserDataPersistenceService가 UserData 전체를 저장하도록 유도)
    private void triggerSave() {
        if (saveUserDataCallback != null) {
            saveUserDataCallback.run();
        }
    }
}