package io.vanderbeke.glife.api.service;

import io.vanderbeke.glife.api.model.Configuration;
import io.vavr.control.Try;

import java.util.Properties;

/**
 * Service that provides a universe configuration thanks to a set of properties.
 */
public interface Provider {
    /**
     * @return Unique provider name.
     */
    String name();

    /**
     * @param properties Properties loaded in the system.
     * @return An universe configuration, ready for automaton processing.
     */
    Try<Configuration> provide(Properties properties);
}
