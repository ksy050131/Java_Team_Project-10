package gui;

import javax.swing.*;
import java.awt.*;
import account.Account;
import app.MainAppGUI;

public class SignUpPage {
    private JFrame frame;

    public SignUpPage() {
        this.frame = new JFrame("Sign Up Page");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // centerPanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 제목
        JLabel title = new JLabel("회원가입", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(20));

        // 입력 폼
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 필드 선언
        JTextField nameField = new JTextField(15);
        JTextField idField = new JTextField(15);
        JPasswordField pwField = new JPasswordField(15);
        JTextField phoneField = new JTextField(15);
        JTextField birthField = new JTextField(15); // YYYY-MM-DD

        // 이름
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("이름:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // 아이디
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("아이디:"), gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        // 비밀번호
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("비밀번호:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pwField, gbc);

        // 전화번호
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("전화번호:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // 생년월일
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("생년월일 (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(birthField, gbc);

        // 회원가입 버튼
        JButton registerBtn = new JButton("회원가입");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerBtn, gbc);

        centerPanel.add(formPanel);
        frame.add(centerPanel, BorderLayout.CENTER);

        // 버튼 클릭 이벤트
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();
            String phone = phoneField.getText().trim();
            String birth = birthField.getText().trim();

            if (name.isEmpty() || id.isEmpty() || pw.isEmpty() || phone.isEmpty() || birth.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "모든 필드를 입력해주세요.");
                return;
            }

            Account account = MainAppGUI.getAccount();
            boolean success = account.register(name, id, pw, phone, birth);
            if (success) {
                JOptionPane.showMessageDialog(frame, "회원가입 성공! 로그인 화면으로 이동합니다.");
                frame.dispose();
                new LoginPage().show();
            } else {
                JOptionPane.showMessageDialog(frame, "회원가입 실패: 아이디 중복 또는 입력 오류.");
            }
        });
    }

    public void show() {
        frame.setVisible(true);
    }
}