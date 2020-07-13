package io.vanderbeke.glife.client.ux;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.service.AutomatonSaver;
import io.vanderbeke.glife.api.service.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class UxClient implements AutomatonProcessor.Listener {

    private static final Logger LOGGER = Logger.getLogger(UxClient.class.getName());

    private final AutomatonProcessor automatonProcessor;
    private final AutomatonSaver automatonSaver;
    private final Renderer renderer;

    private final AtomicLong listenerId = new AtomicLong(-1);

    private final JFrame mainFrame;
    private final JPanel mainContainer;
    private final JPanel header;
    private final JPanel center;
    private final JButton refreshButton;
    private final JButton shutdownButton;
    private final JEditorPane automatonView;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public UxClient(AutomatonProcessor automatonProcessor, AutomatonSaver automatonSaver, Renderer renderer) {
        this.automatonProcessor = automatonProcessor;
        this.automatonSaver = automatonSaver;
        this.renderer = renderer;
        this.mainFrame = new JFrame();
        this.mainContainer = new JPanel(new BorderLayout());
        this.header = new JPanel();
        this.center = new JPanel();
        this.refreshButton = new JButton("Refresh");
        this.shutdownButton = new JButton("Shutdown");
        this.automatonView = new JEditorPane();
    }

    public void show() {
        LOGGER.info("Showing ux");
        mainFrame.setSize(new Dimension(800, 600));
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        refreshButton.addActionListener(new RefreshButtonListener(automatonProcessor));
        shutdownButton.addActionListener(new ShutdownButtonListener(this));

        header.add(refreshButton);
        header.add(shutdownButton);
        automatonView.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(automatonView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(750, 500));

        automatonView.setPreferredSize(new Dimension(750, 500));
        automatonView.setFont(new Font("courier", Font.PLAIN, 16));
        center.add(scrollPane);

        mainContainer.add(header, BorderLayout.NORTH);
        mainContainer.add(center, BorderLayout.CENTER);

        mainFrame.add(mainContainer);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        mainFrame.setVisible(true);

        listenerId.set(this.automatonProcessor.register(this));
    }

    public void setTextFrom(Automaton automaton) {
        final StringBuilder content = new StringBuilder("");
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            renderer.render(automaton, out)
                    .onSuccess(__ -> content.append(new String(out.toByteArray())))
                    .onFailure(this::printErrorAndExit);
        } catch (Exception e) {
            printErrorAndExit(e);
        }
        automatonView.setText(content.toString());
    }

    private void printErrorAndExit(Throwable error) {
        error.printStackTrace();
        close();
    }

    public void close() {
        try {
            automatonProcessor.unregister(listenerId.get(), this);
            automatonSaver.stop();
            automatonProcessor.stop();
        } catch (Exception e) {
            // Does nothing
        } finally {
            System.out.println("Bye.");
            System.exit(0);
        }
    }

    @Override
    public void onNext(Automaton automaton) {
        executor.execute(() -> setTextFrom(automaton));
    }
}
