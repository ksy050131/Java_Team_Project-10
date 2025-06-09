package gui;

import app.MainAppGUI;
import data.Database;
import data.Gemini;
import data.UserData;
import exp.ExpManager;
import routine.DailyRoutine; // DailyRoutine을 타입 확인(instanceof)에 사용하기 위해 임포트
import routine.Routine;
import routine.RoutineManager;
import chart.ChartDisplayFrame; // [추가] ChartDisplayFrame 클래스 임포트
<<<<<<< HEAD
import title.TitleManager; // [추가] TitleManager 클래스 임포트
=======
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainUI extends JFrame {
    private final UserData userData;
    private final RoutineManager routineManager;
    private final ExpManager expManager;
    private JLabel levelLabel;
<<<<<<< HEAD
    private JLabel titleLabel; // [추가] 칭호 라벨 필드 선언
    private JTable routineTable;
    private DefaultTableModel tableModel;
    private JPanel centerPanel;

    private JLabel expDeltaLabel; // [추가] EXP 증가 표시용 라벨

    // 페이드 아웃용 타이머와 알파 값
    private Timer fadeTimer;
    private float alpha = 1.0f;
=======
    private JTable routineTable;
    private DefaultTableModel tableModel;
    private JPanel centerPanel;
    // private JFrame frame; // MainUI가 JFrame을 상속하므로 불필요
    private JProgressBar expBar;
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf

    public MainUI(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(
                userData.getRoutines() != null ? userData.getRoutines() : new ArrayList<>(),
                this::saveUserData
        );
        this.expManager = new ExpManager(userData);
        initUI();
    }

    private void initUI() {
        // 칭호가 있으면 [칭호]이름님, 없으면 그냥 이름님
        String displayName = userData.getCurrentTitle().isBlank()
                ? userData.getUsername()
                : "[" + userData.getCurrentTitle() + "]" + userData.getUsername();

        setTitle("루틴 코치 - " + displayName + "님");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널: 사용자 정보
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1)); // 3행으로 변경 (초기화 횟수 라벨 제거, expDeltaLabel 위치 변경)

        // 첫 번째 행: 기본 정보
        JPanel firstRow = new JPanel(new GridLayout(1, 2));
        JLabel welcomeLabel = new JLabel(displayName + "님 환영합니다!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        firstRow.add(welcomeLabel);

        levelLabel = new JLabel("Lv." + userData.getLevel() + " | EXP: " +
                userData.getExp() + "/" + userData.getNeedExp(), SwingConstants.RIGHT);
        levelLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        firstRow.add(levelLabel);

        topPanel.add(firstRow);

        // 두 번째 행: 회차 정보와 EXP 증가량 표시
        JPanel secondRow = new JPanel(new GridLayout(1, 2));
        JLabel cycleLabel = new JLabel("회차: " + userData.getCycle() + "회차", SwingConstants.LEFT);
        cycleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        secondRow.add(cycleLabel);

<<<<<<< HEAD
        // 초기화 횟수 라벨 제거
        // expDeltaLabel 배치
        expDeltaLabel = new JLabel();
        expDeltaLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        expDeltaLabel.setForeground(new Color(0, 128, 0)); // 초록색
        expDeltaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        expDeltaLabel.setVisible(false); // 기본적으로 숨김
        secondRow.add(expDeltaLabel);
=======
        JLabel resetCountLabel = new JLabel("초기화 횟수: " + userData.getCycle() + "회", SwingConstants.RIGHT);
        resetCountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        secondRow.add(resetCountLabel);
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf

        topPanel.add(secondRow);

        // 세 번째 행: 칭호 정보
        JPanel thirdRow = new JPanel();
        titleLabel = new JLabel("《현재 칭호》  " + userData.getCurrentTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 14));
        thirdRow.add(titleLabel);

        topPanel.add(thirdRow);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: JTable로 루틴 목록 표시
        centerPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"완료", "루틴 타입", "내용", "난이도", "연속 완료일", "ID"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 2;
            }
        };

        routineTable = new JTable(tableModel);
        routineTable.setFillsViewportHeight(true);

        TableColumn idColumn = routineTable.getColumnModel().getColumn(5);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(routineTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
<<<<<<< HEAD
                if (row < 0) return;
=======
                if (row < 0) return; // 행이 선택되지 않은 경우 예외 방지
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf

                String routineId = (String) routineTable.getValueAt(row, 5);
                if (routineId == null) return;
                Optional<Routine> opt = routineManager.getRoutineById(routineId);
                if (opt.isEmpty()) return;
                Routine routine = opt.get();

<<<<<<< HEAD
                if (col == 0) {
                    Boolean completed = (Boolean) tableModel.getValueAt(row, col);
                    if (completed && !routine.isCompleted()) {
                        routineManager.completeRoutine(routine.getId());
                        int gainedExp = expManager.addExpFromRoutine(routine);
                        showExpDelta(gainedExp);
                    } else if (!completed && routine.isCompleted()) {
                        routineManager.uncompleteRoutine(routine.getId());
                        int lostExp = expManager.removeExpFromRoutine(routine);
                        showExpDelta(-lostExp);
=======
                if (col == 0) {  // 완료 체크박스 변경 시 처리 (체크 되지 않고 경험치만 올리고 싶은 경우 completeRoutine, uncompleteRoutine "둘다" 주석 처리)
                    Boolean completed = (Boolean) tableModel.getValueAt(row, col);
                    if (completed && !routine.isCompleted()) {
                         routineManager.completeRoutine(routine.getId()); // 체크 되지 않고 경험치만 올리고 싶은 경우 주석 처리
                        expManager.addExpFromRoutine(routine);
                    } else if (!completed && routine.isCompleted()) {
                         routineManager.uncompleteRoutine(routine.getId()); // 체크 되지 않고 경험치만 올리고 싶은 경우 주석 처리
                        expManager.removeExpFromRoutine(routine);
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf
                    }
                    updateExpDisplay();
                    saveUserData();
                    updateRoutineList();
                } else if (col == 2) {
                    String newContent = (String) tableModel.getValueAt(row, col);
                    routine.setContent(newContent);
                    saveUserData();
                    updateRoutineList();
                }
            }
        });

