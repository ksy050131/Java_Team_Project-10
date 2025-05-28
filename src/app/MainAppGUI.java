package app;

import data.UserData;
import account.Account;
import gui.LoginPage;
import gui.MainUI;

import javax.swing.*;

public class MainAppGUI {
    private static Account account = new Account();
    private static UserData currentUser;

    public static void main(String[] args) {
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
