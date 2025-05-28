package gui;

import javax.swing.*;
import java.awt.*;

/**
 * frame
 * centerPanel
 * |-- Sign in (default)
 *      |-- input id
 *      |-- input password
 *      if correct -> move to MainUI
 *
 * bottomPanel?
 * |-- Sign up (create account)
 *      |-- if clicked -> move to SignUpPage
 */

public class DefaultSignUpPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Sign Up Page");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // centerPanel 생성
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 제목
        JLabel title = new JLabel("Sign Up Page", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(20));

        // 입력 폼 패널 (중앙, GridBagLayout 사용)
        // Sign Up button도 같이 넣음
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 닉네임 라벨
        JLabel nicknameLabel = new JLabel("Nickname: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nicknameLabel, gbc);

        // 닉네임 입력창
        JTextField nicknameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nicknameField, gbc);

        // 비밀번호 라벨
        JLabel passwordLabel = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordLabel, gbc);

        // 비밀번호 입력창
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Sign In button
        JButton signInButton = new JButton("Sign In");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(signInButton, gbc);

        centerPanel.add(formPanel);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
