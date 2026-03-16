package bg.sofia.uni.fmi.mjt.vault.util;

public class CommandValidator {

    private static final int ONLY_COMMAND = 1;

    private static final int PASSWORD_INDEX = 2;
    private static final int REPEATED_PASSWORD_INDEX = 3;

    public static String validate(ParsedCommand cmd) {
        String[] args = cmd.getArgs();
        try {
            switch (cmd.getType()) {
                case REGISTER -> validateRegister(args);
                case LOGIN -> validateLogin(args);
                case RETRIEVE_CREDENTIALS -> validateRetrieve(args);
                case GENERATE_PASSWORD -> validateGenerate(args);
                case ADD_PASSWORD -> validateAdd(args);
                case REMOVE_PASSWORD -> validateRemove(args);
                case LOGOUT, DISCONNECT -> validateNoArgs(args);
                case UNKNOWN -> throw new IllegalArgumentException("Unknown command.");
                default -> { }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static void validateRegister(String[] args) {
        if (args.length != CommandType.REGISTER.getSize()) {
            throw new IllegalArgumentException("Usage: register <user> <password> <password-repeat>");
        }
        if (!args[PASSWORD_INDEX].equals(args[REPEATED_PASSWORD_INDEX])) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }

    private static void validateLogin(String[] args) {
        if (args.length != CommandType.LOGIN.getSize()) {
            throw new IllegalArgumentException("Usage: login <user> <password>");
        }
    }

    private static void validateRetrieve(String[] args) {
        if (args.length != CommandType.RETRIEVE_CREDENTIALS.getSize()) {
            throw new IllegalArgumentException("Usage: retrieve-credentials <website> <user>");
        }
    }

    private static void validateGenerate(String[] args) {
        if (args.length != CommandType.GENERATE_PASSWORD.getSize()) {
            throw new IllegalArgumentException("Usage: generate-password <website> <user>");
        }
    }

    private static void validateAdd(String[] args) {
        if (args.length != CommandType.ADD_PASSWORD.getSize()) {
            throw new IllegalArgumentException("Usage: add-password <website> <user> <password>");
        }
    }

    private static void validateRemove(String[] args) {
        if (args.length != CommandType.REMOVE_PASSWORD.getSize()) {
            throw new IllegalArgumentException("Usage: remove-password <website> <user>");
        }
    }

    private static void validateNoArgs(String[] args) {
        if (args.length != ONLY_COMMAND) {
            throw new IllegalArgumentException("Invalid usage.");
        }
    }
}
