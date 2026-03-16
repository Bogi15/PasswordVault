package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.api.EnzoicPasswordChecker;
import bg.sofia.uni.fmi.mjt.vault.crypto.AESCryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.CryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.exceptions.PasswordCheckerException;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AddPasswordCommandTest {
    private UserRepository repository;
    private ClientSession clientSession;
    private EnzoicPasswordChecker checker;
    private AddPasswordCommand command;
    private CryptoService service;

    private static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    private static final String REGISTRATION_ALREADY_EXISTS = "You already have registration for %s with %s.";
    private static final String PASSWORD_COMPROMISED = "Password %s is compromised," +
            " the registration for %s was not added.";
    private static final String ADDED_REGISTRATION = "Successfully added registration for %s";
    private static final String PASSWORD_CHECKER_EXCEPTION = "There seems to be a problem with this service";


    @BeforeEach
    void setUp() {
        clientSession = new ClientSession();
        repository = Mockito.mock(UserRepository.class);
        checker = Mockito.mock(EnzoicPasswordChecker.class);
        service = new AESCryptoService();
        command = new AddPasswordCommand(repository, checker, service);
    }

    @Test
    void testAddPasswordCommandWhenNotLoggedIn() {
        String[] args = {"add-password", "website", "user", "password"};

        String result = command.execute(args, clientSession);

        assertEquals(NOT_LOGGED_IN, result,
                "Should return proper message when user is not logged in");
        verify(repository, never()).getUser(anyString());
    }

    @Test
    void testAddPasswordCommandWhenRegistrationExists() {
        clientSession.login("username", "password");

        User currentUser = new User("username", "password");
        EncryptedData data = Mockito.mock(EncryptedData.class);
        currentUser.addRegistrationWebsite(new Registration("website", "user", data));

        when(repository.getUser("username")).thenReturn(currentUser);

        String[] args = {"add-password", "website", "user", "randomPassword"};

        String result = command.execute(args, clientSession);

        assertEquals(String.format(REGISTRATION_ALREADY_EXISTS, "website", "user"), result,
                "Should return proper message when registartion exists" );
    }

    @Test
    void testAddPasswordCommandValid() throws PasswordCheckerException {
        clientSession.login("username", "password");

        User currentUser  = new User("username", "password");
        when(repository.getUser("username")).thenReturn(currentUser);

        when(checker.isPasswordCompromised("weakPassword")).thenReturn(false);

        String[] args = {"add-password", "website", "user", "weakPassword"};

        String result = command.execute(args, clientSession);
        assertEquals(String.format(ADDED_REGISTRATION, "website"), result,
                "Should return proper message when adding registration");
    }
}