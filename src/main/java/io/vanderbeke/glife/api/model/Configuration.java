package io.vanderbeke.glife.api.model;

import java.util.Objects;

/**
 * Configuration used to generate a universe.
 */
public interface Configuration {
    /**
     * @return Content to inject in the universe.
     */
    Pattern pattern();

    /**
     * @return Strategy we must use to find neighbors in the universe.
     */
    ExpansionStrategy strategy();

    /**
     * @return Rule set to use in the universe.
     */
    String ruleSet();

    default boolean isNoConfiguration() {
        return Objects.equals(this, NoConfiguration.getInstance());
    }

    static Configuration noConfiguration() {
        return NoConfiguration.getInstance();
    }
}

class NoConfiguration implements Configuration {
    private static final NoConfiguration INSTANCE = new NoConfiguration();

    static NoConfiguration getInstance() {
        return INSTANCE;
    }

    private NoConfiguration() {
    }

    @Override
    public Pattern pattern() {
        return Pattern.aVoid();
    }

    @Override
    public ExpansionStrategy strategy() {
        return ExpansionStrategy.FIXED;
    }

    @Override
    public String ruleSet() {
        return "";
    }
}

class ReverseConfiguration implements Configuration {
    private final Pattern pattern;
    private final Universe universe;

     ReverseConfiguration(Universe universe) {
        this.pattern = Pattern.of(universe);
        this.universe = universe;
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public ExpansionStrategy strategy() {
        return universe.strategy();
    }

    @Override
    public String ruleSet() {
        return universe.ruleSet();
    }
}