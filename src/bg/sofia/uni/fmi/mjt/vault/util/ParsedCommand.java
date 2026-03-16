package bg.sofia.uni.fmi.mjt.vault.util;

public class ParsedCommand {
    private CommandType type;
    private String[] args;

    public ParsedCommand(CommandType type, String[] args) {
        this.type = type;
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public CommandType getType() {
        return type;
    }
}
