package io.vanderbeke.glife.api.model;

import io.vavr.control.Try;

import java.util.List;

public class DefaultRuleSet implements RuleSet {
    private final String name;
    private final List<Rule> rules;

    public DefaultRuleSet(String name, List<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }

    @Override
    public String id() {
        return name;
    }

    @Override
    public List<Rule> rules() {
        return rules;
    }

    @Override
    public Try<Boolean> compute(boolean currentState, int neighborhood) {
        return Try.success(rules.stream()
                .filter(rule -> rule.matches(currentState) && rule.check(neighborhood))
                .map(rule -> rule.newState())
                .findAny()
                .orElse(currentState));
    }
}
