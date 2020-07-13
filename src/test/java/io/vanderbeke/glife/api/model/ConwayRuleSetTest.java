package io.vanderbeke.glife.api.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConwayRuleSetTest {

    @Test
    public void should_die_due_to_underpopulation() {
        boolean result = RuleSet.Basic.CONWAY.compute(true, 1).get();
        assertThat(result).isFalse();
    }

    @Test
    public void should_die_due_to_next_generation() {
        boolean result = RuleSet.Basic.CONWAY.compute(true, 2).get();
        assertThat(result).isTrue();

        result = RuleSet.Basic.CONWAY.compute(true, 3).get();
        assertThat(result).isTrue();
    }

    @Test
    public void should_die_due_to_overpopulation() {
        boolean result = RuleSet.Basic.CONWAY.compute(true, 6).get();
        assertThat(result).isFalse();
    }

    @Test
    public void should_become_alive_due_to_reproduction() {
        boolean result = RuleSet.Basic.CONWAY.compute(false, 3).get();
        assertThat(result).isTrue();
    }

    @Test
    public void should_stay_dead() {
        boolean result = RuleSet.Basic.CONWAY.compute(false, 2).get();
        assertThat(result).isFalse();
    }
}
