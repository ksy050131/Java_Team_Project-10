// 기존 JTable을 사용하던 UI를 카드형 레이아웃으로 개편한 버전입니다.
// JTable이 아닌 JPanel 내부에 루틴들을 카드 스타일로 배치합니다.
// 깔끔하고 직관적인 인터페이스를 빠르게 구성할 수 있습니다.

package gui;

import app.MainAppGUI;
import data.Database;
import data.Gemini;
import data.UserData;
import exp.ExpManager;
import routine.DailyRoutine;
import routine.Routine;
import routine.RoutineManager;
import chart.ChartDisplayFrame; // [추가] ChartDisplayFrame 클래스 임포트
import title.TitleManager; // [추가] TitleManager 클래스 임포트
import account.Account;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainUI extends JFrame {
    private final UserData userData;
    private final RoutineManager routineManager;
    private final ExpManager expManager;
    private JPanel cardContainer;
    private JLabel levelLabel;
    private JLabel titleLabel;
    private JProgressBar expBar;
    private JLabel expDeltaLabel; // [추가] EXP 증가 표시용 라벨
    // 페이드 아웃용 타이머와 알파 값
    private Timer fadeTimer;
    private float alpha = 1.0f;
    private JLabel cycleLabel;

    public MainUI(UserData userData) {
        this.userData = userData;
        this.routineManager = new RoutineManager(userData.getRoutines() != null ? userData.getRoutines() : new ArrayList<>(), this::saveUserData);
        this.expManager = new ExpManager(userData);
        initUI();
    }

    private void initUI() {
        setTitle("루틴 코치 - 카드 UI");
        setSize(900, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCardScrollPane(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshRoutineCards();
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new GridLayout(3, 1)); // 행 3개로 확장

        // 사용자 환영 문구
        String displayName = userData.getCurrentTitle().isBlank()
                ? userData.getUsername()
                : "[" + userData.getCurrentTitle() + "]" + userData.getUsername();
        JLabel welcome = new JLabel(displayName + "님 환영합니다!");
        welcome.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        // 레벨 및 EXP 바
        levelLabel = new JLabel("Lv." + userData.getLevel());
        expBar = new JProgressBar(0, userData.getNeedExp());
        expBar.setValue(userData.getExp());
        expBar.setString(userData.getExp() + " / " + userData.getNeedExp());
        expBar.setStringPainted(true);

        JPanel expPanel = new JPanel(new BorderLayout());
        expPanel.add(levelLabel, BorderLayout.NORTH);
        expPanel.add(expBar, BorderLayout.SOUTH);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.add(welcome, BorderLayout.WEST);
        topRow.add(expPanel, BorderLayout.EAST);

        // 회차 및 EXP 증가 표시
        JPanel secondRow = new JPanel(new GridLayout(1, 2));
        JLabel cycleLabel = new JLabel("회차: " + userData.getCycle() + "회차", SwingConstants.LEFT);
        cycleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        secondRow.add(cycleLabel);

        expDeltaLabel = new JLabel();
        expDeltaLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        expDeltaLabel.setForeground(new Color(0, 128, 0));
        expDeltaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        expDeltaLabel.setVisible(false);
        secondRow.add(expDeltaLabel);

        // 칭호
        JPanel thirdRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("《현재 칭호》 " + userData.getCurrentTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 14));
        thirdRow.add(titleLabel);

        top.add(topRow);
        top.add(secondRow);
        top.add(thirdRow);

        return top;
    }
    //회차업데이트 메서드
    private void updateCycleDisplay() {
        if (cycleLabel != null) {
            cycleLabel.setText("회차: " + userData.getCycle() + "회차");
        }
    }

    private JScrollPane createCardScrollPane() {
        cardContainer = new JPanel(new GridBagLayout());

        // 감싸는 외부 패널 (LEFT 정렬)
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.add(cardContainer);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 부드러운 스크롤
        return scrollPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new GridLayout(1, 7, 10, 10));
        JButton addNormal = new JButton("일반 루틴 추가");
        addNormal.addActionListener(e -> addRoutine(false));

        JButton addDaily = new JButton("일일 루틴 추가");
        addDaily.addActionListener(e -> addRoutine(true));

        // --- [추가] 정렬 드롭다운 버튼 ---
        JButton sortModeButton = new JButton("정렬: 등록일순"); // 기본값 표시
        sortModeButton.setPreferredSize(new Dimension(130, 30)); // [추가] 버튼 크기 고정
        JPopupMenu sortMenu = new JPopupMenu();
        sortMenu.setPreferredSize(new Dimension(130, 90)); // [추가] 드롭다운 메뉴 크기 고정

        JMenuItem sortByNameItem = new JMenuItem("이름순");
        sortByNameItem.setPreferredSize(new Dimension(130, 30)); // [추가] 메뉴 항목 크기 고정
        sortByNameItem.addActionListener(e -> {
            refreshRoutineCards(routineManager.getRoutinesSortedByName());
            sortModeButton.setText("정렬: 이름순");
        });
        sortMenu.add(sortByNameItem);

        JMenuItem sortByCompleteItem = new JMenuItem("완료순");
        sortByCompleteItem.setPreferredSize(new Dimension(130, 30)); // [추가]
        sortByCompleteItem.addActionListener(e -> {
            refreshRoutineCards(routineManager.getRoutinesSortedByComplete());
            sortModeButton.setText("정렬: 완료순");
        });
        sortMenu.add(sortByCompleteItem);

        JMenuItem sortByRegisterItem = new JMenuItem("등록일순");
        sortByRegisterItem.setPreferredSize(new Dimension(130, 30)); // [추가]
        sortByRegisterItem.addActionListener(e -> {
            refreshRoutineCards(routineManager.getRoutinesSortedByRegister());
            sortModeButton.setText("정렬: 등록일순");
        });
        sortMenu.add(sortByRegisterItem);

        sortModeButton.addActionListener(e -> {
            sortMenu.show(sortModeButton, 0, sortModeButton.getHeight());
        });

        JButton viewStatsBtn = new JButton("통계 보기");
        viewStatsBtn.addActionListener(e -> showStatisticsChart());

        // --- [추가] 칭호 선택 버튼 ---
        JButton selectTitleBtn = new JButton("칭호 선택");
        selectTitleBtn.addActionListener(e -> {
            TitleManager.showTitleSelectionDialog(userData, this);
            updateTitleDisplay();
        });

                // --- 회원 탈퇴 버튼을 설정 드롭다운으로 대체 ---
        JButton settingsButton = new JButton("설정");
        settingsButton.setPreferredSize(new Dimension(80, 30));
        JPopupMenu settingsMenu = new JPopupMenu();


        JMenuItem changePasswordItem = new JMenuItem("비밀번호 변경");
        changePasswordItem.addActionListener(e -> {
            String current = JOptionPane.showInputDialog(this, "현재 비밀번호:");
            String newPwd = JOptionPane.showInputDialog(this, "새 비밀번호:");
            if (current != null && newPwd != null) {
                boolean changed = MainAppGUI.getAccount().changePassword(userData.getUserId(), current, newPwd);
                if (changed) {
                    JOptionPane.showMessageDialog(this, "비밀번호가 변경되었습니다.");
                    userData.setPassword(MainAppGUI.getAccount().encrypt(newPwd)); // userData 내부도 갱신
                    saveUserData(); // 변경된 비밀번호 저장
                } else {
                    JOptionPane.showMessageDialog(this, "현재 비밀번호가 틀렸습니다.");
                }
            }
        });

        JMenuItem deleteAccountItem = new JMenuItem("회원 탈퇴");
        deleteAccountItem.addActionListener(e -> {
            String password = JOptionPane.showInputDialog(this, "비밀번호를 입력하세요:");
            if (password == null) return;

            String confirm = JOptionPane.showInputDialog(this, "정말 탈퇴하시겠습니까?\n탈퇴하려면 'CONFIRM'을 입력하세요:");
            if (confirm == null || !confirm.equalsIgnoreCase("CONFIRM")) {
                JOptionPane.showMessageDialog(this, "회원 탈퇴가 취소되었습니다.");
                return;
            }

            String userId = userData.getUserId(); // 자동으로 현재 사용자 ID 사용
            boolean deleted = MainAppGUI.getAccount().deleteAccount(userId, password);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "회원 탈퇴가 완료되었습니다.");
                MainAppGUI.logout();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "탈퇴 실패: 비밀번호가 틀렸습니다.");
            }
        });


        settingsMenu.add(changePasswordItem);
        settingsMenu.add(deleteAccountItem);

        settingsButton.addActionListener(e -> {
            settingsMenu.show(settingsButton, 0, settingsButton.getHeight());
        });

        JButton logout = new JButton("로그아웃");
        logout.addActionListener(e -> logout());


        bottom.add(addNormal);
        bottom.add(addDaily);
        bottom.add(sortModeButton);
        bottom.add(viewStatsBtn);
        bottom.add(selectTitleBtn);
        bottom.add(settingsButton);
        bottom.add(logout);

        return bottom;
    }

    private void refreshRoutineCards() {
        refreshRoutineCards(routineManager.getAllRoutines());
    }

    private void refreshRoutineCards(List<Routine> routines) {
        cardContainer.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 카드 간 간격
        gbc.anchor = GridBagConstraints.FIRST_LINE_START; // 왼쪽 상단 정렬
        gbc.fill = GridBagConstraints.NONE;

        int cols = 4; // 열 개수 고정
        for (int i = 0; i < routines.size(); i++) {
            Routine routine = routines.get(i);
            JPanel card = createRoutineCard(routine);

            gbc.gridx = i % cols;
            gbc.gridy = i / cols;

            cardContainer.add(card, gbc);
        }

        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private JPanel createRoutineCard(Routine routine) {
        JPanel card = new JPanel();
        Dimension cardSize = new Dimension(200, 140); // ← 고정 크기 설정
        card.setPreferredSize(cardSize);
        card.setMaximumSize(cardSize); // ← GridLayout에서 크기 강제 고정
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.setBackground(Color.WHITE);

        JCheckBox check = new JCheckBox(routine.getContent());
        check.setSelected(routine.isCompleted());
        check.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        check.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel diff = new JLabel("난이도: " + routine.getDifficulty());
        diff.setAlignmentX(Component.LEFT_ALIGNMENT);

        String typeStr = (routine instanceof DailyRoutine) ? "[일일]" : "[일반]";
        JLabel type = new JLabel(typeStr);
        type.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton delete = new JButton("삭제");
        delete.setAlignmentX(Component.LEFT_ALIGNMENT);
        delete.setMaximumSize(new Dimension(60, 25));

        delete.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                routineManager.deleteRoutine(routine.getId());
                refreshRoutineCards();
            }
        });

        check.addActionListener(e -> {
            int delta = 0;
            if (check.isSelected()) {
                delta = expManager.addExpFromRoutine(routine);
                routineManager.completeRoutine(routine.getId());
            } else {
                delta = -expManager.removeExpFromRoutine(routine);
                routineManager.uncompleteRoutine(routine.getId());
            }
            updateExpDisplay();
            updateCycleDisplay();
            saveUserData();
            showExpDelta(delta);
        });

        // [추가] 수정 버튼 추가
        JButton edit = new JButton("수정");
        edit.setAlignmentX(Component.LEFT_ALIGNMENT);

        edit.addActionListener(e -> {
            String newContent = JOptionPane.showInputDialog(this, "루틴 내용을 수정하세요:", routine.getContent());
            if (newContent != null && !newContent.trim().isEmpty()) {
                routine.setContent(newContent);
                int newDiff = new Gemini().getDif(newContent);
                routine.setDifficulty(newDiff);
                saveUserData();
                refreshRoutineCards();
            }
        });

        card.add(check);
        card.add(diff);
        card.add(type);
        card.add(Box.createVerticalGlue()); // 여유 공간

        // 버튼 영역 패널 (BorderLayout 사용)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(delete);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(edit);

        card.add(buttonPanel);
        return card;
    }


    private void addRoutine(boolean isDaily) {
        String content = JOptionPane.showInputDialog(this, isDaily ? "일일 루틴 내용" : "일반 루틴 내용");
        if (content != null && !content.trim().isEmpty()) {
            Gemini gemini = new Gemini();
            int difficulty = gemini.getDif(content);
            if (isDaily)
                routineManager.addDailyRoutine(content, difficulty);
            else
                routineManager.addRoutine(content, difficulty);
            refreshRoutineCards();
            saveUserData();
        }
    }

    private void updateExpDisplay() {
        int currentExp = userData.getExp();
        int maxExp = userData.getNeedExp();

        levelLabel.setText("Lv." + userData.getLevel());
        expBar.setMaximum(maxExp);
        expBar.setValue(currentExp);
        expBar.setString(currentExp + " / " + maxExp);
        expBar.setStringPainted(true);
    }

    private void updateTitleDisplay() {
        if (titleLabel != null) {
            titleLabel.setText("《현재 칭호》  " + userData.getCurrentTitle());
        }
    }

    // [추가] 통계 보기
    private void showStatisticsChart() {
        if (this.userData == null) {
            JOptionPane.showMessageDialog(this, "사용자 데이터가 로드되지 않았습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            ChartDisplayFrame chartFrame = new ChartDisplayFrame(this.userData);
            chartFrame.setVisible(true);
        });
    }

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

    private void saveUserData() {
        Database.updateUserData(userData);
    }

    private void logout() {
        saveUserData();
        MainAppGUI.logout();
        dispose();
    }
}
