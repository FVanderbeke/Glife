package io.vanderbeke.glife.api.model;

import java.util.stream.IntStream;

/**
 * Represents the space state of dead/alive cells, governed by a set of rules.
 */
public interface Universe {
    /**
     * @return To get a void universe ("more than empty").
     */
    static Universe aVoid() {
        return VoidUniverse.getInstance();
    }

    /**
     * @return Universe canvas width.
     */
    int width();

    /**
     * @return Universe canvas height.
     */
    int height();

    /**
     * @return How borders react and how neighbors are computed.
     */
    ExpansionStrategy strategy();

    /**
     * @return A stream of indexes representing coordinates (for a tuple (x,y): <code>index = x * width + y</code>)
     *         of all alive cells stored in the universe space.
     */
    IntStream aliveCells();

    /**
     * @return Name of the rule set that is applied on this universe.
     */
    String ruleSet();

    default boolean isEmpty() {
        return aliveCells().count() == 0;
    }

    default boolean isAlive(int index) {
        return aliveCells().anyMatch(i -> i == index);
    }

}

final class VoidUniverse implements Universe {
    private static final VoidUniverse INSTANCE = new VoidUniverse();

    static VoidUniverse getInstance() {
        return INSTANCE;
    }

    private VoidUniverse() {
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
    public ExpansionStrategy strategy() {
        return ExpansionStrategy.FIXED;
    }

    @Override
    public IntStream aliveCells() {
        return IntStream.empty();
    }

    @Override
    public String ruleSet() {
        return null;
    }
}
