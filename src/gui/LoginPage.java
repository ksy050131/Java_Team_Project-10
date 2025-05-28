package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import account.Account;
import data.UserData;
import app.MainAppGUI; // MainUI와 연결되는 클래스

public class LoginPage {
    private JFrame frame;
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginPage() {
        frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel idLabel = new JLabel("아이디:");
        idField = new JTextField(15);

        JLabel pwLabel = new JLabel("비밀번호:");
        passwordField = new JPasswordField(15);

        JButton loginBtn = new JButton("로그인");
        JButton signUpBtn = new JButton("회원가입");

        // 배치
        gbc.gridx = 0; gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridx = 1; panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(pwLabel, gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(loginBtn, gbc);

        gbc.gridy = 3;
        panel.add(signUpBtn, gbc);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 이벤트 연결
        loginBtn.addActionListener(this::handleLogin);
        signUpBtn.addActionListener(e -> {
            frame.dispose();
            new SignUpPage().show(); // 회원가입 화면으로 이동
        });
    }

    private void handleLogin(ActionEvent e) {
        String id = idField.getText();
        String pw = new String(passwordField.getPassword());

        Account account = MainAppGUI.getAccount();
        if (account.login(id, pw)) {
            UserData user = account.getUser();
            frame.dispose();
            MainAppGUI.loginSuccess(user);  // loginSuccess()에서 로그인 성공 시 MainUI 실행
        } else {
            JOptionPane.showMessageDialog(frame, "로그인 실패");
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}
