package bg.sofia.uni.fmi.mjt.vault.server.command.handler;

import bg.sofia.uni.fmi.mjt.vault.util.ParsedCommand;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

import java.util.Set;

public class CommandExecutor {

    private final UserRepository userRepository;
    private final CommandFactory commandFactory;

    public CommandExecutor(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.commandFactory = new CommandFactory();
    }

    public String execute(ParsedCommand parsedCommand, ClientSession clientSession, Set<String> loggedInUsers) {

        ServerCommand command = commandFactory.getCommand(parsedCommand.getType(), userRepository, loggedInUsers);

        return command.execute(parsedCommand.getArgs(), clientSession);
    }
}
