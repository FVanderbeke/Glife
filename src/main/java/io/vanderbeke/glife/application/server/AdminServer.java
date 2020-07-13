package io.vanderbeke.glife.application.server;

import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.service.AutomatonSaver;
import io.vanderbeke.glife.application.core.NioUtil;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminServer {
    private static final Logger LOGGER = Logger.getLogger(AdminServer.class.getName());

    private final ConnectionServer connectionServer;
    private final AutomatonProcessor automatonProcessor;
    private final AutomatonSaver automatonSaver;

    private final AsynchronousServerSocketChannel adminChannel;
    private final CompletionHandler<AsynchronousSocketChannel, Void> mainHandler;

    public AdminServer(ConnectionServer connectionServer, AutomatonProcessor automatonProcessor, AutomatonSaver automatonSaver, InetSocketAddress address) {
        this.connectionServer = connectionServer;
        this.automatonProcessor = automatonProcessor;
        this.automatonSaver = automatonSaver;

        try {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10);
            this.adminChannel = AsynchronousServerSocketChannel.open(group);

            this.mainHandler = NioUtil.toHandler(channel -> manageConnection(channel));

            this.adminChannel.bind(address);
            this.adminChannel.accept(null, NioUtil.toHandler(result -> manageConnection(result)));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not start admin server.", e);
            throw new RuntimeException(e);
        }
    }

    private void manageConnection(AsynchronousSocketChannel channel) {
        AdminConnection connection = new AdminConnection(channel, connectionServer, this,  automatonProcessor, automatonSaver);
        this.adminChannel.accept(null, mainHandler);
    }


    public void shutdown() {
        try {
            this.adminChannel.close();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not stop admin server.", e);
        }
    }
}
