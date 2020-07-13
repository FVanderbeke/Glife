package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.*;
import io.vanderbeke.glife.api.model.Automaton;

import java.util.UUID;

public class BasicAutomaton implements Automaton {
    private final UUID id;
    private final AutomatonState state;

    public BasicAutomaton(UUID id, AutomatonState state) {
        this.id = id;
        this.state = state;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public AutomatonState state() {
        return state;
    }

}
