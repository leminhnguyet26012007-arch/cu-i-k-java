package com.example.dean12.config;

import com.example.dean12.desktop.data.DesktopDao;
import com.example.dean12.desktop.network.ServerDao;
import com.example.dean12.model.User;

import java.sql.Connection;
import java.sql.Statement;

public class VerifyLoginData {
    public static void main(String[] args) {
        DesktopDao dao = new DesktopDao();
        dao.initializeDatabaseSchema();

        verify(dao, "admin", "123");
        verify(dao, "gv01", "123");
        verify(dao, "sv01", "123");
        shutdownH2IfUsed();
    }

    private static void verify(DesktopDao dao, String username, String password) {
        User user = dao.login(username, password);
        if (user == null) {
            throw new IllegalStateException("Login failed for " + username);
        }
        System.out.println(username + " OK (" + user.getRole() + ")");
    }

    private static void shutdownH2IfUsed() {
        try (Connection conn = ServerDao.getConnection();
             Statement stmt = conn.createStatement()) {
            if (conn.getMetaData().getURL().startsWith("jdbc:h2:")) {
                stmt.execute("SHUTDOWN");
            }
        } catch (Exception ignored) {
            // Only used by this command-line verification helper.
        }
    }
}
