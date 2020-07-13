package io.vanderbeke.glife.application.server;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.service.Renderer;
import io.vanderbeke.glife.application.core.NioUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managing remote clients to send them rendered value of the automaton.
 */
public class ConnectionServer implements AutomatonProcessor.Listener {
    private static final Logger LOGGER = Logger.getLogger(ConnectionServer.class.getName());

    private final AutomatonProcessor processor;
    private final Renderer renderer;
    private final AsynchronousServerSocketChannel serverChannel;
    private final long listenerId;

    private final AtomicLong connectionIdSeq = new AtomicLong(0);
    private final CompletionHandler<AsynchronousSocketChannel, Void> mainHandler;
    private final ConcurrentMap<Long, Connection> connections = new ConcurrentHashMap<>(50, 0.9f, Runtime.getRuntime().availableProcessors());
    private final AtomicReference<Automaton> currentGenerationRef = new AtomicReference<>(null);
    private final ExecutorService messageSender = Executors.newSingleThreadExecutor();

    public ConnectionServer(AutomatonProcessor processor, Renderer renderer, InetSocketAddress address) {
        this.processor = processor;
        this.renderer = renderer;

        try {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10);
            this.serverChannel = AsynchronousServerSocketChannel.open(group);

            mainHandler = NioUtil.toHandler(this::manageConnection);

            this.serverChannel.bind(address);
            serverChannel.accept(null, NioUtil.toHandler(this::manageConnection));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not start connection server.", e);
            throw new RuntimeException(e);
        }
        this.listenerId = this.processor.register(this);
    }

    private Connection createConnection(long connectionId, AsynchronousSocketChannel channel) {
        Connection result = new Connection(connectionId, channel, this);

        LOGGER.log(Level.FINE, "Created connection " + connectionId);
        result.write("Game Of Life Simulator...\n\n");

        return result;
    }

    private void manageConnection(AsynchronousSocketChannel channel) {

        connections.computeIfAbsent(connectionIdSeq.incrementAndGet(), id -> createConnection(id, channel));
        this.serverChannel.accept(null, mainHandler);
    }

    public void remove(long connectionId) {
        Connection connection = connections.remove(connectionId);

        if (Objects.nonNull(connection)) {
            LOGGER.log(Level.FINE, "Removed connection " + connectionId);
            connection.close();
        }
    }

    public void shutdown() {
        processor.unregister(listenerId, this);

        this.currentGenerationRef.set(null);

        try {
            messageSender.shutdown();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred when shutting down the server.", e);
        }

        try {
            this.serverChannel.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred when shutting down the server.", e);
        }

        try {
            connections.values().parallelStream()
                    .forEach(Connection::close);
        } catch (Exception e) {
            // Does nothing.
        } finally {
            connections.clear();
        }
    }

    @Override
    public void onNext(Automaton automaton) {

        if (connections.isEmpty()) {
            return;
        }

        schedulerMessageSending(automaton);
    }

    private void schedulerMessageSending(Automaton automaton) {
        currentGenerationRef.set(automaton);
        messageSender.execute(this::sendMessage);
    }

    private void sendMessage() {
        Automaton automaton = currentGenerationRef.getAndSet(null);

        if (Objects.isNull(automaton)) {
            return;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            renderer.render(automaton, out);

            String rendered = new String(out.toByteArray());
            connections.values().parallelStream()
                    .forEach(connection -> connection.write(rendered));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error when trying to render automaton for TCP clients.", e);
        }
    }
}
