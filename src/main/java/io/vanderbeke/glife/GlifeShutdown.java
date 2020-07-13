package io.vanderbeke.glife;

import io.vanderbeke.glife.api.service.PropertyLoader;
import io.vanderbeke.glife.client.tcp.ShutdownCommandClient;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.core.PropertiesUtil;
import io.vanderbeke.glife.infrastructure.basic.BasicPropertyLoader;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.stream.Stream;

public class GlifeShutdown {

    public GlifeShutdown(String... args) {
        PropertyLoader loader = new BasicPropertyLoader();

        loader.load(args)
                .onSuccess(this::sendShutdown)
                .onFailure(this::printErrorAndExit);
    }

    private void printErrorAndExit(Throwable throwable) {
        throwable.printStackTrace();
        System.exit(0);
    }

    private void sendShutdown(Properties properties) {
        int serverPort = PropertiesUtil.getIntProperty(properties, Constants.CONF_ADMIN_SERVER_PORT, Constants.DEFAULT_ADMIN_SERVER_PORT);

        InetSocketAddress address = new InetSocketAddress(serverPort);

        try {
            new ShutdownCommandClient(address).execute();
        } catch (Exception e) {
            printErrorAndExit(e);
        }
    }

    private static void printHelp() {
        String title = "Syntax: shutdown [-admin-port=<VALUE>]\n" +
                "\nWhere VALUE is a TCP port for admin connection (integer; optional; default: 8888).\n";

        System.out.println(title);
    }

    public static void main(String[] args) {
        if (Stream.of(args).anyMatch(arg -> "-h".equals(arg.trim()))) {
            printHelp();
            return;
        }

        GlifeShutdown gof = new GlifeShutdown(args);
    }

}
