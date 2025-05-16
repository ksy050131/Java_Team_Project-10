import javax.swing.*;
import java.awt.*;

public class MainUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Habit Tracker✧");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 상단: 제목
        JLabel title = new JLabel("Habit Tracker", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        // 전체 centerPanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 루틴 외부 패널 (왼쪽 정렬)
        JPanel routineWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // 루틴 내부 패널 (수직 정렬)
        JPanel routinePanel = new JPanel();
        routinePanel.setLayout(new BoxLayout(routinePanel, BoxLayout.Y_AXIS));
        JLabel routineLabel = new JLabel("Routine List", SwingConstants.CENTER);
        JCheckBox cb1 = new JCheckBox("자바 공부");
        JCheckBox cb2 = new JCheckBox("영어 단어 암기");

        routinePanel.add(routineLabel);
        routinePanel.add(Box.createVerticalStrut(10));
        routinePanel.add(cb1);
        routinePanel.add(cb2);

        routineWrapperPanel.add(routinePanel);
        centerPanel.add(routineWrapperPanel);

        // 간격 설정
        centerPanel.add(Box.createVerticalStrut(10));

        // 버튼 패널 (중앙 정렬)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Add Routine");
        JButton removeButton = new JButton("Remove Routine");

        // 완료는 버튼X ->  체크박스에 표시하면 완료된 것으로 설정하는 것이 더 나을 듯
        JButton completeButton = new JButton("Complete Routine");

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(completeButton);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue()); // 버튼 패널 위에 고정

        // centerPanel 추가
        frame.add(centerPanel, BorderLayout.CENTER);

        // 경험치/레벨
        JPanel bottomPanel = new JPanel();
        JLabel levelLabel = new JLabel("레벨: 3");
        JProgressBar expBar = new JProgressBar(0, 100);
        expBar.setValue(60);
        expBar.setString("60 / 100");
        expBar.setStringPainted(true);

        bottomPanel.add(levelLabel);
        bottomPanel.add(expBar);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // 보여주기
        frame.setVisible(true);
    }
}
