package bg.sofia.uni.fmi.mjt.vault.server.user.model;

import bg.sofia.uni.fmi.mjt.vault.util.Hash;
import bg.sofia.uni.fmi.mjt.vault.util.Validator;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final String test;
    private final Map<String, Collection<Registration>> registrations;

    private static final String REGISTRATION = "Registration";
    private static final String USER = "User";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String WEBSITE = "Website";

    public User(String username, String password) {
        Validator.validateString(USERNAME, username);
        Validator.validateString(PASSWORD, password);

        this.username = username;
        this.password = Hash.sha256(password);
        this.registrations = new HashMap<>();
        this.test = "";
    }

    public String getUsername() {
        return username;
    }

    public boolean validPassword(String rawPassword) {
        return password.equals(Hash.sha256(rawPassword));
    }

    public void addRegistrationWebsite(Registration registration) {
        Validator.requiredNonNull(REGISTRATION, registration);

        registrations.putIfAbsent(registration.website(), new HashSet<>());
        registrations.get(registration.website()).add(registration);
    }

    public Registration getRegistrationWebsite(String website, String user) {
        Validator.validateString(WEBSITE, website);
        Validator.validateString(USER, user);

        Collection<Registration> registrationList = registrations.get(website);

        if (registrationList == null) {
            return null;
        }

        return registrationList.stream()
                .filter(r -> r.user().equals(user))
                .findFirst()
                .orElse(null);
    }

    public void removeRegistrationWebsite(String website, String user) {
        Validator.validateString(WEBSITE, website);
        Validator.validateString(USER, user);

        Collection<Registration> registrationList = registrations.get(website);

        if (registrationList == null) {
            return;
        }

        registrationList.removeIf(r -> r.website().equals(website) && r.user().equals(user));
    }

    public boolean registrationExists(String website, String user) {
        Validator.validateString(WEBSITE, website);
        Validator.validateString(USER, user);

        Collection<Registration> registrationList = registrations.get(website);

        if (registrationList == null) {
            return false;
        }

        return registrationList.stream().anyMatch(r -> r.user().equals(user));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof User user)) {
            return false;
        }

        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(registrations, user.registrations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, registrations);
    }

}