<<<<<<< HEAD
        JPanel bottomPanel = new JPanel(new GridLayout(1, 9)); // [수정] 버튼 수 증가에 따라 9로 조정
=======
        // 하단 패널: 루틴 추가, 정렬 버튼 및 로그아웃 버튼 그룹 생성
        JPanel bottomPanel = new JPanel(new GridLayout(1, 7));
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf

        JButton addNormalBtn = new JButton("일반 루틴 추가");
        addNormalBtn.addActionListener(e -> {
            String content = JOptionPane.showInputDialog(this, "일반 루틴 내용을 입력하세요:");
            if (content != null && !content.trim().isEmpty()) {
                Gemini gemini = new Gemini();
                int difficulty = gemini.getDif(content);
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
                Gemini gemini = new Gemini();
                int difficulty = gemini.getDif(content);
                routineManager.addDailyRoutine(content, difficulty);
                updateRoutineList();
                saveUserData();
            }
        });
        bottomPanel.add(addDailyBtn);

<<<<<<< HEAD
        // --- 정렬 드롭다운 버튼 ---
        JButton sortModeButton = new JButton("정렬: 등록일순"); // 기본값 표시
        sortModeButton.setPreferredSize(new Dimension(130, 30)); // [추가] 버튼 크기 고정
        JPopupMenu sortMenu = new JPopupMenu();
        sortMenu.setPreferredSize(new Dimension(130, 90)); // [추가] 드롭다운 메뉴 크기 고정

        JMenuItem sortByNameItem = new JMenuItem("이름순");
        sortByNameItem.setPreferredSize(new Dimension(130, 30)); // [추가] 메뉴 항목 크기 고정
        sortByNameItem.addActionListener(e -> {
            updateRoutineList(routineManager.getRoutinesSortedByName());
            sortModeButton.setText("정렬: 이름순");
        });
        sortMenu.add(sortByNameItem);

        JMenuItem sortByCompleteItem = new JMenuItem("완료순");
        sortByCompleteItem.setPreferredSize(new Dimension(130, 30)); // [추가]
        sortByCompleteItem.addActionListener(e -> {
            updateRoutineList(routineManager.getRoutinesSortedByComplete());
            sortModeButton.setText("정렬: 완료순");
        });
        sortMenu.add(sortByCompleteItem);

        JMenuItem sortByRegisterItem = new JMenuItem("등록일순");
        sortByRegisterItem.setPreferredSize(new Dimension(130, 30)); // [추가]
        sortByRegisterItem.addActionListener(e -> {
            updateRoutineList(routineManager.getRoutinesSortedByRegister());
            sortModeButton.setText("정렬: 등록일순");
        });
        sortMenu.add(sortByRegisterItem);

        sortModeButton.addActionListener(e -> {
            sortMenu.show(sortModeButton, 0, sortModeButton.getHeight());
        });
        bottomPanel.add(sortModeButton);

        // --- 통계 보기 버튼 ---
        JButton viewStatsBtn = new JButton("통계 보기");
        viewStatsBtn.addActionListener(e -> showStatisticsChart());
        bottomPanel.add(viewStatsBtn);

        // --- 칭호 선택 버튼 ---
        JButton selectTitleBtn = new JButton("칭호 선택");
        selectTitleBtn.addActionListener(e -> {
            TitleManager.showTitleSelectionDialog(userData, this);
            updateTitleDisplay();
        });
        bottomPanel.add(selectTitleBtn);

