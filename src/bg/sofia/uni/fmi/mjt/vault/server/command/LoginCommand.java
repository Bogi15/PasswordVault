package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

import java.util.Set;

public class LoginCommand implements ServerCommand {
    private final UserRepository userRepository;
    private final Set<String> loggedInUsers;

    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private static final String NO_SUCH_USER = "No such user.";
    private static final String INCORRECT_PASSWORD = "The provided password is incorrect.";
    private static final String YOU_ARE_ALREADY_LOGGED_IN = "You are already logged in as %s";
    private static final String ALREADY_LOGGED_IN = "User already logged in.";
    private static final String LOGGED_IN = "Successfully logged in as %s";

    public LoginCommand(UserRepository userRepository, Set<String> loggedInUsers) {
        this.userRepository = userRepository;
        this.loggedInUsers = loggedInUsers;
    }

    @Override
    public String execute(String[] args, ClientSession clientSession) {
        String username = args[USERNAME_INDEX];
        String password = args[PASSWORD_INDEX];

        User user = userRepository.getUser(username);

        if (clientSession.isAuthenticated()) {
            return String.format(YOU_ARE_ALREADY_LOGGED_IN, clientSession.getUsername());
        }

        if (user == null) {
            return NO_SUCH_USER;
        }

        if (!user.validPassword(password)) {
            return INCORRECT_PASSWORD;
        }

        if (loggedInUsers.contains(username)) {
            return ALREADY_LOGGED_IN;
        }

        loggedInUsers.add(username);
        clientSession.login(username, password);

        return String.format(LOGGED_IN, username);
    }
}
