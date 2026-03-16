package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

public abstract class AbstractRegistrationCommand implements ServerCommand {
    protected final UserRepository userRepository;

    protected static final int WEBSITE_INDEX = 1;
    protected static final int USER_INDEX = 2;

    protected static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    protected static final String REGISTRATION_DOES_NOT_EXIST = "You do not have registration to %s with %s";

    protected AbstractRegistrationCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public final String execute(String[] args, ClientSession clientSession) {
        if (!clientSession.isAuthenticated()) {
            return NOT_LOGGED_IN;
        }

        String website = args[WEBSITE_INDEX];
        String username = args[USER_INDEX];

        User currentUser = userRepository.getUser(clientSession.getUsername());

        if (!currentUser.registrationExists(website, username)) {
            return String.format(REGISTRATION_DOES_NOT_EXIST, website, username);
        }

        return performAction(currentUser, website, username, clientSession);
    }

    protected abstract String performAction(User user, String website, String username, ClientSession session);
}
