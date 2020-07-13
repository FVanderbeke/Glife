package io.vanderbeke.glife.api.service;

import io.vanderbeke.glife.api.model.AutomatonProcessor;

/**
 * Service managing the automaton periodical save.
 */
public interface AutomatonSaver extends AutomatonProcessor.Listener {
    /**
     * To start to save periodically.
     */
    void start();

    /**
     * To stop the saving process.
     */
    void stop();

    /**
     * @return if started or not.
     */
    boolean isStarted();

    /**
     * @return if a saving task is already scheduled.
     */
    boolean isScheduled();
}
