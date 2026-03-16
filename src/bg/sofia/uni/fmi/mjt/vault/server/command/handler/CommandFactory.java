package bg.sofia.uni.fmi.mjt.vault.server.command.handler;

import bg.sofia.uni.fmi.mjt.vault.server.command.AddPasswordCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.GeneratePasswordCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.LoginCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.LogoutCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.RegisterCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.RemovePasswordCommand;
import bg.sofia.uni.fmi.mjt.vault.server.command.RetrieveCredentialsCommand;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.vault.util.CommandType;

import java.util.Set;

public class CommandFactory {

    public ServerCommand getCommand(CommandType type, UserRepository userRepository,  Set<String> loggedInUsers) {
        return switch (type) {
            case REGISTER -> new RegisterCommand(userRepository);
            case LOGIN -> new LoginCommand(userRepository, loggedInUsers);
            case LOGOUT -> new LogoutCommand(loggedInUsers);
            case RETRIEVE_CREDENTIALS -> new RetrieveCredentialsCommand(userRepository);
            case GENERATE_PASSWORD -> new GeneratePasswordCommand(userRepository);
            case ADD_PASSWORD -> new AddPasswordCommand(userRepository);
            case REMOVE_PASSWORD -> new RemovePasswordCommand(userRepository);
            case DISCONNECT ->  new DisconnectCommand(loggedInUsers);
            default -> null;
        };
    }
}
