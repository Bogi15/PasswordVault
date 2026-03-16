package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeneratePasswordCommandTest {
    private UserRepository repository;
    private ClientSession clientSession;
    private GeneratePasswordCommand command;

    private static final String NOT_LOGGED_IN = "You need to be logged in to use this command.";
    private static final String REGISTRATION_ALREADY_EXISTS = "You already have registration for %s with %s";

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        clientSession = new ClientSession();
        command = new GeneratePasswordCommand(repository);
    }

    @Test
    void testGeneratePasswordWhenNotLogged() {
        String[] args = {"generate-password", "instagram.com", "user"};
        String result = command.execute(args, clientSession);

        assertEquals(NOT_LOGGED_IN, result, "Should return proper message when not logged in");
        verify(repository, never()).getUser(anyString());
    }


    @Test
    void testGeneratePasswordWhenRegistrationAlreadyExists() {
        clientSession.login("username", "password");

        User currentUser = new User("username", "password");

        EncryptedData existingData = Mockito.mock(EncryptedData.class);
        Registration existingReg = new Registration("website", "user", existingData);
        currentUser.addRegistrationWebsite(existingReg);
        assertTrue(currentUser.registrationExists("website", "user"), "Already registered");

        when(repository.getUser("username")).thenReturn(currentUser);

        String[] args = {"generate-password", "website", "user"};

        String result = command.execute(args, clientSession);

        assertEquals(String.format(REGISTRATION_ALREADY_EXISTS, "website", "user"), result,
                "Should return proper message when registration alreday exists");
    }

    @Test
    void testGeneratePasswordWhenValidReturnsGeneratedPassword() {
        clientSession.login("username", "password");

        User currentUser = new User("username", "password");
        when(repository.getUser("username")).thenReturn(currentUser);
        assertFalse(currentUser.registrationExists("website","user"), "No registration for now");

        String[] args = {"generate-password", "website", "user"};

        String generatedPassword = command.execute(args, clientSession);

        assertNotNull(generatedPassword, "Should return the generated password");
        assertTrue(currentUser.registrationExists("website","user"), "Should have a registration now");
    }
}