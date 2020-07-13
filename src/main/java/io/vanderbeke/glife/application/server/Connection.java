package io.vanderbeke.glife.application.server;

import io.vanderbeke.glife.application.core.NioUtil;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * One connection per remote client.
 */
public class Connection {
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    private final CompletionHandler<Integer, Void> writeHandler = NioUtil.toHandler(message -> messageSent(), error -> close());
    private final long connectionId;
    private final ConnectionServer owner;
    private final AtomicBoolean isWriting = new AtomicBoolean(false);
    private final AsynchronousSocketChannel channel;

    public Connection(long connectionId, AsynchronousSocketChannel channel, ConnectionServer owner) {
        this.connectionId = connectionId;
        this.channel = channel;
        this.owner = owner;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void write(String message) {
        if (isWriting.compareAndSet(false, true)) {
            sendMessage(ByteBuffer.wrap(message.getBytes()));
        }
    }

    private void sendMessage(ByteBuffer message) {
        if (Objects.isNull(message)) {
            return;
        }

        if (!channel.isOpen()) {
            return;
        }
        LOGGER.log(Level.FINEST, () -> "Writing message for connection " + connectionId);
        channel.write((message), null, writeHandler);
    }

    private void messageSent() {
        isWriting.compareAndSet(true, false);
    }

    public void close() {
        LOGGER.log(Level.FINE, () -> "Closing connection " + connectionId);

        this.owner.remove(connectionId);

        try {
            if (this.channel.isOpen()) {
                this.channel.close();
                this.channel.shutdownInput();
            }
            LOGGER.log(Level.FINE, () -> "Connection " + connectionId + " closed");
        } catch (ClosedChannelException cce) {
            // Does nothing.
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Error occured when trying to close connection " + connectionId, e);
        }
    }
}