=======
        // 정렬 버튼 3종 (이름순, 완료순, 등록일순)
        JButton sortByNameBtn = new JButton("이름순 정렬");
        sortByNameBtn.addActionListener(e -> updateRoutineList(routineManager.getRoutinesSortedByName()));
        bottomPanel.add(sortByNameBtn);

        JButton sortByCompleteBtn = new JButton("완료순 정렬");
        sortByCompleteBtn.addActionListener(e -> updateRoutineList(routineManager.getRoutinesSortedByComplete()));
        bottomPanel.add(sortByCompleteBtn);

        JButton sortByDateBtn = new JButton("등록일순 정렬");
        sortByDateBtn.addActionListener(e -> updateRoutineList(routineManager.getRoutinesSortedByRegister()));
        bottomPanel.add(sortByDateBtn);

        // --- [추가] "통계 보기" 버튼 ---
        JButton viewStatsBtn = new JButton("통계 보기");
        viewStatsBtn.addActionListener(e -> showStatisticsChart());
        bottomPanel.add(viewStatsBtn);
        // ------------------------------------

        // 로그아웃 버튼
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf
        JButton logoutBtn = new JButton("로그아웃");
        logoutBtn.addActionListener(e -> logout());
        bottomPanel.add(logoutBtn);

        // 하단 패널 레이아웃 재조정 (GridLayout 컬럼 수 변경)
        // 기존 7개 버튼 + 통계 보기 버튼 1개 = 총 8개
        // GridLayout(1, 8)은 그대로 유지됩니다. 만약 버튼 수가 달라진다면 이 숫자를 조정해야 합니다.

        add(bottomPanel, BorderLayout.SOUTH);

        updateRoutineList();
    }

    private void updateExpDisplay() {
        levelLabel.setText("Lv." + userData.getLevel() + " | EXP: " + userData.getExp() + "/" + userData.getNeedExp());
    }

    private void updateTitleDisplay() {
        if (titleLabel != null) {
            titleLabel.setText("《현재 칭호》  " + userData.getCurrentTitle());
        }
    }

    private void updateRoutineList() {
        updateRoutineList(routineManager.getAllRoutines());
    }

    private void updateRoutineList(List<Routine> routines) {
        tableModel.setRowCount(0);
        for (Routine routine : routines) {
            String typeStr;
            String streakInfo = "";

            if (routine instanceof DailyRoutine) {
                typeStr = "[일일]";
                streakInfo = String.valueOf(((DailyRoutine) routine).getStreakCount());
            } else {
                typeStr = "[일반]";
            }

            Object[] row = {
                    routine.isCompleted(),
                    typeStr,
                    routine.getContent(),
                    routine.getDifficulty(),
                    streakInfo,
                    routine.getId()
            };
            tableModel.addRow(row);
        }
    }

<<<<<<< HEAD
=======
    // [제거] 현재 JTable UI와 관련 없는 불필요한 메서드들 제거 (addRoutine(String), updateStatusPanel, createRoutineItem)
    // 이 메서드들은 이전 버전의 UI에서 사용되었던 것으로 보입니다.

    // 변경된 사용자 데이터 저장 호출
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf
    private void saveUserData() {
        Database.updateUserData(userData);
    }

    private void logout() {
        saveUserData();
        MainAppGUI.logout();
        dispose();
    }

<<<<<<< HEAD
    // 경험치 증가량 표시 후 서서히 사라지도록 페이드 아웃 처리
    //게임과 유사하게 표현
    private void showExpDelta(int delta) {
        if (delta == 0) return;

        expDeltaLabel.setText((delta > 0 ? "+" : "") + delta);
        expDeltaLabel.setVisible(true);
        alpha = 1.0f;
        expDeltaLabel.setForeground(new Color(0, 128, 0, 255)); // 완전 불투명 초록

        // 이전 타이머가 실행 중이면 중지
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
=======
    // --- [추가] 차트 보기 메서드 ---
    private void showStatisticsChart() {
        if (this.userData == null) {
            JOptionPane.showMessageDialog(this, "사용자 데이터가 로드되지 않았습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
>>>>>>> f9a8112672d178bd0b11f368129b2e8975375acf
        }

        fadeTimer = new Timer(50, e -> {
            alpha -= 0.05f; // 투명도 감소
            if (alpha <= 0f) {
                alpha = 0f;
                expDeltaLabel.setVisible(false);
                fadeTimer.stop();
            }
            int alphaInt = (int) (alpha * 255);
            Color baseColor = delta > 0 ? new Color(0, 128, 0) : Color.RED;
            expDeltaLabel.setForeground(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alphaInt));
        });
        fadeTimer.start();
    }

    // [추가] 통계 차트 띄우기 (ChartDisplayFrame 호출)
    private void showStatisticsChart() {
        ChartDisplayFrame chartFrame = new ChartDisplayFrame(userData);
        chartFrame.setVisible(true);
    }
}
