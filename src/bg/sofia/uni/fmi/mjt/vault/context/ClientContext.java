package bg.sofia.uni.fmi.mjt.vault.context;

import bg.sofia.uni.fmi.mjt.vault.server.session.ClientSession;

import java.nio.ByteBuffer;

public class ClientContext {
    private static final int BUFFER_SIZE = 1024;

    private final ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private final ClientSession clientSession;

    public ClientContext() {
        this.readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.writeBuffer = null;
        this.clientSession = new ClientSession();
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

    public void setWriteBuffer(ByteBuffer buffer) {
        this.writeBuffer = buffer;
    }

    public ClientSession getClientSession() {
        return clientSession;
    }
}
