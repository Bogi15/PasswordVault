package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


class LogoutCommandTest {
    private ClientSession clientSession;
    private Set<String> loggedInUsers;
    private LogoutCommand command;

    private static final String ALREADY_LOGGED_OUT = "You are already logged out.";
    private static final String LOGGED_OUT = "Successfully logged out of %s account";

    @BeforeEach
    void setUp() {
        clientSession = new ClientSession();
        loggedInUsers = ConcurrentHashMap.newKeySet();
        command = new LogoutCommand(loggedInUsers);
    }

    @Test
    void testLogoutCommandWhenNotLogged() {
        String[] args = {"logout"};

        String result = command.execute(args, clientSession);

        assertEquals(ALREADY_LOGGED_OUT, result,
                "Should return proper message when user is not logged in");

    }

    @Test
    void testLogoutCommandWhenLoggedIn() {
        clientSession.login("username", "password");
        loggedInUsers.add("username");

        String[] args = {"logout"};

        String result = command.execute(args, clientSession);

        assertEquals(String.format(LOGGED_OUT, "username"), result,
                "Should retunr proper message when logging out succesfully");

    }
}