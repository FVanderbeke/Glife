package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.exception.UnknownRuleSetException;
import io.vanderbeke.glife.api.model.RuleSet;
import io.vanderbeke.glife.api.repository.RuleSetRepository;
import io.vavr.control.Try;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasicRuleSetRepository implements RuleSetRepository {
    private final Map<String, RuleSet> ruleSets;

    public BasicRuleSetRepository() {
        this.ruleSets = Arrays.asList(RuleSet.Basic.values()).stream().collect(Collectors.toMap(RuleSet::id, Function.identity()));
    }

    @Override
    public Try<RuleSet> find(String id) {
        return Optional.ofNullable(id)
                .map(ruleSets::get)
                .map(Try::success)
                .orElseGet(() -> Try.failure(new UnknownRuleSetException(id)));
    }
}
