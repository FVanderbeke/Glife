package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.service.PropertyLoader;
import io.vanderbeke.glife.core.Constants;
import io.vavr.control.Try;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BasicPropertyLoader implements PropertyLoader {
    private static final Logger LOGGER = Logger.getLogger(BasicPropertyLoader.class.getName());

    public static final String CONFIG_FILE_NAME = "config.properties";

    private final Path appBaseDir;

    public BasicPropertyLoader() {
        String definedDir = Optional.of(Constants.CONF_SERVER_BASE_DIR_PATH)
                .map(System::getProperty)
                .filter(val -> !"".equals(val.trim()))
                .orElseGet(() -> System.getProperty("user.dir"));

        appBaseDir = Paths.get(definedDir);

        if (!appBaseDir.toFile().exists() || !appBaseDir.toFile().isDirectory()) {
            LOGGER.log(Level.WARNING, "No '" + CONFIG_FILE_NAME + "' file found in '" + definedDir +"' application home directory.");
        }
    }

    private Try<Properties> addConfigFile(Properties properties) {

        File appProperties = appBaseDir.resolve(CONFIG_FILE_NAME).toFile();

        if (!appProperties.exists()) {
            return Try.success(properties);
        }

        return Try.of(() -> {
            try (FileReader reader = new FileReader(appProperties)) {
                properties.load(reader);
                return properties;
            }
        });
    }

    private Properties addCommand(Properties properties, List<String> args) {

        for (String arg : args) {
            if (arg == null || "".equals(arg.trim()) || arg.startsWith("-JVMD=")) {
                continue;
            } else if (arg.startsWith("-width=")) {
                properties.setProperty(Constants.CONF_UNIVERSE_WIDTH, cleanArg(arg.substring("-width=".length())));
            } else if (arg.startsWith("-height=")) {
                properties.setProperty(Constants.CONF_UNIVERSE_HEIGHT, cleanArg(arg.substring("-height=".length())));
            } else if (arg.startsWith("-spawn=")) {
                properties.setProperty(Constants.CONF_UNIVERSE_SPAWN_RATE, cleanArg(arg.substring("-spawn=".length())));
            } else if (arg.startsWith("-refresh=")) {
                properties.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_VALUE, cleanArg(arg.substring("-refresh=".length())));
            } else if (arg.startsWith("-mode=")) {
                properties.setProperty(Constants.CONF_APPLICATION_MODE, cleanArg(arg.substring("-mode=".length()).toUpperCase()));
            } else if (arg.startsWith("-out=")) {
                properties.setProperty(Constants.CONF_APPLICATION_OUTPUT, cleanArg(arg.substring("-out=".length()).toUpperCase()));
            } else if (arg.startsWith("-port=")) {
                properties.setProperty(Constants.CONF_CLIENT_SERVER_PORT, cleanArg(arg.substring("-port=".length()).toUpperCase()));
            } else if (arg.startsWith("-admin-port=")) {
                properties.setProperty(Constants.CONF_ADMIN_SERVER_PORT, cleanArg(arg.substring("-admin-port=".length()).toUpperCase()));
            } else {
                throw new IllegalArgumentException("Unknown command line parameter: " + arg);
            }
        }

        return properties;
    }

    private Properties addSystemPropertyCommand(Properties properties, List<String> args) {

        for (String arg : args) {
            if (arg == null || "".equals(arg.trim()) || !arg.startsWith("-JVMD=")) {
                continue;
            }
            String value = arg.substring("-JVMD=".length());
            String[] conf = value.split(":");

            if (!Constants.CONF_KEYS.contains(conf[0])) {
                continue;
            }

            properties.setProperty(conf[0], cleanArg(conf[1]));
        }

        return properties;
    }

    private void addSystemProperty(String key, Properties list) {
        String value = System.getProperty(key);
        if (Objects.nonNull(value) && !"".equals(value.trim())) {
            list.setProperty(key, value);
        }
    }

    private String cleanArg(String arg) {
        if (arg.length() > 1 && arg.startsWith("'") && arg.endsWith("'")) {
            return arg.substring(1, arg.length() - 1);
        }
        if (arg.length() > 1 && arg.startsWith("\"") && arg.endsWith("\"")) {
            return arg.substring(1, arg.length() - 1);
        }

        return arg;
    }

    @Override
    public Try<Properties> load(String... args) {
        List<String> cleanArgs = Stream.of(args)
                .map(arg -> arg.trim())
                .map(this::cleanArg)
                .filter(arg -> !"".equals(arg.trim()))
                .distinct()
                .collect(Collectors.toList());

        final Properties properties = new Properties();

        // Default settings
        properties.setProperty(Constants.CONF_APPLICATION_MODE, Constants.DEFAULT_APPLICATION_MODE);
        properties.setProperty(Constants.CONF_APPLICATION_OUTPUT, Constants.DEFAULT_APPLICATION_OUTPUT);
        properties.setProperty(Constants.CONF_UNIVERSE_SPAWN_RATE, Constants.DEFAULT_UNIVERSE_SPAWN_RATE + "");
        properties.setProperty(Constants.CONF_UNIVERSE_RULE_SET, Constants.DEFAULT_UNIVERSE_RULE_SET);
        properties.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_VALUE, Constants.DEFAULT_UNIVERSE_REFRESH_RATE_VALUE + "");
        properties.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_UNIT, Constants.DEFAULT_UNIVERSE_REFRESH_RATE_UNIT.name());

        properties.setProperty(Constants.CONF_SERVER_SAVE_RATE_VALUE, Constants.DEFAULT_SERVER_SAVE_RATE_VALUE + "");
        properties.setProperty(Constants.CONF_SERVER_SAVE_RATE_UNIT, Constants.DEFAULT_SERVER_SAVE_RATE_UNIT.name());

        properties.setProperty(Constants.CONF_CLIENT_SERVER_PORT, Constants.DEFAULT_CLIENT_SERVER_PORT + "");
        properties.setProperty(Constants.CONF_SERVER_SAVE_RATE_VALUE, Constants.DEFAULT_SERVER_SAVE_RATE_VALUE + "");
        properties.setProperty(Constants.CONF_ADMIN_SERVER_PORT, Constants.DEFAULT_ADMIN_SERVER_PORT + "");

        // First system properties
        Constants.CONF_KEYS.forEach(key -> addSystemProperty(key, properties));

        // Overriden by the config file.
        // Overriden by the command
        return addConfigFile(properties)
                .map(props -> addSystemPropertyCommand(props, cleanArgs))
                .map(props -> addCommand(props, cleanArgs));

    }
}
