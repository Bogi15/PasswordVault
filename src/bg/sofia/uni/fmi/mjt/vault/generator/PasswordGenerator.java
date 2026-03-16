package bg.sofia.uni.fmi.mjt.vault.generator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private static final int PASSWORD_LENGTH = 22;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+<>?{}[]|:;,.~";

    private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {

    }

    public static String generate() {
        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(randomChar(UPPER));
        passwordChars.add(randomChar(LOWER));
        passwordChars.add(randomChar(DIGITS));
        passwordChars.add(randomChar(SYMBOLS));

        int required = passwordChars.size();

        for (int i = required; i < PASSWORD_LENGTH; i++) {
            passwordChars.add(randomChar(ALL));
        }

        Collections.shuffle(passwordChars, RANDOM);

        StringBuilder result = new StringBuilder(PASSWORD_LENGTH);
        for (char c : passwordChars) {
            result.append(c);
        }

        return result.toString();
    }

    private static char randomChar(String source) {
        return source.charAt(RANDOM.nextInt(source.length()));
    }
}
