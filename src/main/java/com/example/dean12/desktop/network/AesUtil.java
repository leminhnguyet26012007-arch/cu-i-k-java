package com.example.dean12.desktop.network;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesUtil {
    private static final String ALGORITHM = "AES";
    
    // Secret 16-byte key for AES-128
    private static final byte[] KEY_BYTES = "Secr3tStUd3ntK3y".getBytes(StandardCharsets.UTF_8);

    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) return plainText;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY_BYTES, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("[Security] Encryption error: " + e.getMessage());
            return plainText;
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) return encryptedText;
        try {
            // Check if it is a valid Base64 string to avoid crashing on old unencrypted database entries
            byte[] decodedBytes;
            try {
                decodedBytes = Base64.getDecoder().decode(encryptedText);
            } catch (IllegalArgumentException e) {
                // Not base64, return original (was unencrypted)
                return encryptedText;
            }

            SecretKeySpec secretKey = new SecretKeySpec(KEY_BYTES, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // If decryption fails (e.g. key mismatch or wasn't encrypted), return the original text
            return encryptedText;
        }
    }
}
