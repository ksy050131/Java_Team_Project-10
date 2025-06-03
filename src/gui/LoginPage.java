package gui;

import app.MainAppGUI;
import data.UserData;
import account.Account;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("루틴 코치 - 로그인");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("아이디:"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(new LoginListener());
        add(loginButton);

        JButton signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> {
            SignUpPage signUpPage = new SignUpPage();
            signUpPage.setVisible(true); // setVisible 호출
            dispose();
        });
        add(signUpButton);
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = idField.getText();
            String password = new String(passwordField.getPassword());

            if (MainAppGUI.getAccount().login(userId, password)) {
                MainAppGUI.loginSuccess(MainAppGUI.getAccount().getUser());
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginPage.this, "로그인 실패");
            }
        }
    }
}
