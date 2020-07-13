package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.RuleSet;
import io.vanderbeke.glife.api.repository.RuleSetRepository;
import io.vanderbeke.glife.api.service.AutomatonExecutor;
import io.vanderbeke.glife.api.service.CellStateComputer;
import io.vanderbeke.glife.api.service.NeighborhoodComputer;
import io.vanderbeke.glife.api.service.NeighborhoodComputers;
import io.vanderbeke.glife.infrastructure.basic.BasicRuleSetRepository;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAutomatonExecutorTest {
    private final RuleSetRepository ruleSetRepository = new BasicRuleSetRepository();
    private final CellStateComputer cellStateComputer = new BasicCellStateComputer(ruleSetRepository);

    private final NeighborhoodComputer circularNeighborhoodComputer = new CircularNeighborhoodComputer();
    private final NeighborhoodComputer fixedNeighborhoodComputer = new FixedNeighborhoodComputer();
    private final NeighborhoodComputers neighborhoodComputers = new NeighborhoodComputers(Arrays.asList(circularNeighborhoodComputer, fixedNeighborhoodComputer));

    private final AutomatonExecutor automatonExecutor = new BasicAutomatonExecutor(cellStateComputer, neighborhoodComputers);

    @Test
    public void should_execute_next_generation() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{3,4,5}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        Automaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        // THEN - cyclic test.
        automaton = automatonExecutor.performNext(automaton).get();
        assertThat(automaton.state().universe().aliveCells().toArray()).containsOnly(1,4,7);
        automaton = automatonExecutor.performNext(automaton).get();
        assertThat(automaton.state().universe().aliveCells().toArray()).containsOnly(3,4,5);
    }
}
