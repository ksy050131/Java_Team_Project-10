import javax.swing.*;
import java.awt.*;

public class SwingExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("루틴 코치");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🔹 상단 타이틀
        JLabel title = new JLabel("- 습관 루틴 코치", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        frame.add(title, BorderLayout.NORTH);

        // 🔹 가운데 루틴 목록
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JCheckBox r1 = new JCheckBox("아침 운동");
        JCheckBox r2 = new JCheckBox("영어 단어 암기");
        centerPanel.add(r1);
        centerPanel.add(Box.createVerticalStrut(10)); // 간격
        centerPanel.add(r2);

        frame.add(centerPanel, BorderLayout.CENTER);

        // 🔹 하단 상태바
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel levelLabel = new JLabel("레벨: 3");
        JProgressBar expBar = new JProgressBar(0, 100);
        expBar.setValue(60);
        expBar.setString("60/100 경험치");
        expBar.setStringPainted(true);

        bottomPanel.add(levelLabel);
        bottomPanel.add(expBar);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
