package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

public class RemovePasswordCommand extends AbstractRegistrationCommand {

    private static final String REGISTRATION_REMOVED = "Successfully removed your registration for %s with %s";

    public RemovePasswordCommand(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    protected String performAction(User user, String website, String username, ClientSession session) {
        user.removeRegistrationWebsite(website, username);

        return String.format(REGISTRATION_REMOVED, website, username);
    }
}
