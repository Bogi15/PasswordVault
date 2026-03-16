package bg.sofia.uni.fmi.mjt.vault.server.user.repository;

import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository(new HashMap<>());
    }

    @Test
    void testAddUser() {
        User user = new User("john", "pass123");

        repository.addUser(user);

        assertTrue(repository.userExists("john"),
                "Adding user");
    }

    @Test
    void testAddUserThrowsWhenNull() {
        assertThrows(Exception.class, () -> repository.addUser(null),
                "Should throw exception when user is null");
    }

    @Test
    void testGetUserReturnsCorrectUser() {
        User user = new User("alice", "secret");
        repository.addUser(user);

        User retrieved = repository.getUser("alice");

        assertNotNull(retrieved);
        assertEquals("alice", retrieved.getUsername(),
                "Checking if we have added such user");
    }

    @Test
    void testGetUserReturnsNullWhenNotExists() {
        User retrieved = repository.getUser("nonexistent");

        assertNull(retrieved,
                "No users were added should return null");
    }

    @Test
    void testGetUserThrowsWhenUsernameNull() {
        assertThrows(Exception.class, () -> repository.getUser(null),
                "Should throw exception when tring to get user with null");
    }

    @Test
    void testGetUserThrowsWhenUsernameEmpty() {
        assertThrows(Exception.class, () -> repository.getUser(""),
                "Should thow except4ion when tring to get user with blank string");
    }

    @Test
    void testGetAllUsersReturnsAllUsers() {
        repository.addUser(new User("user1", "pass1"));
        repository.addUser(new User("user2", "pass2"));
        repository.addUser(new User("user3", "pass3"));

        Collection<User> users = repository.getAllUsers();

        assertEquals(3, users.size(),
                "We should have 3 users in the repository");
    }

    @Test
    void testUserExistsReturnsTrue() {
        repository.addUser(new User("bob", "password"));

        assertTrue(repository.userExists("bob"),
                "After adding user we should be able to retrieve it with his username");
    }

    @Test
    void testUserExistsReturnsFalse() {
        assertFalse(repository.userExists("nobody"),
                "When we have non-existent username we should return false");
    }

    @Test
    void testUserExistsThrowsWhenUsernameNull() {
        assertThrows(Exception.class, () -> repository.userExists(null),
                "Throws exception when trying to check if user exists with null");
    }

    @Test
    void testUserExistsThrowsWhenUsernameEmpty() {
        assertThrows(Exception.class, () -> repository.userExists(""),
                "Throws exception when trying to check if user exists with blank string");
    }

    @Test
    void testGetUsersReturnsMap() {
        repository.addUser(new User("user", "pass"));

        Map<String, User> users = repository.getUsers();

        assertEquals(1, users.size(), "Should have oine user in repository");
        assertTrue(users.containsKey("user"), "");
    }

    @Test
    void testConstructorWithPreloadedData() {
        Map<String, User> initial = new HashMap<>();
        initial.put("user", new User("user", "password"));

        InMemoryUserRepository repo = new InMemoryUserRepository(initial);

        assertTrue(repo.userExists("user"),
                "User should exist in the repository");
        assertEquals(1, repo.getAllUsers().size(),
                "We have constructed inMemoryRepository with map with one user");
    }

}