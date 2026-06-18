package com.example.dean12.config;

import java.io.File;

public final class DatabaseSettings {

    private static final String DB_DIR =
            System.getProperty("user.home") + File.separator + ".dean12";

    private static final String JDBC_URL =
            "jdbc:h2:file:" + DB_DIR.replace('\\', '/') + "/school_db"
                    + ";MODE=PostgreSQL";

    static {
        new File(DB_DIR).mkdirs();
    }

    private DatabaseSettings() {
    }

    public static String getJdbcUrl() {
        return JDBC_URL;
    }

    public static String getUsername() {
        return "sa";
    }

    public static String getPassword() {
        return "";
    }

    public static String getDataDirectory() {
        return DB_DIR;
    }
}
