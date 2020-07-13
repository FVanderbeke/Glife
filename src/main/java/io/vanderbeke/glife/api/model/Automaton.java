package io.vanderbeke.glife.api.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Stores the automaton & universe state at a given time.
 */
public interface Automaton {
    UUID id();
    AutomatonState state();

    default boolean isNotStarted() {
        return Objects.equals(this, notStarted());
    }
    static Automaton notStarted() {
        return NotStartedAutomaton.getInstance();
    }
}

class NotStartedAutomaton implements Automaton {
    private static final UUID DEFAULT_ID = UUID.fromString("557106bc-b486-11e9-a2a3-2a2ae2dbcce4");

    private static final NotStartedAutomaton INSTANCE = new NotStartedAutomaton();

    static NotStartedAutomaton getInstance() {
        return INSTANCE;
    }

    private NotStartedAutomaton() {}

    @Override
    public UUID id() {
        return DEFAULT_ID;
    }

    @Override
    public AutomatonState state() {
        return AutomatonState.notInitialized();
    }
}
