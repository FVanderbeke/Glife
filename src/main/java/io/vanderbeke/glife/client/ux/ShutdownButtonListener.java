package io.vanderbeke.glife.client.ux;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class ShutdownButtonListener implements ActionListener {
    private static final Logger LOGGER = Logger.getLogger(ShutdownButtonListener.class.getName());

    private final UxClient owner;

    public ShutdownButtonListener(UxClient owner) {
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.fine("Shutting down....");
        owner.close();
    }
}
