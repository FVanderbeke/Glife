package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.RuleSet;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedNeighborhoodComputerTest {

    private FixedNeighborhoodComputer computer = new FixedNeighborhoodComputer();

    @Test
    public void should_computer_neighbors_in_fix_canvas() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{3,4,5}, RuleSet.Basic.CONWAY.name());

        assertThat(computer.findNeighbors(0, universe).toArray()).containsOnly(1,3,4);
        assertThat(computer.findNeighbors(1, universe).toArray()).containsOnly(0,2,3,4,5);
        assertThat(computer.findNeighbors(2, universe).toArray()).containsOnly(1,4,5);
        assertThat(computer.findNeighbors(3, universe).toArray()).containsOnly(0,1,4,6,7);
        assertThat(computer.findNeighbors(4, universe).toArray()).containsOnly(0,1,2,3,5,6,7,8);
        assertThat(computer.findNeighbors(5, universe).toArray()).containsOnly(1,2,4,7,8);
        assertThat(computer.findNeighbors(6, universe).toArray()).containsOnly(3,4,7);
        assertThat(computer.findNeighbors(7, universe).toArray()).containsOnly(3,4,5,6,8);
        assertThat(computer.findNeighbors(8, universe).toArray()).containsOnly(4,5,7);


        universe = new BasicUniverse(3, 2, ExpansionStrategy.FIXED, new int[]{3,4,5}, RuleSet.Basic.CONWAY.name());

        assertThat(computer.findNeighbors(0, universe).toArray()).containsOnly(1,3,4);
        assertThat(computer.findNeighbors(1, universe).toArray()).containsOnly(0,2,3,4,5);
        assertThat(computer.findNeighbors(2, universe).toArray()).containsOnly(1,4,5);
        assertThat(computer.findNeighbors(3, universe).toArray()).containsOnly(0,1,4);
        assertThat(computer.findNeighbors(4, universe).toArray()).containsOnly(0,1,2,3,5);
        assertThat(computer.findNeighbors(5, universe).toArray()).containsOnly(1,2,4);
    }
}
