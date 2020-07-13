package io.vanderbeke.glife.infrastructure.rng;

import io.vanderbeke.glife.api.model.Configuration;
import io.vanderbeke.glife.api.service.Provider;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.infrastructure.core.DefaultConfiguration;
import io.vavr.control.Try;

import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class RngProvider implements Provider {
    @Override
    public String name() {
        return "RNG";
    }

    @Override
    public Try<Configuration> provide(Properties properties) {
        return Try.of(() -> unsafeProvide(properties));
    }

    private Configuration unsafeProvide(Properties properties) {
        DefaultConfiguration.Builder builder = DefaultConfiguration.aBuilder(properties);

        float spawnRate = Optional.ofNullable(properties.get(Constants.CONF_UNIVERSE_SPAWN_RATE))
                .map(String.class::cast)
                .map(Float::parseFloat)
                .orElse(Constants.DEFAULT_UNIVERSE_SPAWN_RATE);

        if (builder.getPattern().getWidth() < 0 || builder.getPattern().getHeight() < 0) {
            throw new IllegalArgumentException("Width and height must be both positives");
        }

        if (spawnRate < 0 || spawnRate > 1) {
            throw new IllegalArgumentException("Spawn rate must be between 0 and 1");
        }

        int spaceSize = builder.getPattern().getWidth() * builder.getPattern().getHeight();
        long nbAliveCells = new Float(spawnRate * spaceSize).longValue();

        int[] space = new Random().ints(nbAliveCells, 0, spaceSize).sorted().toArray();

        builder.getPattern().setSpace(space);

        return builder.build();
    }
}
