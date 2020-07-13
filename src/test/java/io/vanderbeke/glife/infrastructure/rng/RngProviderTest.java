package io.vanderbeke.glife.infrastructure.rng;

import io.vanderbeke.glife.api.model.Configuration;
import io.vanderbeke.glife.core.Constants;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

public class RngProviderTest {
    private RngProvider provider = new RngProvider();

    @Test
    public void void_should_provide_random_pattern_with_default_spawn_rate() {
        // GIVEN
        Properties properties = new Properties();
        properties.setProperty(Constants.CONF_UNIVERSE_WIDTH, "10");
        properties.setProperty(Constants.CONF_UNIVERSE_HEIGHT, "20");

        // WHEN
        Try<Configuration> conf = provider.provide(properties);

        // THEN
        assertThat(conf.isSuccess()).isTrue();
        assertThat(conf.get().pattern().width()).isEqualTo(10);
        assertThat(conf.get().pattern().height()).isEqualTo(20);
        assertThat(conf.get().pattern().space().toArray()).hasSize(30);
    }

    @Test
    public void void_should_provide_random_pattern_with_custom_spawn_rate() {
        Properties properties = new Properties();

        properties.setProperty(Constants.CONF_UNIVERSE_SPAWN_RATE, "0.25");
        properties.setProperty(Constants.CONF_UNIVERSE_WIDTH, "10");
        properties.setProperty(Constants.CONF_UNIVERSE_HEIGHT, "20");

        // WHEN
        Try<Configuration> conf = provider.provide(properties);

        // THEN
        assertThat(conf.isSuccess()).isTrue();
        assertThat(conf.get().pattern().width()).isEqualTo(10);
        assertThat(conf.get().pattern().height()).isEqualTo(20);
        assertThat(conf.get().pattern().space().toArray()).hasSize(50);

    }
}
