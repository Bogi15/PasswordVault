package bg.sofia.uni.fmi.mjt.vault.server.command;

import bg.sofia.uni.fmi.mjt.vault.crypto.AESCryptoService;
import bg.sofia.uni.fmi.mjt.vault.crypto.CryptoService;
import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.Registration;
import bg.sofia.uni.fmi.mjt.vault.server.user.model.User;
import bg.sofia.uni.fmi.mjt.vault.server.user.repository.UserRepository;

public class RetrieveCredentialsCommand extends AbstractRegistrationCommand {
    private final CryptoService cryptoService = new AESCryptoService();

    public RetrieveCredentialsCommand(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    protected String performAction(User user, String website, String username, ClientSession session) {
        Registration registration = user.getRegistrationWebsite(website, username);

        return cryptoService.decrypt(registration.data(), session.getMasterPassword()
        );
    }
}
