package bg.sofia.uni.fmi.mjt.vault.api;

import bg.sofia.uni.fmi.mjt.vault.exceptions.PasswordCheckerException;

public interface PasswordLeakedChecker {
    boolean isPasswordCompromised(String rawPassword) throws PasswordCheckerException;
}
