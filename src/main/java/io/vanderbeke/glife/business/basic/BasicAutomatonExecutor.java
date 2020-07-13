package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.Universe;
import io.vanderbeke.glife.api.service.AutomatonExecutor;
import io.vanderbeke.glife.api.service.CellStateComputer;
import io.vanderbeke.glife.api.service.NeighborhoodComputers;
import io.vavr.control.Try;

import java.util.stream.IntStream;

/**
 * Basic generation computation implementation. Here we take a given universe state and generate a new one.
 *
 * This basic computation performs the check on each universe cell (in a parallel mode).
 */
public class BasicAutomatonExecutor implements AutomatonExecutor {

    private final CellStateComputer cellStateComputer;
    private final NeighborhoodComputers neighborhoodComputers;

    public BasicAutomatonExecutor(CellStateComputer cellStateComputer, NeighborhoodComputers neighborhoodComputers) {
        this.cellStateComputer = cellStateComputer;
        this.neighborhoodComputers = neighborhoodComputers;
    }

    private int createNeigborhood(int index, Universe universe) {


        return (int) neighborhoodComputers.find(universe.strategy())
                .map(computer -> computer.findNeighbors(index, universe))
                .orElse(IntStream.empty())
                .filter(universe::isAlive)
                .count();
    }

    @Override
    public Try<Automaton> performNext(Automaton automaton) {
        if (!automaton.state().isInitialized()) {
            // Not yet initialized.
            return Try.failure(new IllegalStateException("Automaton not initialized yet"));
        }

        long nextGenerationNumber = automaton.state().generationNumber() + 1;
        final Universe universe = automaton.state().universe();

        if (universe.isEmpty()) {
            BasicUniverse nextUniverse = new BasicUniverse(
                    universe.width(),
                    universe.height(),
                    universe.strategy(),
                    new int[0],
                    universe.ruleSet());

            BasicAutomatonState nextState =  new BasicAutomatonState(nextGenerationNumber, nextUniverse);

            return Try.success(new BasicAutomaton(automaton.id(), nextState));
        }

        final int universeSize = universe.width() * universe.height();

        //
        // Parallel computing.
        //
        int[] nexSpace = IntStream.range(0, universeSize)
                .parallel()
                .filter(index -> cellStateComputer.compute(
                        universe.isAlive(index),
                        createNeigborhood(index, universe),
                        universe.ruleSet()))
                .sorted()
                .toArray();

        Universe nextUniverse = new BasicUniverse(universe.width(), universe.height(), universe.strategy(), nexSpace, universe.ruleSet());

        BasicAutomatonState nextState =  new BasicAutomatonState(nextGenerationNumber, nextUniverse);

        return Try.success(new BasicAutomaton(automaton.id(), nextState));
    }
}
