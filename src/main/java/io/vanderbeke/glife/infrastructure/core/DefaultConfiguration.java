package io.vanderbeke.glife.infrastructure.core;

import io.vanderbeke.glife.api.model.Configuration;
import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.Pattern;
import io.vanderbeke.glife.core.Constants;

import java.util.Optional;
import java.util.Properties;

public class DefaultConfiguration implements Configuration {

    public static Builder aBuilder(Properties props) {
        Builder builder = new Builder();

        builder.getPattern()
                .setWidth(
                        Optional.ofNullable(props.get(Constants.CONF_UNIVERSE_WIDTH))
                                .map(String.class::cast)
                                .map(Integer::parseInt)
                                .orElse(-1))
                .setHeight(
                        Optional.ofNullable(props.get(Constants.CONF_UNIVERSE_HEIGHT))
                                .map(String.class::cast)
                                .map(Integer::parseInt)
                                .orElse(-1));

        builder
                .setStrategy(
                        Optional.ofNullable(props.get(Constants.CONF_UNIVERSE_EXPANSION_STRATEGY))
                                .map(String.class::cast)
                                .map(String::toUpperCase)
                                .map(ExpansionStrategy::valueOf)
                                .orElse(ExpansionStrategy.FIXED))
                .setRuleSet(
                        Optional.ofNullable(props.get(Constants.CONF_UNIVERSE_RULE_SET))
                                .map(String.class::cast)
                                .orElse(Constants.DEFAULT_UNIVERSE_RULE_SET));
        return builder;

    }

    public static class Builder {

        private final DefaultPattern.Builder pattern = DefaultPattern.aBuilder();
        private ExpansionStrategy strategy;
        private String ruleSet;

        public DefaultPattern.Builder getPattern() {
            return pattern;
        }

        public ExpansionStrategy getStrategy() {
            return strategy;
        }

        public Builder setStrategy(ExpansionStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public String getRuleSet() {
            return ruleSet;
        }

        public Builder setRuleSet(String ruleSet) {
            this.ruleSet = ruleSet;
            return this;
        }

        public DefaultConfiguration build() {
            return new DefaultConfiguration(this);
        }
    }

    private final Pattern pattern;
    private final ExpansionStrategy strategy;
    private final String ruleSet;

    private DefaultConfiguration(Builder builder) {
        this.pattern = builder.getPattern().build();
        this.strategy = builder.getStrategy();
        this.ruleSet = builder.getRuleSet();
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public ExpansionStrategy strategy() {
        return strategy;
    }

    @Override
    public String ruleSet() {
        return ruleSet;
    }
}
