package gui;

import javax.swing.*;
import java.awt.*;
import data.UserData;

public class MainUI {
    private JFrame frame;
    private UserData user;

    public MainUI(UserData user) {
        this.user = user;
        frame = new JFrame("Habit Tracker✧");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // 상단
        JLabel title = new JLabel("Habit Tracker", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        // centerPanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 루틴 패널
        JPanel routinePanel = new JPanel();
        routinePanel.setLayout(new BoxLayout(routinePanel, BoxLayout.Y_AXIS));
        routinePanel.setFont(new Font("NanumBarunGothic", Font.PLAIN, 40));
        routinePanel.add(new JLabel("Routine List"));

        // 샘플 체크박스 (나중에 user.getRoutines()로 대체)
        JCheckBox cb1 = new JCheckBox("자바 공부");
        JCheckBox cb2 = new JCheckBox("영어 단어 암기");
        routinePanel.add(cb1);
        routinePanel.add(cb2);

        centerPanel.add(routinePanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // 버튼들
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        String[] btnNames = {"Add Routine", "Remove Routine", "Complete Routine"};
        for (String name : btnNames) {
            JButton btn = new JButton(name);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(btn);
            buttonPanel.add(Box.createVerticalStrut(10));
        }

        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        frame.add(centerPanel, BorderLayout.CENTER);

        // 하단: 경험치
        JPanel bottomPanel = new JPanel();
        JLabel levelLabel = new JLabel("레벨: " + user.getLevel()); // 예시
        JProgressBar expBar = new JProgressBar(0, 100);
        expBar.setValue(60);
        expBar.setString("60 / 100");
        expBar.setStringPainted(true);
        bottomPanel.add(levelLabel);
        bottomPanel.add(expBar);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void show() {
        frame.setVisible(true);
    }
}