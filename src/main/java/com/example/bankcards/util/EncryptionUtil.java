package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private final String secret = "verysecretkey";

    public String encrypt(String raw) {
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    public String mask(String encrypted) {
        String decrypted = decrypt(encrypted);
        return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
    }
}
