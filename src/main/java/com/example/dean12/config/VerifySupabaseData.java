package com.example.dean12.config;

import com.example.dean12.desktop.network.ServerDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class VerifySupabaseData {
    public static void main(String[] args) throws Exception {
        try (Connection conn = ServerDao.getConnection();
             Statement stmt = conn.createStatement()) {
            printCount(stmt, "users");
            printCount(stmt, "giang_vien");
            printCount(stmt, "sinh_vien");
            printCount(stmt, "mon_hoc");
            printCount(stmt, "lop_hoc_phan");
            printCount(stmt, "dang_ky_hoc");
            printCount(stmt, "diem");
            printCount(stmt, "notifications");
            printCount(stmt, "system_logs");
        }
    }

    private static void printCount(Statement stmt, String table) throws Exception {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            System.out.println(table + "=" + rs.getInt(1));
        }
    }
}
