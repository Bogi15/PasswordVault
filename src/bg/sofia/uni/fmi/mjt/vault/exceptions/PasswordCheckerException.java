package bg.sofia.uni.fmi.mjt.vault.exceptions;

public class PasswordCheckerException extends Exception {
    public PasswordCheckerException(String message) {
        super(message);
    }

    public PasswordCheckerException(String message, Throwable cause) {
        super(message, cause);
    }
}
