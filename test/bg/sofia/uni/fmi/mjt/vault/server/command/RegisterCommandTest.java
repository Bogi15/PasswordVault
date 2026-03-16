package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterCommandTest {

    private UserRepository repository;
    private ClientSession clientSession;
    private RegisterCommand command;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        clientSession = new ClientSession();
        command = new RegisterCommand(repository);
    }

    private static final String ALREADY_LOGGED_IN = "You are logged in. Logout to register a new account";
    private static final String USERNAME_ALREADY_EXISTS = "This username is already taken by someone.";
    private static final String REGISTERED = "Successfully registered.";

    @Test
    void testRegisterCommandWhenAlreadyLogged() {
        clientSession.login("username", "password");

        String[] args = {"register", "username", "password", "repeatedPassword"};

        String result = command.execute(args, clientSession);

        assertEquals(ALREADY_LOGGED_IN, result,
                "Should return proper message for trying to register when logged in");
        verify(repository, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterCommandWhenUserWithSuchUsernameAlreadyExists() {
        User alreadyRegisteredUser = new User("username", "password");
        when(repository.getUser("username")).thenReturn(alreadyRegisteredUser);

        String[] args = {"register", "username", "password", "repeatedPassword"};

        String result = command.execute(args, clientSession);

        assertEquals(USERNAME_ALREADY_EXISTS, result,
                "Should return proper message for already taken username");
        verify(repository, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterCommandWhenValid() {
        when(repository.getUser("username")).thenReturn(null);

        String[] args = {"register", "username", "password", "repeatedPassword"};

        String result = command.execute(args, clientSession);

        assertEquals(REGISTERED, result,
                "Should return proper message for successful registration");
        verify(repository, times(1)).addUser(any(User.class));
    }

}