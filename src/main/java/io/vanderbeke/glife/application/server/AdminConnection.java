package io.vanderbeke.glife.application.server;

import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.service.AutomatonSaver;
import io.vanderbeke.glife.application.core.NioUtil;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminConnection {
    private static final Logger LOGGER = Logger.getLogger(AdminConnection.class.getName());

    private final AsynchronousSocketChannel channel;
    private final ConnectionServer connectionServer;
    private final AdminServer adminServer;
    private final AutomatonProcessor automatonProcessor;
    private final AutomatonSaver automatonSaver;

    private final AtomicReference<ByteBuffer> readBuffer = new AtomicReference<>();
    private final CompletionHandler<Integer, ByteBuffer> readHandler = NioUtil.toHandler(this::received);

    public AdminConnection(AsynchronousSocketChannel channel, ConnectionServer connectionServer, AdminServer adminServer, AutomatonProcessor automatonProcessor, AutomatonSaver automatonSaver) {
        this.channel = channel;
        this.connectionServer = connectionServer;
        this.adminServer = adminServer;
        this.automatonProcessor = automatonProcessor;
        this.automatonSaver = automatonSaver;

        nextCommand();
    }

    public void shutdown() {
        try {
            automatonSaver.stop();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not stop automaton saver.", e);
        }

        try {
            connectionServer.shutdown();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not shutdown connection server.", e);
        }

        try {
            automatonProcessor.stop();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not stop automaton processor.", e);
        }

        try {
            adminServer.shutdown();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not shutdown connection server.", e);
        }

        LOGGER.log(Level.INFO, "Server shutdown. Bye !");

        System.exit(0);
    }

    private void received(Integer value) {
        if (value.equals(-1)) {
            return;
        }
        String request = read(readBuffer.get());

        LOGGER.log(Level.FINE, () -> "received " + request);

        if ("1".equals(request.trim())) {
            automatonProcessor.reset();
        } else if ("2".equals(request.trim())) {
            shutdown();
        } else {
            LOGGER.log(Level.WARNING, () -> "received unknown admin request: " + request);
            return;
        }

        try {
            if (this.channel.isOpen()) {
                this.channel.close();
                this.channel.shutdownInput();
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Could not close admin connection.", e);
            // Does nothing.
        }
    }

    private String read(ByteBuffer buffer) {
        buffer.flip();

        StringBuilder builder = new StringBuilder();

        while(buffer.remaining() > 0) {
            builder.append((char) buffer.get());
        }

        buffer.clear();

        return builder.toString();
    }


    private void nextCommand() {
        readBuffer.set(ByteBuffer.allocateDirect(1024));
        channel.read(readBuffer.get(), readBuffer.get(), readHandler);
    }

    public void reset() {
        automatonProcessor.reset();
    }
}
