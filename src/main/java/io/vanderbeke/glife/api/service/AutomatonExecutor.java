package io.vanderbeke.glife.api.service;

import io.vanderbeke.glife.api.model.Automaton;
import io.vavr.control.Try;

/**
 * This service executes one (and exactly one) new step.
 */
public interface AutomatonExecutor {
    /**
     * Computes/generates the new automaton state.
     *
     * @param automaton Current automaton to compute.
     *
     * @return The automaton with the new generated state.
     */
    Try<Automaton> performNext(Automaton automaton);
}
