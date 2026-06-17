package com.example.dean12.config;

import com.example.dean12.desktop.network.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;

public class SupabaseConnectionTest {
    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");
        DriverManager.setLoginTimeout(10);

        String url = ConfigUtil.getProperty("database.url");
        String username = ConfigUtil.getProperty("database.username");
        String password = ConfigUtil.getProperty("database.password");

        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            System.out.println("Supabase/PostgreSQL connection OK: " + url);
        }
    }
}
