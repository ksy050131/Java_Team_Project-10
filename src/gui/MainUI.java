package gui;

import app.MainAppGUI;
import data.Database;
import data.UserData;
import exp.ExpManager;
import routine.Routine;
import routine.Routine.RoutineType;
import routine.RoutineManager;

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
    private DefaultTableModel tableModel;  // JTable에 연결할 테이블 모델

    public MainUI(UserData userData) {
        this.userData = userData;
        // UserData 내 루틴 리스트와 저장 콜백을 전달해 RoutineManager 생성
        this.routineManager = new RoutineManager(userData.getRoutines(), this::saveUserData);
        this.expManager = new ExpManager(userData);
        initUI();
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

        JLabel levelLabel = new JLabel("Lv." + userData.getLevel() + " | EXP: " +
                userData.getExp() + "/" + userData.getNeedExp(), SwingConstants.RIGHT);
        levelLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        firstRow.add(levelLabel);

        topPanel.add(firstRow);

        // 두 번째 행: 회차 정보
        JPanel secondRow = new JPanel(new GridLayout(1, 2));
        JLabel cycleLabel = new JLabel("회차: " + userData.getCycle() + "회차", SwingConstants.LEFT);
        cycleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        secondRow.add(cycleLabel);

        JLabel resetCountLabel = new JLabel("초기화 횟수: " + userData.getLevelResetCount() + "회", SwingConstants.RIGHT);
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
        JPanel centerPanel = new JPanel(new BorderLayout());

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
                        routineManager.completeRoutine(routine.getId());
                        expManager.addExpFromRoutine(routine);
                    } else if (!completed && routine.isCompleted()) {
                        routineManager.uncompleteRoutine(routine.getId());
                        expManager.removeExpFromRoutine(routine);
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
        addNormalBtn.addActionListener(e -> addRoutine(RoutineType.NORMAL));
        bottomPanel.add(addNormalBtn);

        JButton addDailyBtn = new JButton("일일 루틴 추가");
        addDailyBtn.addActionListener(e -> addRoutine(RoutineType.DAILY));
        bottomPanel.add(addDailyBtn);

        JButton addStreakBtn = new JButton("연속 루틴 추가");
        addStreakBtn.addActionListener(e -> addRoutine(RoutineType.STREAK));
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
            String typeStr = switch (routine.getType()) {
                case DAILY -> "[일일]";
                case STREAK -> "[연속]";
                default -> "[일반]";
            };
            Object[] rowData = {
                    routine.isCompleted(),
                    typeStr,
                    routine.getContent(),
                    routine.getDifficulty(),
                    routine.getType() == RoutineType.STREAK ? routine.getStreakCount() : "",
                    routine.getId()  // 숨겨진 ID 컬럼 (식별용)
            };
            tableModel.addRow(rowData);
        }
    }

    // 루틴 추가 다이얼로그 후 추가 처리
    private void addRoutine(RoutineType type) {
        String content = JOptionPane.showInputDialog(this, "루틴 내용을 입력하세요:");
        if (content != null && !content.trim().isEmpty()) {
            int difficulty = 3; // 기본 난이도, 필요 시 수정 가능
            routineManager.addRoutine(content, difficulty, type);
            updateRoutineList();
            saveUserData();
        }
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
}