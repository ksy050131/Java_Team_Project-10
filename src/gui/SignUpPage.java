package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import account.Account;
import app.MainAppGUI;

public class SignUpPage extends JFrame {
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField pwField;
    private JTextField phoneField;
    private JTextField birthField;

    public SignUpPage() {
        setTitle("루틴 코치 - 회원가입");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10)); // 7행 2열, 간격 추가

        // 패딩 설정
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new GridLayout(7, 2, 10, 10));
        setContentPane(contentPane);

        // 이름 필드
        add(new JLabel("이름:"));
        nameField = new JTextField();
        add(nameField);

        // 아이디 필드
        add(new JLabel("아이디:"));
        idField = new JTextField();
        add(idField);

        // 비밀번호 필드
        add(new JLabel("비밀번호:"));
        pwField = new JPasswordField();
        add(pwField);

        // 전화번호 필드
        add(new JLabel("전화번호:"));
        phoneField = new JTextField();
        add(phoneField);

        // 생년월일 필드
        add(new JLabel("생년월일 (YYYY-MM-DD):"));
        birthField = new JTextField();
        add(birthField);

        // 빈 레이블 (공간 확보)
        add(new JLabel());
        add(new JLabel());

        // 가입하기 버튼
        JButton signUpButton = new JButton("가입하기");
        signUpButton.setBackground(new Color(70, 130, 180)); // 파란색
        signUpButton.setForeground(Color.WHITE);
        signUpButton.addActionListener(new SignUpListener());
        add(signUpButton);

        // 취소 버튼
        JButton cancelButton = new JButton("취소");
        cancelButton.setBackground(new Color(220, 20, 60)); // 빨간색
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        add(cancelButton);
    }

    // 회원가입 처리 리스너
    private class SignUpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 필드 값 추출
            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();
            String phone = phoneField.getText().trim();
            String birth = birthField.getText().trim();

            // 유효성 검사
            if (name.isEmpty() || id.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(
                        SignUpPage.this,
                        "이름, 아이디, 비밀번호는 필수 입력 항목입니다.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 생년월일 형식 검사 (간단한 버전)
            if (!birth.isEmpty() && !birth.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(
                        SignUpPage.this,
                        "생년월일은 YYYY-MM-DD 형식으로 입력해주세요.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 회원가입 시도
            Account account = MainAppGUI.getAccount();
            boolean success = account.register(name, id, pw, phone, birth);

            // 결과 처리
            if (success) {
                JOptionPane.showMessageDialog(
                        SignUpPage.this,
                        "회원가입 성공!\n로그인 화면으로 이동합니다.",
                        "가입 완료",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // 로그인 화면으로 전환
                SwingUtilities.invokeLater(() -> {
                    new LoginPage().setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(
                        SignUpPage.this,
                        "회원가입 실패:\n이미 사용 중인 아이디이거나 입력 정보가 잘못되었습니다.",
                        "가입 실패",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}