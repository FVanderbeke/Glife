package io.vanderbeke.glife.client.tcp;

import java.net.InetSocketAddress;

public class ShutdownCommandClient  extends AbstractCommandClient {

    public ShutdownCommandClient(InetSocketAddress serverAddress) throws Exception {
        super(serverAddress);
    }

    @Override
    protected String getMssage() {
        return "2";
    }
}