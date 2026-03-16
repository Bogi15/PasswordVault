package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;

import java.util.Set;

public class DisconnectCommand implements ServerCommand {
    private final Set<String> loggedInUsers;

    private static final String DISCONNECT = "Disconnected";

    public DisconnectCommand(Set<String> loggedInUsers) {
        this.loggedInUsers = loggedInUsers;
    }

    @Override
    public String execute(String[] args, ClientSession clientSession) {
        if (clientSession.isAuthenticated()) {
            loggedInUsers.remove(clientSession.getUsername());
            clientSession.logout();
        }

        return DISCONNECT;
    }
}
