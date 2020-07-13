package io.vanderbeke.glife.api.repository;

import io.vanderbeke.glife.api.model.Automaton;
import io.vavr.control.Try;

import java.util.Optional;

/**
 * Service managing automaton update.
 */
public interface AutomatonRepository {
    /**
     * Saves automaton.
     * @param automaton automaton to store.
     * @return the automaton itself if succeed.
     */
    Try<Automaton> save(Automaton automaton);

    /**
     * @return If an automaton is currently saved.
     */
    boolean exists();

    /**
     * @return If a save exists, checks if this one is not corrupted.
     */
    boolean isCorrupted();

    /**
     * Deletes any availabel save state.
     */
    void delete();

    /**
     * @return The currently saved state, if it exists. If this one is corrupted, then deletes it and return empty result.
     */
    Try<Optional<Automaton>> find();
}
