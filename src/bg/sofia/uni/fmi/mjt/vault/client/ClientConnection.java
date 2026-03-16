package bg.sofia.uni.fmi.mjt.vault.client;

import bg.sofia.uni.fmi.mjt.vault.logger.Logger;
import bg.sofia.uni.fmi.mjt.vault.util.CommandParser;
import bg.sofia.uni.fmi.mjt.vault.util.CommandType;
import bg.sofia.uni.fmi.mjt.vault.util.CommandValidator;
import bg.sofia.uni.fmi.mjt.vault.util.ParsedCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientConnection {
    private static final int SERVER_PORT = 4444;
    private static final String SERVER_HOST = "localhost";

    public static void main() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader in = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in);) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                System.out.print("Enter command: ");
                String input = scanner.nextLine();

                ParsedCommand parsed = CommandParser.parse(input);
                String validationError = CommandValidator.validate(parsed);

                if (validationError != null) {
                    System.out.println(validationError);
                    continue;
                }
                out.println(input);
                System.out.println(in.readLine());

                if (parsed.getType() == CommandType.DISCONNECT) {
                    break;
                }
            }
        } catch (IOException e) {
            Logger.logError("Network error: ", e);
            System.out.println("Network error: " + e.getMessage());
        }
    }
}
