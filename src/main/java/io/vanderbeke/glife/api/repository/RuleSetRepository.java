package io.vanderbeke.glife.api.repository;

import io.vanderbeke.glife.api.model.RuleSet;
import io.vavr.control.Try;

/**
 * To find declared rule set on the system.
 */
public interface RuleSetRepository {
    /**
     * Gets the rule set having the asked id.
     *
     * @param id Rule set id.
     *
     * @return The rule set corresponding to it. An error otherwise.
     */
    Try<RuleSet> find(String id);
}
