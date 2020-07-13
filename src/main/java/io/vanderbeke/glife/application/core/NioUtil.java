package io.vanderbeke.glife.application.core;

import io.vanderbeke.glife.core.PropertiesUtil;

import java.net.InetSocketAddress;
import java.nio.channels.CompletionHandler;
import java.util.Properties;
import java.util.function.Consumer;

public final class NioUtil {

    public static <V, A>CompletionHandler<V, A> toHandler(Consumer<V> onResult) {
        return toHandler(onResult, error -> {});
    }

    public static <V, A>CompletionHandler<V, A> toHandler(Consumer<V> onResult, Consumer<Throwable> onError) {
        return new CompletionHandler<V, A>() {
            @Override
            public void completed(V result, A attachment) {
                onResult.accept(result);
            }

            @Override
            public void failed(Throwable error, A attachment) {
                onError.accept(error);
            }
        };
    }

    public static InetSocketAddress socketAddress(Properties props, String portKey, int defaultPort) {
        return new InetSocketAddress(PropertiesUtil.getIntProperty(props, portKey, defaultPort));
    }

    private NioUtil() {}
}
