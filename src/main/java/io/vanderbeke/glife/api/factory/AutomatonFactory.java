package io.vanderbeke.glife.api.factory;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.Configuration;
import io.vavr.control.Try;

/**
 * Service dedicated to the {@link Automaton} creation.
 */
public interface AutomatonFactory {

    Try<Automaton> create(Configuration configuration);
}
