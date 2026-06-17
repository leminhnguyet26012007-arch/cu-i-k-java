package com.example.dean12.desktop.network;

import com.example.dean12.config.DatabaseSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static final Properties PROPS = new Properties();

    static {
        PROPS.setProperty("server.host", "localhost");
        PROPS.setProperty("server.port", "9000");
        PROPS.setProperty("database.url", DatabaseSettings.getJdbcUrl());
        PROPS.setProperty("database.username", DatabaseSettings.getUsername());
        PROPS.setProperty("database.password", DatabaseSettings.getPassword());

        File external = new File("config.properties");
        if (external.exists()) {
            try (InputStream is = new FileInputStream(external)) {
                Properties extra = new Properties();
                extra.load(is);
                copyIfPresent(extra, "server.host");
                copyIfPresent(extra, "server.port");
                copyIfPresent(extra, "database.url");
                copyIfPresent(extra, "database.username");
                copyIfPresent(extra, "database.password");
                System.out.println("[Config] Loaded config.properties");
            } catch (IOException e) {
                System.err.println("[Config] Cannot read config.properties: " + e.getMessage());
            }
        }
        System.out.println("[Config] Database: " + PROPS.getProperty("database.url"));
    }

    public static String getProperty(String key) {
        return PROPS.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }

    private static void copyIfPresent(Properties from, String key) {
        String value = from.getProperty(key);
        if (value != null && !value.isBlank()) {
            PROPS.setProperty(key, value.trim());
        }
    }

    public static int getIntProperty(String key, int defaultValue) {
        String val = PROPS.getProperty(key);
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
