package io.vanderbeke.glife.api.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnkamaRuleSetTest {
    @Test
    public void should_spawn_if_neighbors_between_one_and_five() {
        boolean result = RuleSet.Basic.ANKAMA.compute(false, 1).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(false, 2).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(false, 3).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(false, 4).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(false, 5).get();
        assertThat(result).isTrue();
    }

    @Test
    public void should_stay_alive_if_neighbors_between_one_and_five() {
        boolean result = RuleSet.Basic.ANKAMA.compute(true, 1).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(true, 2).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(true, 3).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(true, 4).get();
        assertThat(result).isTrue();
        result = RuleSet.Basic.ANKAMA.compute(true, 5).get();
        assertThat(result).isTrue();
    }

    @Test
    public void should_die_if_no_neighbor_or_overpopulation() {
        boolean result = RuleSet.Basic.ANKAMA.compute(true, 0).get();
        assertThat(result).isFalse();
        result = RuleSet.Basic.ANKAMA.compute(true, 6).get();
        assertThat(result).isFalse();
        result = RuleSet.Basic.ANKAMA.compute(true, 7).get();
        assertThat(result).isFalse();
        result = RuleSet.Basic.ANKAMA.compute(true, 8).get();
        assertThat(result).isFalse();
    }

    @Test
    public void should_stay_dead_if_no_neighbor_or_overpopulation() {
        boolean result = RuleSet.Basic.ANKAMA.compute(false, 0).get();
        assertThat(result).isFalse();
        result = RuleSet.Basic.ANKAMA.compute(false, 6).get();
        assertThat(result).isFalse();
        result = RuleSet.Basic.ANKAMA.compute(false, 7).get();
        assertThat(result).isFalse();
        result = RuleSet.Basic.ANKAMA.compute(false, 8).get();
        assertThat(result).isFalse();
    }

}
