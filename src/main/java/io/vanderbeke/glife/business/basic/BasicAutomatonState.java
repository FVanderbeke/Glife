package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.AutomatonState;
import io.vanderbeke.glife.api.model.Universe;

public class BasicAutomatonState implements AutomatonState {
    private final long generationNumber;
    private final Universe universe;

    public BasicAutomatonState(long generationNumber, Universe universe) {
        this.generationNumber = generationNumber;
        this.universe = universe;
    }

    @Override
    public long generationNumber() {
        return generationNumber;
    }

    @Override
    public Universe universe() {
        return universe;
    }
}
