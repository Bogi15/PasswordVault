package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.crypto.AESCryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.CryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.generator.PasswordGenerator;
import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

public class GeneratePasswordCommand implements ServerCommand {
    private final UserRepository userRepository;
    private final CryptoService service = new AESCryptoService();

    private static final int WEBSITE_INDEX = 1;
    private static final int USER_INDEX = 2;

    private static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    private static final String REGISTRATION_ALREADY_EXISTS = "You already have registration for %s with %s";

    public GeneratePasswordCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String execute(String[] args, ClientSession clientSession) {
        String website = args[WEBSITE_INDEX];
        String user = args[USER_INDEX];

        if (!clientSession.isAuthenticated()) {
            return NOT_LOGGED_IN;
        }

        User currentUser = userRepository.getUser(clientSession.getUsername());

        if (currentUser.registrationExists(website, user)) {
            return String.format(REGISTRATION_ALREADY_EXISTS, website, user);
        }

        String rawPassword = PasswordGenerator.generate();
        EncryptedData data = service.encrypt(rawPassword, clientSession.getMasterPassword());
        Registration registration = Registration.of(website, user, data);

        currentUser.addRegistrationWebsite(registration);
        return rawPassword;
    }
}
