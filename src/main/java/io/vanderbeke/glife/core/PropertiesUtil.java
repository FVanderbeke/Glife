package io.vanderbeke.glife.core;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class PropertiesUtil {


    public static int getIntProperty(Properties props, String key, int defaultValue) {
        return Optional.ofNullable(props.get(key))
                .map(String.class::cast)
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }

    public static long getLongProperty(Properties props, String key, long defaultValue) {
        return Optional.ofNullable(props.get(key))
                .map(String.class::cast)
                .map(Long::parseLong)
                .orElse(defaultValue);
    }

    public static TimeUnit getTimeUnitProperty(Properties props, String key, TimeUnit defaultValue) {
        return Optional.ofNullable(props.get(key))
                .map(String.class::cast)
                .map(TimeUnit::valueOf)
                .orElse(defaultValue);
    }




    private PropertiesUtil() {}
}
