package routine;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Routine 클래스에서 ID 생성을 위해 사용
import data.Gemini;
import java.util.ArrayList;
import java.util.Comparator;
// UUID는 Routine 클래스 내부에서 사용되므로 RoutineManager에서는 직접적인 UUID 임포트가 필수는 아닐 수 있음

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
        // 생성 시점에 모든 루틴의 상태를 한 번 업데이트 해주는 것이 좋을 수 있습니다.
        updateAllRoutinesForNewDay();
    }

    // 새 일회성 루틴 추가 (내용과 난이도 사용)
    public Routine addRoutine(String content, int difficulty) {
        Routine newRoutine = new Routine(content, difficulty); // Routine 클래스의 생성자 활용
        this.routines.add(newRoutine);
        triggerSave(); // 변경사항 저장 신호
        System.out.println("새 할 일 '" + content + "' 추가됨 (ID: " + newRoutine.getId() + ")");
        return newRoutine;
    }

    // 새 일회성 루틴 추가 (내용만 사용, 난이도는 Routine 클래스의 기본값 사용)
    public Routine addRoutine(String content) {
        Routine newRoutine = new Routine(content); // Routine 클래스의 생성자 활용
        this.routines.add(newRoutine);
        triggerSave(); // 변경사항 저장 신호
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

    // 전체 루틴 목록 조회 (방어적 복사본 반환 고려 가능)
    public List<Routine> getAllRoutines() {
        // UserData의 리스트를 직접 수정할 수 있게 하려면 원본 반환,
        // 그렇지 않다면 new ArrayList<>(this.routines)로 복사본 반환
        return this.routines;
    }

    // ------------------------------------------------------------
    // 1) 등록 순 정렬 전체 루틴 목록 조회
    // ------------------------------------------------------------
    public List<Routine> getRoutinesSortedByRegister() {
        // 내부 리스트를 수정하지 않기 위해 복사본을 만든 뒤 정렬
        List<Routine> sorted = new ArrayList<>(this.routines);
        // dateCreated는 "yyyy-MM-dd" 형식의 문자열이므로 String.compareTo만으로 날짜 순 오름차순 정렬 가능
        sorted.sort(Comparator.comparing(Routine::getDateCreated));
        return sorted;
    }

    // ------------------------------------------------------------
    // 2) 완료 여부 순 정렬 전체 루틴 목록 조회
    //    - 완료된 것들이 먼저, 완료된 것들끼리는 완료 마킹된 날짜(dateMarkedCompleted) 오름차순
    //    - 그 외(미완료)는 dateCreated 기준 오름차순
    // ------------------------------------------------------------
    public List<Routine> getRoutinesSortedByComplete() {
        List<Routine> sorted = new ArrayList<>(this.routines);
        sorted.sort((r1, r2) -> {
            boolean c1 = r1.isCompleted();
            boolean c2 = r2.isCompleted();

            // 둘 다 완료된 경우 -> dateMarkedCompleted 기준 오름차순
            if (c1 && c2) {
                String d1 = r1.getDateMarkedCompleted();
                String d2 = r2.getDateMarkedCompleted();
                // null 안전성: 둘 다 null이라면 0, 하나만 null이면 null이 뒤로 가도록 처리
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return 1;
                if (d2 == null) return -1;
                return d1.compareTo(d2);
            }
            // r1만 완료된 경우 -> r1이 앞으로
            else if (c1 && !c2) {
                return -1;
            }
            // r2만 완료된 경우 -> r2가 앞으로
            else if (!c1 && c2) {
                return 1;
            }
            // 둘 다 미완료인 경우 -> dateCreated 기준 오름차순
            else {
                return r1.getDateCreated().compareTo(r2.getDateCreated());
            }
        });
        return sorted;
    }

    // ------------------------------------------------------------
    // 3) 이름(내용) 순 정렬 전체 루틴 목록 조회
    //    - content(String) 기준 대소문자 구분 없이 사전식 오름차순
    // ------------------------------------------------------------
    public List<Routine> getRoutinesSortedByName() {
        List<Routine> sorted = new ArrayList<>(this.routines);
        // String.CASE_INSENSITIVE_ORDER을 사용하면 대소문자 구분 없이 정렬
        sorted.sort(Comparator.comparing(Routine::getContent, String.CASE_INSENSITIVE_ORDER));
        return sorted;
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

    // 루틴 난이도 수정
    public boolean updateRoutineDifficulty(String id, int newDifficulty) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            routineOpt.get().setDifficulty(newDifficulty);
            triggerSave();
            System.out.println("ID '" + id + "' 할 일 난이도 변경됨.");
            return true;
        }
        System.out.println("오류: ID '" + id + "' 할 일을 찾을 수 없어 난이도를 변경할 수 없습니다.");
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

    // 루틴 완료 처리 (난이도 기반 경험치 반환)
    public int completeRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (!routine.isCompleted()) { // 아직 완료되지 않은 경우에만 처리
                routine.markAsCompleted();
                triggerSave();
                return routine.getDifficulty(); // 완료된 루틴의 난이도 값 반환 (경험치처럼 사용)
            } else {
                System.out.println("할 일 '" + routine.getContent() + "'은(는) 이미 완료되었습니다.");
                return 0; // 이미 완료되었으면 0 XP 반환
            }
        }
        System.out.println("오류: ID가 '" + id + "'인 할 일을 찾을 수 없어 완료 처리할 수 없습니다.");
        return 0; // 루틴 못 찾으면 0 XP 반환
    }

    // 루틴 완료 취소 (경험치 회수)
    public int uncompleteRoutine(String id) {
        Optional<Routine> routineOpt = getRoutineById(id);
        if (routineOpt.isPresent()) {
            Routine routine = routineOpt.get();
            if (routine.isCompleted()) { // 완료된 경우에만 처리
                routine.markAsUncompleted();
                triggerSave();
                return -routine.getDifficulty(); // 음수 반환 (경험치 회수)
            }
        }
        return 0; // 루틴 없거나 이미 미완료면 0
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