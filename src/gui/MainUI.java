package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import data.UserData;
import routine.Routine;
import routine.RoutineManager;
import data.Database;
import exp.ExpManager;

public class MainUI {
    private JFrame frame; // 메인 프레임
    private UserData user; // 로그인된 사용자 정보
    private JPanel routinePanel; // 루틴 목록을 보여줄 패널
    private RoutineManager routineManager; // 루틴 로직 처리 클래스
    private ExpManager expManager; // 경험치 및 레벨 관리 객체
    private JLabel levelLabel; // 레벨 표시 라벨
    private JProgressBar expBar; // 경험치 표시 프로그레스바

    public MainUI(UserData user) {
        this.user = user;
        this.routineManager = new RoutineManager(user.getRoutines(), () -> Database.updateUserData(user));
        this.expManager = new ExpManager(user);

        // 프레임 초기화 및 종료 시 저장 처리
        frame = new JFrame("Habit Tracker✧");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 화면 가운데 위치

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Database.updateUserData(user); // 창 닫을 때 데이터 저장
            }
        });

        initUI(); // UI 초기화
    }

    // UI 초기 구성 메서드
    private void initUI() {
        // 상단 제목 설정
        JLabel title = new JLabel("Habit Tracker", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        // 중앙 전체 패널 구성
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 루틴 목록 패널 초기화
        routinePanel = new JPanel();
        routinePanel.setLayout(new BoxLayout(routinePanel, BoxLayout.Y_AXIS));
        routinePanel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        routinePanel.add(new JLabel("Routine List"));

        // 기존 루틴들 UI에 추가
        for (Routine routine : user.getRoutines()) {
            routinePanel.add(createRoutineItem(routine));
        }

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

        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());
        frame.add(centerPanel, BorderLayout.CENTER);

        // 하단 경험치 상태 표시 패널 구성
        JPanel bottomPanel = new JPanel();
        levelLabel = new JLabel();
        expBar = new JProgressBar();
        expBar.setStringPainted(true);
        bottomPanel.add(levelLabel);
        bottomPanel.add(expBar);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.add(bottomPanel, BorderLayout.SOUTH);

        updateStatusPanel(); // 경험치/레벨 초기 상태 표시
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

    // 경험치/레벨 상태 UI 갱신 메서드
    private void updateStatusPanel() {
        levelLabel.setText("레벨: " + user.getLevel());
        expBar.setMaximum(user.getNeedExp());
        expBar.setValue(user.getExp());
        expBar.setString(user.getExp() + " / " + user.getNeedExp());
    }

    // 화면 보이기 메서드
    public void show() {
        frame.setVisible(true);
    }
}
