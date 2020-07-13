package io.vanderbeke.glife.api.model;

import java.util.Objects;

/**
 * Automaton state produced at a given generation loop.
 */
public interface AutomatonState {
    /**
     * @return Generation loop that produced this state.
     */
    long generationNumber();

    /**
     * @return Universe content at this generation step.
     */
    Universe universe();

    /**
     * @return If initialized.
     */
    default boolean isInitialized() {
        return !Objects.equals(this, NotInitializedAutomatonState.getInstance());
    }

    /**
     * @return returns a state representing a non initialized generation processor.
     */
    static AutomatonState notInitialized() {
        return NotInitializedAutomatonState.getInstance();
    }
}

final class NotInitializedAutomatonState implements AutomatonState {
    private static final NotInitializedAutomatonState INSTANCE = new NotInitializedAutomatonState();

    static NotInitializedAutomatonState getInstance() {
        return INSTANCE;
    }

    private NotInitializedAutomatonState() {}

    @Override
    public long generationNumber() {
        return -1;
    }

    @Override
    public Universe universe() {
        return Universe.aVoid();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(INSTANCE);
    }
}
