package gui;

import app.MainAppGUI;
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
        setLocationRelativeTo(null); // 화면 중앙 정렬

        setLayout(new BorderLayout());

        // 중앙 입력 패널
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 아이디/비밀번호 입력
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        inputPanel.add(new JLabel("아이디:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.NORTH);

        // 로그인 & 회원가입 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton loginButton = new JButton("로그인");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        loginButton.addActionListener(new LoginListener());
        buttonPanel.add(loginButton);

        JButton signUpButton = new JButton("회원가입");
        signUpButton.setPreferredSize(new Dimension(120, 35));
        signUpButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        signUpButton.addActionListener(e -> {
            SignUpPage signUpPage = new SignUpPage();
            signUpPage.setVisible(true);
            dispose();
        });
        buttonPanel.add(signUpButton);

        add(buttonPanel, BorderLayout.CENTER);

        // 보조 기능 (아이디 찾기, 비밀번호 재설정, 회원 탈퇴)
        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 텍스트 링크 스타일 버튼 생성 함수
        JButton findIdButton = createFlatTextButton("아이디 찾기");
        JButton findPwButton = createFlatTextButton("비밀번호 찾기");

        // 아이디 찾기
        findIdButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "이름:");
            String phone = JOptionPane.showInputDialog(this, "전화번호 (010-XXXX-XXXX):");
            String birth = JOptionPane.showInputDialog(this, "생년월일 (YYYY-MM-DD):");

            String userId = MainAppGUI.getAccount().findId(phone, birth, name);
            if (userId != null) {
                JOptionPane.showMessageDialog(this, "아이디: " + userId);
            } else {
                JOptionPane.showMessageDialog(this, "일치하는 아이디를 찾을 수 없습니다.");
            }
        });

        // 비밀번호 찾기
        findPwButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "이름:");
            String phone = JOptionPane.showInputDialog(this, "전화번호 (010-XXXX-XXXX):");
            String birth = JOptionPane.showInputDialog(this, "생년월일 (YYYY-MM-DD):");
            String newPassword = JOptionPane.showInputDialog(this, "새 비밀번호:");

            boolean success = MainAppGUI.getAccount().changePassword2(name, phone, birth, newPassword);
            JOptionPane.showMessageDialog(this,
                    success ? "비밀번호가 재설정되었습니다." : "정보가 일치하지 않습니다.");
        });

        linkPanel.add(findIdButton);
        linkPanel.add(findPwButton);

        add(linkPanel, BorderLayout.SOUTH);
    }

    // 로그인 처리
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

    // 텍스트 스타일 버튼 생성
    private JButton createFlatTextButton(String text) {
        JButton btn = new JButton(text);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        btn.setForeground(Color.BLUE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
