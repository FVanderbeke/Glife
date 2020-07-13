package io.vanderbeke.glife.infrastructure.tcp;

import io.vanderbeke.glife.infrastructure.basic.BasicRenderer;

public class TcpRenderer extends BasicRenderer {
    @Override
    public String getSeparator() {
        return "\r\n";
    }
}
