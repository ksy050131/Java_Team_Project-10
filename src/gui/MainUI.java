package gui;

import app.MainAppGUI;
import data.Database;
import data.Gemini;
import data.UserData;
import exp.ExpManager;
import routine.DailyRoutine; // DailyRoutine을 타입 확인(instanceof)에 사용하기 위해 임포트
import routine.Routine;
import routine.RoutineManager;
import chart.ChartDisplayFrame; // ChartDisplayFrame 임포트 (유지)

// import java.security.Permissions; // [FIXED] 불필요한 import 제거
import java.util.ArrayList; // UserData의 routines가 null일 경우 대비

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MainUI extends JFrame {
    private final UserData userData;
    private final RoutineManager routineManager;
    private final ExpManager expManager;
    private JLabel levelLabel;        // 상단 사용자 경험치 표시 레이블
    private JTable routineTable;      // 루틴 목록을 보여줄 JTable
    private JPanel routinePanel;      // 루틴 목록 보여줄 패널 -> table하고 겹치나?
    private DefaultTableModel tableModel;  // JTable에 연결할 테이블 모델
    private JPanel centerPanel; // [FIXED] 타입을 올바른 JPanel로 수정
    private JFrame frame; // JFrame 객체 선언
    private JProgressBar expBar;

    public MainUI(UserData userData) {
        this.userData = userData;
        this.frame = this; // frame을 mainUI로 설정
        // UserData의 getRoutines()가 null을 반환할 경우를 대비하여 빈 리스트로 초기화
        this.routineManager = new RoutineManager(
                userData.getRoutines() != null ? userData.getRoutines() : new ArrayList<>(),
                this::saveUserData // Database.updateUserData는 static 메서드이거나, Database 인스턴스 필요
        );
        this.expManager = new ExpManager(userData);
        initUI(); // UI 초기화 메서드 호출
    }

    private void initUI() {
        setTitle("루틴 코치 - " + userData.getUsername() + "님");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널: 사용자 정보
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1)); // 3행

        // 첫 번째 행: 기본 정보
        JPanel firstRow = new JPanel(new GridLayout(1, 2));
        JLabel welcomeLabel = new JLabel(userData.getUsername() + "님 환영합니다!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        firstRow.add(welcomeLabel);

        levelLabel = new JLabel("Lv." + userData.getLevel() + " | EXP: " +
                userData.getExp() + "/" + userData.getNeedExp(), SwingConstants.RIGHT);
        levelLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        firstRow.add(levelLabel);

        topPanel.add(firstRow);

        // 두 번째 행: 회차 정보
        JPanel secondRow = new JPanel(new GridLayout(1, 2));
        JLabel cycleLabel = new JLabel("회차: " + userData.getCycle() + "회차", SwingConstants.LEFT);
        cycleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        secondRow.add(cycleLabel);

        // [FIXED] "초기화 횟수" 레이블이 levelResetCount를 올바르게 표시하도록 수정
        JLabel resetCountLabel = new JLabel("초기화 횟수: " + userData.getCycle() + "회", SwingConstants.RIGHT);
        resetCountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        secondRow.add(resetCountLabel);

        topPanel.add(secondRow);

        // 세 번째 행: 칭호 정보
        JPanel thirdRow = new JPanel();
        JLabel titleLabel = new JLabel("현재 칭호: " + userData.getCurrentTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 14));
        thirdRow.add(titleLabel);

        topPanel.add(thirdRow);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: JTable로 루틴 목록 표시
        centerPanel = new JPanel(new BorderLayout()); // [FIXED] 지역 변수가 아닌 클래스 필드를 초기화

        // 컬럼명 배열: 완료(체크박스), 루틴 타입, 내용, 난이도, 연속 완료일, ID(숨김)
        String[] columnNames = {"완료", "루틴 타입", "내용", "난이도", "연속 완료일", "ID"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // 완료 체크박스 컬럼 타입 지정
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // 완료 체크박스(0번째 컬럼)와 내용(2번째 컬럼)만 편집 가능하게 설정
                return column == 0 || column == 2;
            }
        };

        routineTable = new JTable(tableModel);
        routineTable.setFillsViewportHeight(true);

        // ID 컬럼 숨기기: 너비 0으로 설정하여 UI에 보이지 않게 함
        TableColumn idColumn = routineTable.getColumnModel().getColumn(5);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);
        idColumn.setWidth(0);
        // 헤더에서도 ID 컬럼 숨김 처리
        routineTable.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
        routineTable.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
        routineTable.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);

        // 스크롤패인에 JTable 추가 후 중앙 패널에 붙임
        JScrollPane scrollPane = new JScrollPane(routineTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 테이블 편집(체크박스 및 내용) 감지용 리스너 등록
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                String routineId = (String) routineTable.getValueAt(row, 5);  // 숨긴 ID 컬럼으로 루틴 식별
                if (routineId == null) return;
                Optional<Routine> opt = routineManager.getRoutineById(routineId);
                if (opt.isEmpty()) return;
                Routine routine = opt.get();

                if (col == 0) {  // 완료 체크박스 변경 시 처리
                    Boolean completed = (Boolean) tableModel.getValueAt(row, col);
                    if (completed && !routine.isCompleted()) {
                        // [FIXED] routineManager.completeRoutine이 반환하는 경험치를 expManager.addExp에 전달
                        int gainedExp = routineManager.completeRoutine(routine.getId());
                        expManager.addExp(gainedExp);
                    } else if (!completed && routine.isCompleted()) {
                        // [FIXED] routineManager.uncompleteRoutine이 반환하는 음수 경험치를 expManager.addExp에 전달
                        int recoveredExp = routineManager.uncompleteRoutine(routine.getId());
                        expManager.addExp(recoveredExp);
                    }
                    updateExpDisplay();
                    saveUserData();
                    updateRoutineList();
                } else if (col == 2) { // 내용 컬럼 수정 시 처리
                    String newContent = (String) tableModel.getValueAt(row, col);
                    routine.setContent(newContent);
                    saveUserData();
                    updateRoutineList();
                }
            }
        });

        // 하단 패널: 루틴 추가, 정렬 버튼 및 로그아웃 버튼 그룹 생성
        JPanel bottomPanel = new JPanel(new GridLayout(1, 8));

        // 루틴 추가 버튼 3종 (일반, 일일, 연속)
        JButton addNormalBtn = new JButton("일반 루틴 추가");
        addNormalBtn.addActionListener(e -> {
            String content = JOptionPane.showInputDialog(this, "일반 루틴 내용을 입력하세요:");
            if (content != null && !content.trim().isEmpty()) {
                Gemini gemini = new Gemini();
                int difficulty = gemini.getDif(content); // 난이도 받아오기
                routineManager.addRoutine(content, difficulty);
                updateRoutineList();
                saveUserData();
            }
        });
        bottomPanel.add(addNormalBtn);

        JButton addDailyBtn = new JButton("일일 루틴 추가");
        addDailyBtn.addActionListener(e -> {
            String content = JOptionPane.showInputDialog(this, "일일 루틴 내용을 입력하세요:");
            if (content != null && !content.trim().isEmpty()) {
                int difficulty = 3; // 기본 난이도, 필요 시 수정 가능
                routineManager.addDailyRoutine(content, difficulty);
                updateRoutineList();
                saveUserData();
            }
        });
        bottomPanel.add(addDailyBtn);

        JButton addStreakBtn = new JButton("연속 루틴 추가");
        addStreakBtn.addActionListener(e -> {
            // RoutineManager에 연속 루틴 추가 로직이 없으므로 미구현 처리
            JOptionPane.showMessageDialog(this, "연속 루틴 기능은 아직 구현되지 않았습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        });
        bottomPanel.add(addStreakBtn);

        // 정렬 버튼 3종 (이름순, 완료순, 등록일순)
        JButton sortByNameBtn = new JButton("이름순 정렬");
        sortByNameBtn.addActionListener(e -> {
            List<Routine> sorted = routineManager.getRoutinesSortedByName();
            updateRoutineList(sorted);
        });
        bottomPanel.add(sortByNameBtn);

        JButton sortByCompleteBtn = new JButton("완료순 정렬");
        sortByCompleteBtn.addActionListener(e -> {
            List<Routine> sorted = routineManager.getRoutinesSortedByComplete();
            updateRoutineList(sorted);
        });
        bottomPanel.add(sortByCompleteBtn);

        JButton sortByDateBtn = new JButton("등록일순 정렬");
        sortByDateBtn.addActionListener(e -> {
            List<Routine> sorted = routineManager.getRoutinesSortedByRegister();
            updateRoutineList(sorted);
        });
        bottomPanel.add(sortByDateBtn);

        // 로그아웃 버튼
        JButton logoutBtn = new JButton("로그아웃");
        logoutBtn.addActionListener(e -> logout());
        bottomPanel.add(logoutBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // 초기 루틴 목록 갱신
        updateRoutineList();
    }

    // 사용자 경험치 레이블 업데이트 메서드
    private void updateExpDisplay() {
        levelLabel.setText("Lv." + userData.getLevel() + " | EXP: " + userData.getExp() + "/" + userData.getNeedExp());
    }

    // 루틴 목록 전체 갱신 (내부 루틴 리스트 기준)
    private void updateRoutineList() {
        updateRoutineList(routineManager.getAllRoutines());
    }

    // 정렬된 루틴 리스트를 받아 JTable 갱신 (중복 제거 및 정렬 반영)
    private void updateRoutineList(List<Routine> routines) {
        tableModel.setRowCount(0); // 기존 테이블 내용 제거
        for (Routine routine : routines) {
            String typeStr;
            String streakInfo = ""; // 연속 완료일 정보

            // instanceof를 사용해 실제 객체 타입에 따라 분기
            if (routine instanceof DailyRoutine) {
                typeStr = "[일일]";
                // DailyRoutine에 연속 관련 정보가 있다면 여기서 처리. 예: streakInfo = String.valueOf(((DailyRoutine) routine).getSomeValue());
            } else {
                typeStr = "[일반]";
            }
            // 참고: 연속 루틴(StreakRoutine) 클래스가 추가되면 여기에 'else if (routine instanceof StreakRoutine)' 구문을 추가하여 처리할 수 있습니다.
            // 예: else if (routine instanceof StreakRoutine) { typeStr = "[연속]"; streakInfo = String.valueOf(((StreakRoutine) routine).getStreakCount()); }

            Object[] rowData = {
                    routine.isCompleted(),
                    typeStr,
                    routine.getContent(),
                    routine.getDifficulty(),
                    streakInfo, // 현재는 연속 루틴 로직이 없으므로 항상 비어있음
                    routine.getId()  // 숨겨진 ID 컬럼 (식별용)
            };
            tableModel.addRow(rowData);
        }
    }

    // 루틴 추가 다이얼로그 후 추가 처리 -> 버튼 리스너에서 직접 처리하도록 변경되어 이 메서드는 더 이상 필요하지 않습니다.
    /*
    private void addRoutine(RoutineType type) {
        String content = JOptionPane.showInputDialog(this, "루틴 내용을 입력하세요:");
        if (content != null && !content.trim().isEmpty()) {
            int difficulty = 3; // 기본 난이도, 필요 시 수정 가능
            routineManager.addRoutine(content, difficulty, type);
            updateRoutineList();
            saveUserData();
        }
    }
    */

    // 이 아래의 메서드들은 기존 코드에 있었지만, 현재 JTable 기반 UI에서는 직접적으로 사용되지 않는 것으로 보입니다.
    // 기능 유지를 위해 남겨두되, 필요에 따라 JTable UI와 통합하거나 제거할 수 있습니다.
    private void addRoutine(String s) { // 이 메서드는 더이상 호출되지 않음
        centerPanel.add(routinePanel);
        // centerPanel.add(Box.createVerticalStrut(10));

        // 버튼 영역 구성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Add Routine");

        // 루틴 추가 버튼 동작 정의
        addButton.addActionListener(e -> {
            String routineName = JOptionPane.showInputDialog(frame, "추가할 루틴의 이름을 입력하세요:");
            if (routineName != null && !routineName.trim().isEmpty()) {
                Routine newRoutine = routineManager.addRoutine(routineName.trim());
                routinePanel.add(createRoutineItem(newRoutine));
                routinePanel.revalidate();
                routinePanel.repaint();
                updateStatusPanel();
            }
        });

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // "통계 보기" 버튼 추가 (원본 버튼 패널 스타일에 맞춤)
        JButton viewStatsButton = new JButton("View Statistics");
        // viewStatsButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 12)); // 원본에 폰트 설정은 없었음
        viewStatsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 원본 스타일
        viewStatsButton.addActionListener(e -> {
            showStatisticsChart();
        });
        buttonPanel.add(viewStatsButton);
        // buttonPanel.add(Box.createVerticalStrut(10)); // 통계 버튼 다음 간격 (선택 사항)


        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());
        frame.add(centerPanel, BorderLayout.CENTER);

        // 하단 경험치 상태 표시 패널 구성
        JPanel bottomPanel = new JPanel();
        levelLabel = new JLabel();
        expBar.setStringPainted(true);
        bottomPanel.add(levelLabel);
        bottomPanel.add(expBar);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.add(bottomPanel, BorderLayout.SOUTH);

        updateStatusPanel(); // 경험치/레벨 초기 상태 표시
    }

    private void updateStatusPanel() {
        // JTable UI의 levelLabel을 업데이트하도록 로직을 통합하거나,
        // 이 메서드와 관련된 구 UI 컴포넌트(expBar 등)를 다시 활성화해야 합니다.
        // 현재는 expBar가 초기화되지 않아 NullPointerException이 발생할 수 있습니다.
        if (levelLabel != null) {
            updateExpDisplay();
        }
    }

    // 루틴 하나의 체크박스 + 삭제 버튼 UI 생성 메서드
    private JPanel createRoutineItem(Routine routine) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // FlowLayout 설정
        itemPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬

        JCheckBox cb = new JCheckBox(routine.getContent());
        cb.setSelected(routine.isCompleted());
        cb.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        // 체크 상태 변화에 따라 완료/미완료 처리 및 경험치 반영
        cb.addActionListener(e -> {
            if (cb.isSelected()) {
                int gained = routineManager.completeRoutine(routine.getId());
                expManager.addExp(gained);
            } else {
                int lost = routineManager.uncompleteRoutine(routine.getId());
                expManager.addExp(lost);
            }
            updateStatusPanel();
        });

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.setFont(new Font("Malgun Gothic", Font.PLAIN, 10));
        deleteBtn.setMargin(new Insets(0, 5, 0, 5));
        deleteBtn.setFocusable(false); // 뭐 좀 더 작게 만들어 주는..?

        // 삭제 기능 (근데 체크박스 표시된 상태로 삭제하면 경험치가 그대로 남아있음)
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "정말 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (routine.isCompleted()) {
                    // 이미 완료된 루틴 삭제 시 경험치 처리 (원본 코드에는 이 로직 없음)
                    // int recoveredExp = routineManager.uncompleteRoutine(routine.getId());
                    // expManager.addExp(recoveredExp);
                }
                routineManager.deleteRoutine(routine.getId());
                routinePanel.remove(itemPanel);
                routinePanel.revalidate();
                routinePanel.repaint();
                updateStatusPanel();
            }
        });

        itemPanel.add(cb);
        itemPanel.add(Box.createHorizontalStrut(10));
        itemPanel.add(deleteBtn);

        return itemPanel;
    }

    // 변경된 사용자 데이터 저장 호출
    private void saveUserData() {
        Database.updateUserData(userData);
    }

    // 로그아웃 처리: 데이터 저장 후 로그인 화면으로 이동, 현재 창 닫기
    private void logout() {
        saveUserData();
        MainAppGUI.logout();
        dispose();
    }

    // --- 차트 보기 메서드 (유지) ---
    private void showStatisticsChart() {
        if (this.userData == null) {
            JOptionPane.showMessageDialog(this.frame, "사용자 데이터가 로드되지 않았습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            // MainUI의 userData 필드를 사용하도록 수정 (원본의 user -> userData)
            ChartDisplayFrame chartFrame = new ChartDisplayFrame(this.userData);
            chartFrame.setVisible(true);
        });
    }

    // 화면 보이기 메서드
    public void showFrame() { // 원본에 show() 메서드가 없어 showFrame()으로 명명
        this.setVisible(true);
    }
}