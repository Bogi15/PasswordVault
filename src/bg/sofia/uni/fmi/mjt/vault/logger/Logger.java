package bg.sofia.uni.fmi.mjt.vault.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private Logger() {

    }

    public static void log(String context) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("Timestamp: " + LocalDateTime.now().format(FORMATTER));
            writer.println("Context: " + context);
        } catch (IOException e) {
            System.err.println("Failed to write to log file.");
        }
    }

    public static void logError(String context, Throwable cause) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("Timestamp: " + LocalDateTime.now().format(FORMATTER));
            writer.println("Context: " + context);
            writer.println("Error: " + cause.getMessage());
            writer.println("Stack trace: ");
            cause.printStackTrace(writer);
            writer.println();
        } catch (IOException e) {
            System.err.println("Failed to write to log file.");
        }
    }
}
