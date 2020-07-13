package io.vanderbeke.glife.api.service;

import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.Universe;

import java.util.stream.IntStream;

/**
 * Service in charge of computing the number of alive cells around a given position.
 */
public interface NeighborhoodComputer {
    /**
     * If it can manage the given expansion strategy.
     *
     * @param strategy Strategy to apply for neighborhood computation.
     *
     * @return if it manages this strategy.
     */
    boolean manages(ExpansionStrategy strategy);

    /**
     * @param index Current cell index.
     * @param universe Universe in which the cell exists (dead or alive).
     *
     * @return the number of alive cells (stream of their indexes) around the given cell.
     */
    IntStream findNeighbors(int index, Universe universe);
}
