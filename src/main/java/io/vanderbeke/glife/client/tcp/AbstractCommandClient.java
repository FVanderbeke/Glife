package io.vanderbeke.glife.client.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.Future;

public abstract class AbstractCommandClient {

    private final AsynchronousSocketChannel client;

    public AbstractCommandClient(InetSocketAddress serverAddress) throws Exception {

        client = AsynchronousSocketChannel.open();
        Future future = client.connect(serverAddress);

        if(Objects.nonNull(future.get())) {
            throw new Exception("Could not connect to admin server.");
        }
    }

    protected abstract String getMssage();

    public void execute() {
        ByteBuffer buffer = ByteBuffer.wrap(getMssage().getBytes());
        client.write(buffer);
    }
}
