// 이 코드는 Swing 기반 Habit Tracker의 UI를 카드형 레이아웃으로 개편한 버전입니다.
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
        JPanel top = new JPanel(new GridLayout(2, 1));

        JPanel topRow = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel(userData.getUsername() + "님 환영합니다!");
        welcome.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        levelLabel = new JLabel("Lv." + userData.getLevel());
        expBar = new JProgressBar(0, userData.getNeedExp());
        expBar.setValue(userData.getExp());
        expBar.setString(userData.getExp() + " / " + userData.getNeedExp());
        expBar.setStringPainted(true);

        JPanel right = new JPanel(new BorderLayout());
        right.add(levelLabel, BorderLayout.NORTH);
        right.add(expBar, BorderLayout.SOUTH);

        topRow.add(welcome, BorderLayout.WEST);
        topRow.add(right, BorderLayout.EAST);

        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleLabel = new JLabel("칭호: " + userData.getCurrentTitle());
        secondRow.add(titleLabel);

        top.add(topRow);
        top.add(secondRow);
        return top;
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
        JPanel bottom = new JPanel(new GridLayout(1, 6, 10, 10));

        JButton addNormal = new JButton("일반 루틴 추가");
        addNormal.addActionListener(e -> addRoutine(false));

        JButton addDaily = new JButton("일일 루틴 추가");
        addDaily.addActionListener(e -> addRoutine(true));

        JButton sortName = new JButton("이름순");
        sortName.addActionListener(e -> refreshRoutineCards(routineManager.getRoutinesSortedByName()));

        JButton sortComplete = new JButton("완료순");
        sortComplete.addActionListener(e -> refreshRoutineCards(routineManager.getRoutinesSortedByComplete()));

        JButton sortDate = new JButton("등록일순");
        sortDate.addActionListener(e -> refreshRoutineCards(routineManager.getRoutinesSortedByRegister()));

        JButton logout = new JButton("로그아웃");
        logout.addActionListener(e -> logout());

        bottom.add(addNormal);
        bottom.add(addDaily);
        bottom.add(sortName);
        bottom.add(sortComplete);
        bottom.add(sortDate);
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
            if (check.isSelected()) {
                expManager.addExpFromRoutine(routine);
                routineManager.completeRoutine(routine.getId());
            } else {
                expManager.removeExpFromRoutine(routine);
                routineManager.uncompleteRoutine(routine.getId());
            }
            updateExpDisplay();
            saveUserData();
        });

        card.add(check);
        card.add(diff);
        card.add(type);
        card.add(Box.createVerticalGlue()); // 여유 공간
        card.add(delete);

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

    private void saveUserData() {
        Database.updateUserData(userData);
    }

    private void logout() {
        saveUserData();
        MainAppGUI.logout();
        dispose();
    }
}
