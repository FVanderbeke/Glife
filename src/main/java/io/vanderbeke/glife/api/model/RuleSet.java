package io.vanderbeke.glife.api.model;

import io.vavr.control.Try;

import java.util.Arrays;
import java.util.List;

/**
 * A set of transition rules.
 *
 * By default, two are provided:
 * <ul>
 *     <li>ANKAMA: spawning/surviving if alive neighbors number is between 1 or 5. Die otherwise.</li>
 *     <li>CONWAY: Well known Conway rules.</li>
 * </ul>
 */
public interface RuleSet {
    enum Basic implements RuleSet {
        ANKAMA(new DefaultRuleSet("ANKAMA", Arrays.asList(AnkamaRule.values()))),
        CONWAY(new DefaultRuleSet("CONWAY", Arrays.asList(ConwayRule.values())));

        private final RuleSet delegate;

        Basic(RuleSet delegate) {
            this.delegate = delegate;
        }


        @Override
        public String id() {
            return delegate.id();
        }

        @Override
        public List<Rule> rules() {
            return delegate.rules();
        }

        @Override
        public Try<Boolean> compute(boolean currentState, int neighborhood) {
            return delegate.compute(currentState, neighborhood);
        }
    }

    String id();
    List<Rule> rules();
    Try<Boolean> compute(boolean currentState, int neighborhood);
}
