package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisconnectCommandTest {

    private DisconnectCommand command;
    private ClientSession clientSession;
    private Set<String> loggedInUsers;

    private static final String DISCONNECT = "Disconnected";

    @BeforeEach
    void setUp() {
        loggedInUsers = ConcurrentHashMap.newKeySet();
        command = new DisconnectCommand(loggedInUsers);
        clientSession = new ClientSession();
    }

    @Test
    void testDisconnectCommandWhenUserIsNotLogged() {
        String[] args = {"disconnect"};

        String result = command.execute(args, clientSession);

        assertEquals(DISCONNECT, result,
                "Should return proper message for disconnecting");
    }

    @Test
    void testDisconnectCommandWhenUserLogged() {
        String[] args = {"disconnect"};
        clientSession.login("username", "password");
        loggedInUsers.add("username");
        assertTrue(clientSession.isAuthenticated());

        String result = command.execute(args, clientSession);

        assertEquals(DISCONNECT, result,
                "Should return proper message for disconnecting");
        assertFalse(clientSession.isAuthenticated());
        assertFalse(loggedInUsers.contains("username"));
    }

}