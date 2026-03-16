package bg.sofia.uni.fmi.mjt.vault.server.user.repository;

import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserRepository {
    void addUser(User user);

    User getUser(String username);

    Collection<User> getAllUsers();

    Map<String, User> getUsers();

    boolean userExists(String username);
}
