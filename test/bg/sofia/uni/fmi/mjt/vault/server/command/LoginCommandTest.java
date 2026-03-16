package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LoginCommandTest {

    private UserRepository repository;
    private ClientSession clientSession;
    private Set<String> loggedInUsers;
    private LoginCommand command;

    private static final String NO_SUCH_USER = "No such user.";
    private static final String INCORRECT_PASSWORD = "The provided password is incorrect.";
    private static final String ALREADY_LOGGED_IN = "User already logged in.";
    private static final String LOGGED_IN = "Successfully logged in as %s";


    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        loggedInUsers = ConcurrentHashMap.newKeySet();
        clientSession = new ClientSession();
        command = new LoginCommand(repository, loggedInUsers);
    }

    @Test
    void testLoginCommandWhenSuchUsernameDoesNotExist() {
        when(repository.getUser(anyString())).thenReturn(null);
        String[] args = {"login", "username", "password"};

        String result = command.execute(args, clientSession);

        assertEquals(NO_SUCH_USER, result,
                "Should return proper message for non-existing username in the repository.");

    }

    @Test
    void testLoginCommandWhenPasswordIsNotValid() {
        User user = new User("username", "password");
        when(repository.getUser("username")).thenReturn(user);

        String[] args = {"login", "username", "wrongPassword"};

        String result = command.execute(args, clientSession);

        assertEquals(INCORRECT_PASSWORD, result,
                "Should return proper message for incorrect password");
    }

    @Test
    void testLoginCommandWhenHeIsAlreadyLoggedInOtherSession() {
        loggedInUsers.add("username");
        User user = new User("username", "password");

        when(repository.getUser("username")).thenReturn(user);

        String[] args = {"login", "username", "password"};

        String result = command.execute(args, clientSession);

        assertEquals(ALREADY_LOGGED_IN, result,
                "Should return proper message when trying to login into account which is in session");
    }

    @Test
    void testLoginCommandWhenInputIsValid() {
        User user = new User("username", "password");

        when(repository.getUser("username")).thenReturn(user);

        String[] args = {"login", "username", "password"};
        String result = command.execute(args, clientSession);

        assertEquals(String.format(LOGGED_IN, "username"), result,
                "Should give proper message for logging in");
        assertTrue(loggedInUsers.contains("username"), "The username should be saved in the loggedUsers");
        assertTrue(clientSession.isAuthenticated());
    }

}


