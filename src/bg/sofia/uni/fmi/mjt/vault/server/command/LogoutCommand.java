package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;

import java.util.Set;

public class LogoutCommand implements ServerCommand {
    private final Set<String> loggedInUsers;

    private static final String ALREADY_LOGGED_OUT = "You are already logged out.";
    private static final String LOGGED_OUT = "Successfully logged out of %s account";

    public LogoutCommand(Set<String> loggedInUsers) {
        this.loggedInUsers = loggedInUsers;
    }

    @Override
    public String execute(String[] args, ClientSession clientSession) {
        if (!clientSession.isAuthenticated()) {
            return ALREADY_LOGGED_OUT;
        }

        String username = clientSession.getUsername();
        loggedInUsers.remove(clientSession.getUsername());
        clientSession.logout();
        return String.format(LOGGED_OUT, username);
    }
}
