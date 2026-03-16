package bg.sofia.uni.fmi.mjt.vault.server;

import bg.sofia.uni.fmi.mjt.vault.logger.Logger;
import bg.sofia.uni.fmi.mjt.vault.util.CommandParser;
import bg.sofia.uni.fmi.mjt.vault.util.ParsedCommand;
import bg.sofia.uni.fmi.mjt.vault.context.ClientContext;
import bg.sofia.uni.fmi.mjt.vault.server.command.handler.CommandExecutor;
import bg.sofia.uni.fmi.mjt.vault.server.user.UserStorage;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordVaultServer {

    private static final int PORT = 4444;
    private static final String HOST = "localhost";

    private final UserStorage userStorage;
    private final UserRepository userRepository;
    private final CommandExecutor commandExecutor;
    private final Set<String> loggedInUsers;

    private boolean isServerWorking;
    private Selector selector;

    private static final int THREAD_POOL_SIZE = 15;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public PasswordVaultServer() {
        this.userStorage = new UserStorage();
        Map<String, User> loaded = userStorage.load();
        this.userRepository = new InMemoryUserRepository(loaded);
        this.commandExecutor = new CommandExecutor(userRepository);
        this.loggedInUsers = ConcurrentHashMap.newKeySet();
    }

    public void start() throws IOException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            this.selector = selector;
            configureServerSocketChannel(serverSocketChannel, selector);

            isServerWorking = true;
            runEventLoop(selector);

        } catch (IOException e) {
            Logger.logError("Failed to start server", e);
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    private void runEventLoop(Selector selector) throws IOException {
        while (isServerWorking) {
            if (selector.select() == 0) {
                continue;
            }

            processSelectedKeys(selector.selectedKeys());
        }
    }

    private void processSelectedKeys(Set<SelectionKey> selectionKeys) throws IOException {
        Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            keyIterator.remove();

            dispatchKey(key);
        }
    }

    private void dispatchKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            accept(selector, key);
        } else if (key.isReadable()) {
            handleRead(key);
        } else if (key.isWritable()) {
            handleWrite(key);
        }
    }

    public void stop() {
        userStorage.save(userRepository.getUsers());
        isServerWorking = false;
        if (selector != null && selector.isOpen()) {
            selector.wakeup();
        }
        EXECUTOR_SERVICE.shutdown();
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel == null) {
            return;
        }

        clientChannel.configureBlocking(false);

        ClientContext clientContext = new ClientContext();
        clientChannel.register(selector, SelectionKey.OP_READ, clientContext);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientContext clientContext = (ClientContext) key.attachment();
        ByteBuffer buffer = clientContext.getReadBuffer();

        buffer.clear();
        int readBytes = clientChannel.read(buffer);

        if (readBytes < 0) {
            clientChannel.close();
            key.cancel();
            return;
        }

        if (readBytes == 0) {
            return;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        String clientInput = new String(clientInputBytes, StandardCharsets.UTF_8);
        submitClientInput(clientContext, clientInput, key);
    }

    private void submitClientInput(ClientContext clientContext, String clientInput, SelectionKey key) {
        EXECUTOR_SERVICE.submit(() -> {
            ParsedCommand parsedCommand = CommandParser.parse(clientInput);
            String output = commandExecutor.execute(parsedCommand, clientContext.getClientSession(), loggedInUsers);

            clientContext.setWriteBuffer(ByteBuffer.wrap(
                    (output + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));

            key.interestOps(SelectionKey.OP_WRITE);
            key.selector().wakeup();
        });
    }

    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientContext clientContext = (ClientContext) key.attachment();
        ByteBuffer buffer = clientContext.getWriteBuffer();

        if (buffer == null) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        }

        clientChannel.write(buffer);

        if (!buffer.hasRemaining()) {
            clientContext.setWriteBuffer(null);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) {
        PasswordVaultServer server = new PasswordVaultServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(System.lineSeparator() + "Shutting down server");
            Logger.log("Shutting down server" + System.lineSeparator());
            server.stop();
            System.out.println("Server stopped.");
        }));

        try {
            System.out.print("Starting server..." + System.lineSeparator());
            Logger.log("Starting server");
            server.start();
        } catch (IOException e) {
            System.out.println("Failed to start the server");
        }
    }
}