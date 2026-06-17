package com.example.dean12.desktop;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        return ENCODER.matches(rawPassword, encodedPassword);
    }

    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }
}

