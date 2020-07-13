package io.vanderbeke.glife.api.service;

/**
 * Service that performs the transition state on cells.
 */
public interface CellStateComputer {
    /**
     * Given a current state cell, its neighborhood, and the applied rule set, returns the next state to apply to the cell.
     *
     * @param currentState Current cell state.
     *
     * @param neighborhood Number of alive cells in its neighborhood.
     *
     * @param ruleSetId The rules to apply.
     *
     * @return True if the cell must spawn/stay alive. False if it must die.
     */
    boolean compute(boolean currentState, int neighborhood, String ruleSetId);
}
