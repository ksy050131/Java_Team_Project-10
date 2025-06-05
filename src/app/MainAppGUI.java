package app;

import account.Account;
import data.UserData;
import gui.LoginPage;
import gui.MainUI;
import javax.swing.*;

public class MainAppGUI {
    private static Account account = new Account();
    private static UserData currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true); // show() 대신 setVisible(true)
        });
    }

    public static void loginSuccess(UserData user) {
        currentUser = user;
        MainUI mainUI = new MainUI(user);
        mainUI.setVisible(true); // show() 대신 setVisible(true)
    }

    public static Account getAccount() {
        return account;
    }

    public static UserData getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true); // show() 대신 setVisible(true)
    }
}
