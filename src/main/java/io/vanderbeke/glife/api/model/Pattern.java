package io.vanderbeke.glife.api.model;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Default content that must be inserted into the universe.
 */
public interface Pattern {
    /**
     * @return Universe width.
     */
    int width();

    /**
     * @return Universe height.
     */
    int height();

    /**
     * @return universe space (index of alive cells)
     */
    IntStream space();

    /**
     * @return If there is nothing in this space (no size, no space).
     */
    default boolean isVoid() {
        return Objects.equals(this, VoidPattern.getInstance());
    }

    /**
     * @return To get a "void" pattern (more than "empty").
     */
    static Pattern aVoid() {
        return VoidPattern.getInstance();
    }

    /**
     * Converts a given universe into a pattern.
     *
     * @param universe Universe to convert.
     *
     * @return The pattern that corresponds to the current universe state.
     */
    static Pattern of(Universe universe) {
        return new ReversePattern(universe);
    }
}

class VoidPattern implements Pattern {
    private static final VoidPattern INSTANCE = new VoidPattern();

    static VoidPattern getInstance() {
        return INSTANCE;
    }

    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public IntStream space() {
        return IntStream.empty();
    }
}

class ReversePattern implements Pattern {
    private final Universe universe;

    ReversePattern(Universe universe) {
        this.universe = universe;
    }

    @Override
    public int width() {
        return universe.width();
    }

    @Override
    public int height() {
        return universe.height();
    }

    @Override
    public IntStream space() {
        return universe.aliveCells();
    }
}
