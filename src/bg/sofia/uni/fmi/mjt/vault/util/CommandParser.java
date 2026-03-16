package bg.sofia.uni.fmi.mjt.vault.util;

public class CommandParser {

    public static final String REGEX = "\\s+";

    public static ParsedCommand parse(String input) {

        String[] tokens = input.trim().split(REGEX);

        if (tokens.length == 0) {
            return new ParsedCommand(CommandType.UNKNOWN, tokens);
        }

        CommandType type = CommandType.getType(tokens[0]);

        return new ParsedCommand(type, tokens);
    }

}
