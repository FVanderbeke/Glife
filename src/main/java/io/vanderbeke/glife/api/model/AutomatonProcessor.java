package io.vanderbeke.glife.api.model;

import io.vavr.control.Try;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Main process. Periodically generates new universe content.
 */
public interface AutomatonProcessor {

    /**
     * To notify generation updates.
     */
    @FunctionalInterface
    interface Listener {
        void onNext(Automaton automaton);
    }

    /**
     * To initialize this process runner.
     *
     * @param properties GLife properties.
     *
     * @return First generation result.
     */
    Try<Automaton> initialize(Properties properties);

    /**
     * To stop the generation process.
     *
     * @return The last generation result.
     */
    Try<Automaton> stop();

    /**
     * To reset the universe content.
     *
     * @return The updated generation result.
     */
    Try<Automaton> reset();

    Optional<Automaton> currentGeneration();
    long register(Listener listener);
    boolean unregister(long listenerId, Listener listener);
    boolean isRegistered(Listener listener);
    boolean isRegistered(long listenerId);

    long refreshRate(TimeUnit unit);
    boolean isInitializing();
    boolean isStarted();
}
