package bg.sofia.uni.fmi.mjt.vault.server.command.handler;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;

public interface ServerCommand {
    String execute(String[] args, ClientSession clientSession);
}
