package io.vanderbeke.glife.api.model;

import java.util.function.Predicate;

/**
 * Conway rules
 */
public enum ConwayRule implements Rule {

    FIRST(true, false, nbNeighbors -> nbNeighbors < 2),
    SECOND(true, false, nbNeighbors -> nbNeighbors > 3),
    THIRD(true, true, nbNeighbors -> nbNeighbors == 2 || nbNeighbors == 3),
    FOURTH(false, true, nbNeighbors -> nbNeighbors == 3);

    private final boolean matchingState;
    private final boolean returnedState;
    private final Predicate<Integer> filter;

    ConwayRule(boolean matchingState, boolean returnedState, Predicate<Integer> filter) {
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
