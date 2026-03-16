package bg.sofia.uni.fmi.mjt.vault.util;

public class Validator {

    public static <T> void requiredNonNull(String objectName, T object) {
        if (object == null) {
            throw new IllegalArgumentException(objectName + " cannot be null.");
        }
    }

    public static void validateString(String objectName, String str) {
        if (str == null) {
            throw new IllegalArgumentException(objectName + " cannot be null.");
        }
        if (str.isBlank()) {
            throw  new IllegalArgumentException(objectName + "cannot be blank.");
        }
    }
}
