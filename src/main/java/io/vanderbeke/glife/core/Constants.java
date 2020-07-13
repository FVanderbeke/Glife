package io.vanderbeke.glife.core;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * All GLife constants.
 */
public class Constants {
    public static final String CONF_APPLICATION_MODE = "application.mode";
    public static final String CONF_APPLICATION_OUTPUT = "application.out";

    public static final String CONF_PATTERN_FILE_PATH = "pattern.file.path";
    public static final String CONF_UNIVERSE_WIDTH = "universe.width";
    public static final String CONF_UNIVERSE_HEIGHT = "universe.height";

    public static final String CONF_UNIVERSE_REFRESH_RATE_VALUE = "universe.refresh.rate.value";
    public static final String CONF_UNIVERSE_REFRESH_RATE_UNIT = "universe.refresh.rate.unit";
    public static final String CONF_UNIVERSE_EXPANSION_STRATEGY = "universe.expansion.strategy";
    public static final String CONF_UNIVERSE_RULE_SET = "universe.rule.set";
    public static final String CONF_UNIVERSE_SPAWN_RATE = "universe.spawn.rate";
    public static final String CONF_SERVER_SAVE_RATE_VALUE = "server.save.rate.value";
    public static final String CONF_SERVER_SAVE_RATE_UNIT = "server.save.rate.unit";
    public static final String CONF_SERVER_BASE_DIR_PATH = "server.base.dir";

    public static final String CONF_CLIENT_SERVER_PORT = "server.client.port";
    public static final String CONF_ADMIN_SERVER_PORT = "server.admin.port";

    public static final List<String> CONF_KEYS = Arrays.asList(
            CONF_APPLICATION_MODE, CONF_APPLICATION_OUTPUT, CONF_PATTERN_FILE_PATH, CONF_UNIVERSE_WIDTH,
            CONF_UNIVERSE_HEIGHT, CONF_UNIVERSE_REFRESH_RATE_VALUE, CONF_UNIVERSE_REFRESH_RATE_UNIT,
            CONF_UNIVERSE_EXPANSION_STRATEGY, CONF_UNIVERSE_RULE_SET, CONF_UNIVERSE_SPAWN_RATE,
            CONF_SERVER_SAVE_RATE_VALUE , CONF_SERVER_SAVE_RATE_UNIT , CONF_SERVER_BASE_DIR_PATH ,
            CONF_CLIENT_SERVER_PORT);

    public static final String DEFAULT_APPLICATION_MODE = "BASIC";
    public static final String DEFAULT_APPLICATION_OUTPUT = "CONSOLE";
    public static final float DEFAULT_UNIVERSE_SPAWN_RATE = 0.15f;
    public static final String DEFAULT_UNIVERSE_RULE_SET = "ANKAMA";
    public static final long DEFAULT_UNIVERSE_REFRESH_RATE_VALUE = 5;
    public static final TimeUnit DEFAULT_UNIVERSE_REFRESH_RATE_UNIT = TimeUnit.SECONDS;
    public static final long DEFAULT_SERVER_SAVE_RATE_VALUE = 30;
    public static final TimeUnit DEFAULT_SERVER_SAVE_RATE_UNIT = TimeUnit.SECONDS;

    public static final int DEFAULT_CLIENT_SERVER_PORT = 7777;
    public static final int DEFAULT_ADMIN_SERVER_PORT = 8888;

    private Constants() {}
}
