package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.vault.util.CommandType;
import bg.sofia.uni.fmi.mjt.vault.util.ParsedCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


class RemovePasswordCommandTest {
    private UserRepository repository;
    private ClientSession clientSession;
    private RemovePasswordCommand command;


    private static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    private static final String REGISTRATION_DOES_NOT_EXIST = "You do not have registration to %s with %s";
    private static final String REGISTRATION_REMOVED = "Successfully removed your registration for %s with %s";

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        clientSession = new ClientSession();
        command = new RemovePasswordCommand(repository);
    }

    @Test
    void testRemovePasswordCommandWhenNotLogged() {
        String[] args = {"remove-pasword", "website", "user", "password"};

        String result = command.execute(args, clientSession);

        assertEquals(NOT_LOGGED_IN, result,
                "Should return proper message for trying to use command when not logged in");
    }

    @Test
    void testRemovePasswordWhenRegistrationNonExistent() {
        clientSession.login("username", "password");
        User currentUser = new User("username", "password");
        when(repository.getUser("username")).thenReturn(currentUser);

        String[] args = {"remove-password", "website", "user"};

        String result = command.execute(args, clientSession);

        assertEquals(String.format(REGISTRATION_DOES_NOT_EXIST, "website", "user"), result,
                "Should return proper message when trying to remove non-existent registration");
        assertFalse(currentUser.registrationExists("website", "user"));
    }

    @Test
    void testRemoveRegistrationWhenValid() {
        clientSession.login("username", "password");

        User currentUser = new User("username", "password");
        EncryptedData data = Mockito.mock(EncryptedData.class);
        currentUser.addRegistrationWebsite(new Registration("website", "user", data));

        when(repository.getUser("username")).thenReturn(currentUser);
        assertTrue(currentUser.registrationExists("website", "user"));

        String[] args = {"remove-password", "website", "user"};
        ParsedCommand parsedCommand = new ParsedCommand(CommandType.REMOVE_PASSWORD, args);

        String result = command.execute(args, clientSession);

        assertEquals(String.format(String.format(REGISTRATION_REMOVED,"website", "user"), "website", "user"), result,
                "Should return proper message for removing registration");
        assertFalse(currentUser.registrationExists("website", "user"));
    }

}