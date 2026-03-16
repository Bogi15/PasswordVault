package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.api.EnzoicClient;
import bg.sofia.uni.fmi.mjt.vault.api.EnzoicConfig;
import bg.sofia.uni.fmi.mjt.vault.api.EnzoicPasswordChecker;
import bg.sofia.uni.fmi.mjt.vault.crypto.AESCryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.CryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.exceptions.PasswordCheckerException;
import bg.sofia.uni.fmi.mjt.vault.server.command.handler.ServerCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

public class AddPasswordCommand implements ServerCommand {
    private final UserRepository userRepository;
    private final EnzoicPasswordChecker checker;
    private final CryptoService service;

    private static final int WEBSITE_INDEX = 1;
    private static final int USER_INDEX = 2;
    private static final int PASSWORD_INDEX = 3;

    private static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    private static final String REGISTRATION_ALREADY_EXISTS = "You already have registration for %s with %s.";
    private static final String PASSWORD_COMPROMISED = "Password %s is compromised," +
            " the registration for %s was not added.";
    private static final String ADDED_REGISTRATION = "Successfully added registration for %s";
    private static final String PASSWORD_CHECKER_EXCEPTION = "There seems to be a problem with this service";

    public AddPasswordCommand(UserRepository userRepository) {
        this(userRepository, createPasswordChecker() , new AESCryptoService());
    }

    AddPasswordCommand(UserRepository userRepository, EnzoicPasswordChecker checker, CryptoService service) {
        this.userRepository = userRepository;
        this.service = service;
        this.checker = checker;
    }

    private static EnzoicPasswordChecker createPasswordChecker() {
        EnzoicConfig config = new EnzoicConfig();
        EnzoicClient client = new EnzoicClient(config);
        return new EnzoicPasswordChecker(client);
    }

    @Override
    public String execute(String[] args, ClientSession clientSession) {
        String website = args[WEBSITE_INDEX];
        String user = args[USER_INDEX];
        String password = args[PASSWORD_INDEX];

        if (!clientSession.isAuthenticated()) {
            return NOT_LOGGED_IN;
        }

        User currentUser = userRepository.getUser(clientSession.getUsername());

        if (currentUser.registrationExists(website, user)) {
            return String.format(REGISTRATION_ALREADY_EXISTS, website, user);
        }

        try {
            boolean compromised = checker.isPasswordCompromised(password);
            if (compromised) {
                return String.format(PASSWORD_COMPROMISED, password, website);
            } else {
                EncryptedData data = service.encrypt(password, clientSession.getMasterPassword());
                Registration registration = Registration.of(website, user, data);
                currentUser.addRegistrationWebsite(registration);
                return String.format(ADDED_REGISTRATION, website);
            }
        } catch (PasswordCheckerException e) {
            return PASSWORD_CHECKER_EXCEPTION;
        }
    }
}
