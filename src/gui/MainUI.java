package gui;

import app.MainAppGUI;
import data.Database; // Database 클래스 임포트 추가
import data.UserData;
import exp.ExpManager;
import routine.Routine;
import routine.Routine.RoutineType;
import routine.RoutineManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class MainUI extends JFrame {
    private final UserData userData;
    private final RoutineManager routineManager;
    private final ExpManager expManager;
    private JTextArea routineListArea;

    public MainUI(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(
                userData.getRoutines(),
                this::saveUserData
        );
        this.expManager = new ExpManager(userData);
        initUI();
    }

    private void initUI() {
        setTitle("루틴 코치 - " + userData.getUsername() + "님");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널: 사용자 정보
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        JLabel welcomeLabel = new JLabel(userData.getUsername() + "님 환영합니다!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        topPanel.add(welcomeLabel);

        JLabel levelLabel = new JLabel("Lv." + userData.getLevel() + " | EXP: " +
                userData.getExp() + "/" + userData.getNeedExp(), SwingConstants.RIGHT);
        levelLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        topPanel.add(levelLabel);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: 루틴 목록
        JPanel centerPanel = new JPanel(new BorderLayout());
        routineListArea = new JTextArea();
        routineListArea.setEditable(false);
        updateRoutineList();

        JScrollPane scrollPane = new JScrollPane(routineListArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단 패널: 버튼 그룹
        JPanel bottomPanel = new JPanel(new GridLayout(1, 5));

        JButton addNormalBtn = new JButton("일반 루틴 추가");
        addNormalBtn.addActionListener(e -> addRoutine(RoutineType.NORMAL));
        bottomPanel.add(addNormalBtn);

        JButton addDailyBtn = new JButton("일일 루틴 추가");
        addDailyBtn.addActionListener(e -> addRoutine(RoutineType.DAILY));
        bottomPanel.add(addDailyBtn);

        JButton addStreakBtn = new JButton("연속 루틴 추가");
        addStreakBtn.addActionListener(e -> addRoutine(RoutineType.STREAK));
        bottomPanel.add(addStreakBtn);

        JButton completeBtn = new JButton("완료 처리");
        completeBtn.addActionListener(e -> completeRoutine());
        bottomPanel.add(completeBtn);

        JButton logoutBtn = new JButton("로그아웃");
        logoutBtn.addActionListener(e -> logout());
        bottomPanel.add(logoutBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addRoutine(RoutineType type) {
        String content = JOptionPane.showInputDialog(this, "루틴 내용을 입력하세요:");
        if (content != null && !content.trim().isEmpty()) {
            int difficulty = 3; // 기본값 (실제로는 Gemini API 사용)
            routineManager.addRoutine(content, difficulty, type);
            updateRoutineList();
            saveUserData();
        }
    }

    private void completeRoutine() {
        String id = JOptionPane.showInputDialog(this, "완료할 루틴 ID를 입력하세요:");
        if (id != null && !id.trim().isEmpty()) {
            Optional<Routine> routineOpt = routineManager.getRoutineById(id);
            if (routineOpt.isPresent()) {
                routineManager.completeRoutine(id);
                expManager.addExpFromRoutine(routineOpt.get());
                updateRoutineList();
                saveUserData();
                JOptionPane.showMessageDialog(this, "루틴 완료 처리되었습니다!");
            } else {
                JOptionPane.showMessageDialog(this, "해당 ID의 루틴을 찾을 수 없습니다.");
            }
        }
    }

    private void updateRoutineList() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 루틴 목록 ===\n");

        for (Routine routine : routineManager.getAllRoutines()) {
            String type = "";
            switch (routine.getType()) {
                case DAILY: type = "[일일]"; break;
                case STREAK: type = "[연속]"; break;
                default: type = "[일반]";
            }

            sb.append(String.format("%s %s (난이도: %d)\n",
                    type, routine.getContent(), routine.getDifficulty()));
            sb.append(String.format("  상태: %s | ID: %s\n",
                    routine.isCompleted() ? "완료" : "미완료", routine.getId()));

            if (routine.getType() == RoutineType.STREAK) {
                sb.append(String.format("  연속 완료: %d일\n", routine.getStreakCount()));
            }

            sb.append("----------------------------\n");
        }

        routineListArea.setText(sb.toString());
    }

    private void saveUserData() {
        Database.updateUserData(userData);
    }

    private void logout() {
        saveUserData();
        MainAppGUI.logout();
        dispose();
    }
}
