package com.example.dean12.desktop;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}

