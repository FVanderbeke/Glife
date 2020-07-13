package io.vanderbeke.glife.api.service;

import io.vavr.control.Try;

import java.util.Properties;

/**
 * Service that allows to load properties in the system.
 */
public interface PropertyLoader {
    /**
     * Loads properties from the configuration file (if available) and override the default values thanks to the ones read from the file.
     * Then, overrides the given properties by values defined in the command line arguments.
     *
     * @param args Command line arguments.
     *
     * @return Properties correctly defined for the process.
     */
    Try<Properties> load(String... args);
}
