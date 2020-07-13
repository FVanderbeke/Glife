package io.vanderbeke.glife.client.tcp;

import java.net.InetSocketAddress;

public class RefreshCommandClient extends AbstractCommandClient {

    public RefreshCommandClient(InetSocketAddress serverAddress) throws Exception {
        super(serverAddress);
    }

    @Override
    protected String getMssage() {
        return "1";
    }
}
