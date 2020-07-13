package io.vanderbeke.glife.client.ux;

import io.vanderbeke.glife.api.model.AutomatonProcessor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class RefreshButtonListener implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(RefreshButtonListener.class.getName());

    private final AutomatonProcessor processor;

    public RefreshButtonListener(AutomatonProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.fine("refreshing....");
        processor.reset();
    }
}
