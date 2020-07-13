package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.Universe;
import io.vanderbeke.glife.api.service.NeighborhoodComputer;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Check alive neighbors in a fix universe.
 */
public class FixedNeighborhoodComputer implements NeighborhoodComputer {

    @Override
    public boolean manages(ExpansionStrategy strategy) {
        return strategy == ExpansionStrategy.FIXED;
    }

    @Override
    public IntStream findNeighbors(int index, Universe universe) {
        if (universe.strategy() != ExpansionStrategy.FIXED) {
            return IntStream.empty();
        }

        final int universeSize = universe.width() * universe.height();

        if (universeSize <= 0) {
            return IntStream.empty();
        }

        int x = index / universe.width();
        int y = index % universe.width();

        return Stream.of(
                Cell.of(x - 1, y - 1),
                Cell.of(x - 1, y),
                Cell.of(x - 1, y + 1),
                Cell.of(x, y - 1),
                Cell.of(x, y + 1),
                Cell.of(x + 1, y - 1),
                Cell.of(x + 1, y),
                Cell.of(x + 1, y + 1))
                .filter(cell -> cell.isInRange(universe.width(), universe.height()))
                .mapToInt(cell -> cell.x * universe.width() + cell.y)
                .filter(idx -> idx != index)
                .distinct()
                .sorted();
    }
}
