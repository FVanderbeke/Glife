package io.vanderbeke.glife.api.model;

/**
 * A birth/survival/death rule upon cells available in the universe space.
 *
 * Corresponds to a transition. For a given current state and available neighbors, will provide the new state.
 */
public interface Rule {
    String name();

    /**
     * Checks if it matches the current cell state.
     *
     * @param currentState Current cell state.
     * @return True if the current state matches the rule requirement.
     */
    boolean matches(boolean currentState);

    /**
     * Checks the neighborhood.
     *
     * @param nbAliveNeighbors number of alive neighbors.
     *
     * @return True if the number matches the rule requirements.
     */
    boolean check(int nbAliveNeighbors);

    /**
     * @return the next state to apply on the current cell.
     */
    boolean newState();
}
