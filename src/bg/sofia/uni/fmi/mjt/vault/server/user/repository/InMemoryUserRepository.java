package bg.sofia.uni.fmi.mjt.vault.server.user.repository;

import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.util.Validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users;

    private static final String USER = "User";
    private static final String USERNAME = "Username";

    public InMemoryUserRepository(Map<String, User> loadedData) {
        users = new ConcurrentHashMap<>(loadedData);
    }

    @Override
    public void addUser(User user) {
        Validator.requiredNonNull(USER, user);

        users.put(user.getUsername(), user);
    }

    @Override
    public User getUser(String username) {
        Validator.validateString(USERNAME, username);

        return users.get(username);
    }

    @Override
    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public boolean userExists(String username) {
        Validator.validateString(USERNAME, username);

        return users.containsKey(username);
    }

    @Override
    public Map<String, User> getUsers() {
        return users;
    }
}
