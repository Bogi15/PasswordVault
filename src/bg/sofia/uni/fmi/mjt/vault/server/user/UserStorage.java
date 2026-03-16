package bg.sofia.uni.fmi.mjt.vault.server.user;

import bg.sofia.uni.fmi.mjt.vault.logger.Logger;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStorage {
    private static final String FOLDER_NAME = "storage";
    private static final String FILE_NAME = "users.dat";

    private Path getStoragePath() {
        return Path.of(FOLDER_NAME, FILE_NAME);
    }

    public synchronized Map<String, User> load() {
        Map<String, User> result = new ConcurrentHashMap<>();
        Path path = getStoragePath();

        if (!Files.exists(getStoragePath())) {
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(
               new BufferedInputStream(Files.newInputStream(path)))) {
            while (true) {
                User user = (User) inputStream.readObject();
                result.put(user.getUsername(), user);
            }
        } catch (EOFException e) {
            return result;
        } catch (IOException | ClassNotFoundException e) {
            Logger.logError("Failed to load users", e);
            throw new RuntimeException("Failed to load users", e);
        }
    }

    public synchronized void save(Map<String, User> users) {
        Path path = getStoragePath();
        createFile();

        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(path)))) {

            for (User user : users.values()) {
                outputStream.writeObject(user);
            }

        } catch (IOException e) {
            Logger.logError("Failed to save users", e);
            throw new RuntimeException("Failed to save users", e);
        }
    }

    private void createFolder() {
        Path path = Path.of(FOLDER_NAME);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            Logger.logError("Failed to create folder", e);
            throw new RuntimeException("Failed to create folder", e);
        }
    }

    private void createFile() {
        createFolder();
        Path path = getStoragePath();

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            Logger.logError("Failed to create file", e);
            throw new RuntimeException("Failed to create file", e);
        }
    }

}
