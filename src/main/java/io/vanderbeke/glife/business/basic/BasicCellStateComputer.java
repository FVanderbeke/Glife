package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.repository.RuleSetRepository;
import io.vanderbeke.glife.api.service.CellStateComputer;

public class BasicCellStateComputer implements CellStateComputer {

    private final RuleSetRepository ruleSetRepository;

    public BasicCellStateComputer(RuleSetRepository ruleSetRepository) {
        this.ruleSetRepository = ruleSetRepository;
    }

    @Override
    public boolean compute(boolean currentState, int neighborhood, String ruleSetId) {
        return ruleSetRepository.find(ruleSetId)
                .flatMap(ruleSet -> ruleSet.compute(currentState, neighborhood))
                .getOrElse(currentState);
    }
}
