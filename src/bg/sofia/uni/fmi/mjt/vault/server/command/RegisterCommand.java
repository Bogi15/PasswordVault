package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

public class RegisterCommand implements ServerCommand {
    private final UserRepository userRepository;
    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private static final String ALREADY_LOGGED_IN = "You are logged in. Logout to register a new account";
    private static final String USERNAME_ALREADY_EXISTS = "This username is already taken by someone.";
    private static final String REGISTERED = "Successfully registered.";

    public RegisterCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String execute(String[] args, ClientSession clientSession) {
        String username = args[USERNAME_INDEX];
        String password = args[PASSWORD_INDEX];

        if (clientSession.isAuthenticated()) {
            return ALREADY_LOGGED_IN;
        }

        User user = userRepository.getUser(username);

        if (user != null) {
            return USERNAME_ALREADY_EXISTS;
        }

        User toAdd = new User(username, password);
        userRepository.addUser(toAdd);
        return REGISTERED;
    }
}
