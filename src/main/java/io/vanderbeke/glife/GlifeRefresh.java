package io.vanderbeke.glife;

import io.vanderbeke.glife.api.service.PropertyLoader;
import io.vanderbeke.glife.client.tcp.RefreshCommandClient;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.core.PropertiesUtil;
import io.vanderbeke.glife.infrastructure.basic.BasicPropertyLoader;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.stream.Stream;

public class GlifeRefresh {

    public GlifeRefresh(String... args) {
        PropertyLoader loader = new BasicPropertyLoader();

        loader.load(args)
                .onSuccess(this::sendRefresh)
                .onFailure(this::printErrorAndExit);
    }

    private void printErrorAndExit(Throwable throwable) {
        throwable.printStackTrace();
        System.exit(0);
    }

    private void sendRefresh(Properties properties) {
        int serverPort = PropertiesUtil.getIntProperty(properties, Constants.CONF_ADMIN_SERVER_PORT, Constants.DEFAULT_ADMIN_SERVER_PORT);

        InetSocketAddress address = new InetSocketAddress(serverPort);

        try {
            new RefreshCommandClient(address).execute();
        } catch (Exception e) {
            printErrorAndExit(e);
        }
    }

    private static void printHelp() {
        String title = "Syntax: refresh [-admin-port=<VALUE>]\n" +
                "\nWhere VALUE is a TCP port for admin connection (integer; optional; default: 8888).\n";

        System.out.println(title);
    }

    public static void main(String[] args) {
        if (Stream.of(args).anyMatch(arg -> "-h".equals(arg.trim()))) {
            printHelp();
            return;
        }

        GlifeRefresh gof = new GlifeRefresh(args);
    }
}
