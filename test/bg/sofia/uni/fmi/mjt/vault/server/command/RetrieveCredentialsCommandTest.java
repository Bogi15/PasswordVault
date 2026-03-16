package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.crypto.AESCryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.CryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RetrieveCredentialsCommandTest {
    private UserRepository repository;
    private ClientSession clientSession;
    private CryptoService cryptoService;
    private RetrieveCredentialsCommand command;

    private static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    private static final String REGISTRATION_DOES_NOT_EXIST = "You do not have registration to %s with %s";

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        clientSession = new ClientSession();
        cryptoService = new AESCryptoService();
        command = new RetrieveCredentialsCommand(repository);
    }

    @Test
    void testRetrieveCredentialsCommandWhenNotLoggedIn() {
        String[] args = {"retrieve-credentials", "website", "user"};

        String result = command.execute(args, clientSession);

        assertEquals(NOT_LOGGED_IN, result,
                "Should return proper message when user is not logged in");
    }

    @Test
    void testRetrieveCredentialsCommandWhenRegistrationDoesNotExist() {
        clientSession.login("username", "password");

        User currentUser = new User("username", "password");
        when(repository.getUser("username")).thenReturn(currentUser);

        String[] args = {"retrieve-credentials", "website", "user"};

        String result = command.execute(args, clientSession);

        assertEquals(String.format(REGISTRATION_DOES_NOT_EXIST, "website", "user"), result,
                "Should return proper message when registration does not exist");
    }

    @Test
    void testRetrieveCredentialsCommandWhenValid() {
        clientSession.login("username", "password");

        User currentUser = new User("username", "password");

        String originalPassword = "myPassword";
        EncryptedData encryptedData = cryptoService.encrypt(originalPassword, clientSession.getMasterPassword());
        Registration registration = new Registration("website", "user", encryptedData);
        currentUser.addRegistrationWebsite(registration);

        when(repository.getUser("username")).thenReturn(currentUser);

        String[] args = {"retrieve-credentials", "website", "user"};

        String result = command.execute(args, clientSession);

        assertEquals(originalPassword, result,
                "Should return decrypted password when retrieving credentials");
    }
}