package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.factory.AutomatonFactory;
import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.Configuration;
import io.vanderbeke.glife.api.model.Universe;
import io.vanderbeke.glife.business.basic.BasicAutomaton;
import io.vanderbeke.glife.business.basic.BasicAutomatonState;
import io.vanderbeke.glife.business.basic.BasicUniverse;
import io.vavr.control.Try;

import java.util.UUID;

/**
 * Basic {@link AutomatonFactory} implementation to create new automaton instances.
 */
public class BasicAutomatonFactory implements AutomatonFactory {

    @Override
    public Try<Automaton> create(Configuration configuration) {

        UUID id = UUID.randomUUID();

        if (configuration.pattern().width() * configuration.pattern().height() <= 0) {
            return Try.success(new BasicAutomaton(id, new BasicAutomatonState(0, Universe.aVoid())));
        }

        BasicUniverse universe = new BasicUniverse(
                configuration.pattern().width(),
                configuration.pattern().height(),
                configuration.strategy(),
                configuration.pattern().space().toArray(),
                configuration.ruleSet());

        return Try.success(new BasicAutomaton(id, new BasicAutomatonState(0, universe)));
    }
}
