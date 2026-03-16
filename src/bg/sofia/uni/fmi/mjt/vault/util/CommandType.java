package bg.sofia.uni.fmi.mjt.vault.util;

import java.util.Arrays;

public enum CommandType {
    REGISTER("register", 4, "register <user> <password> <password-repeat>"),
    LOGIN("login", 3, "login <user> <password>"),
    LOGOUT("logout", 1, "logout"),
    RETRIEVE_CREDENTIALS("retrieve-credentials", 3, "retrieve-credentials <website> <user>"),
    GENERATE_PASSWORD("generate-password", 3, "generate-password <website> <user>"),
    ADD_PASSWORD("add-password", 4, "add-password <website> <user> <password>"),
    REMOVE_PASSWORD("remove-password", 3, "remove-password <website> <user>"),
    DISCONNECT("disconnect", 1, ""),
    UNKNOWN("", 0, "");

    private final String commandName;
    private final int size;
    private final String usage;

    CommandType(String commandName, int size, String usage) {
        this.commandName = commandName;
        this.size = size;
        this.usage = usage;
    }

    public String getCommandName() {
        return  commandName;
    }

    public int getSize() {
        return size;
    }

    public String getUsage() {
        return usage;
    }

    public static CommandType getType(String command) {
        return Arrays.stream(values())
                .filter(cmd -> cmd.getCommandName().equals(command))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
