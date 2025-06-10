package app;

import data.UserData;
import account.Account;
import gui.LoginPage;
import gui.MainUI;

// [추가] swing UI를 더 예쁘게 만들어주는 라이브러리
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class MainAppGUI {
    private static Account account = new Account();
    private static UserData currentUser;

    public static void main(String[] args) {
        try {
            // FlatLaf 라이트 테마 설정 (FlatDarkLaf도 가능)
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf 초기화 실패: " + ex.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> new LoginPage().show());
    }

    public static void loginSuccess(UserData user) {
        currentUser = user;
        new MainUI(user).show();
    }

    public static Account getAccount() {
        return account;
    }

    public static UserData getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
        new LoginPage().show();
    }
}
