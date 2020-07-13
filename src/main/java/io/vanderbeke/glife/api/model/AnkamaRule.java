package io.vanderbeke.glife.api.model;

import java.util.function.Predicate;

/**
 * Default rules for Ankama.
 */
public enum AnkamaRule implements Rule {

    FIRST(false, true, nbNeighbors -> nbNeighbors >= 1 && nbNeighbors <= 5),
    SECOND(false, false, nbNeighbors -> nbNeighbors == 0 || nbNeighbors > 5),
    THIRD(true, true, nbNeighbors -> nbNeighbors >= 1 && nbNeighbors <= 5),
    FOURTH(true, false, nbNeighbors -> nbNeighbors == 0 || nbNeighbors > 5);

    private final boolean matchingState;
    private final boolean returnedState;
    private final Predicate<Integer> filter;

    AnkamaRule(boolean matchingState, boolean returnedState, Predicate<Integer> filter) {
        this.matchingState = matchingState;
        this.returnedState = returnedState;
        this.filter = filter;
    }

    @Override
    public boolean matches(boolean currentState) {
        return currentState == matchingState;
    }

    @Override
    public boolean check(int nbAliveNeighbors) {
        return filter.test(nbAliveNeighbors);
    }

    @Override
    public boolean newState() {
        return returnedState;
    }


}
