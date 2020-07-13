package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.Universe;

import java.util.stream.IntStream;

public class BasicUniverse implements Universe {
    private final int width;
    private final int height;
    private final ExpansionStrategy strategy;
    private final int[] aliveCells;
    private final String ruleSet;

    public BasicUniverse(int width, int height, ExpansionStrategy strategy, int[] aliveCells, String ruleSet) {
        this.width = width;
        this.height = height;
        this.strategy = strategy;
        this.aliveCells = aliveCells;
        this.ruleSet = ruleSet;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public ExpansionStrategy strategy() {
        return strategy;
    }

    @Override
    public IntStream aliveCells() {
        return IntStream.of(aliveCells);
    }

    @Override
    public String ruleSet() {
        return ruleSet;
    }
}
