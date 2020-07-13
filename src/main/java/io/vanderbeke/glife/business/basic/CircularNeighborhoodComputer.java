package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.Universe;
import io.vanderbeke.glife.api.service.NeighborhoodComputer;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Check alive neighbors in a circular universe.
 */
public class CircularNeighborhoodComputer implements NeighborhoodComputer {

    @Override
    public boolean manages(ExpansionStrategy strategy) {
        return strategy == ExpansionStrategy.CIRCULAR;
    }

    private int shift(int value, int size) {
        return (value + size) % size;
    }

    @Override
    public IntStream findNeighbors(int index, Universe universe) {
        if (universe.strategy() != ExpansionStrategy.CIRCULAR) {
            return IntStream.empty();
        }
        int width = universe.width();
        int height = universe.height();

        int universeSize = width * height;

        if (universeSize <= 0) {
            return IntStream.empty();
        }

        int x = index / width;
        int y = index % width;

        return Stream.of(
                Cell.of(shift(x - 1, height), shift(y - 1, width)),
                Cell.of(shift(x - 1, height), shift(y, width)),
                Cell.of(shift(x - 1, height), shift(y + 1, width)),
                Cell.of(shift(x, height), shift(y - 1, width)),
                Cell.of(shift(x, height), shift(y + 1, width)),
                Cell.of(shift(x + 1, height), shift(y - 1, width)),
                Cell.of(shift(x + 1, height), shift(y, width)),
                Cell.of(shift(x + 1, height), shift(y + 1, width)))
                .mapToInt(cell -> cell.x * width + cell.y)
                .filter(idx -> idx != index)
                .distinct()
                .sorted();
    }
}
