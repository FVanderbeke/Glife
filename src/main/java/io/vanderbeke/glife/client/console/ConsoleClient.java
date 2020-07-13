package io.vanderbeke.glife.client.console;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.service.AutomatonSaver;
import io.vanderbeke.glife.api.service.Renderer;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class ConsoleClient implements Runnable, AutomatonProcessor.Listener {

    private final Scanner reader;
    private final BufferedWriter writer;
    private final OutputStream originalOutputStream;

    private final AutomatonProcessor automatonProcessor;
    private final AutomatonSaver automatonSaver;
    private final Renderer renderer;

    private final AtomicLong listenerId = new AtomicLong(-1);

    public ConsoleClient(InputStream source, OutputStream out, AutomatonProcessor automatonProcessor, AutomatonSaver automatonSaver, Renderer renderer) {
        this.reader = new Scanner(source);
        this.originalOutputStream = out;
        this.writer = new BufferedWriter(new OutputStreamWriter(out));
        this.automatonProcessor = automatonProcessor;
        this.automatonSaver = automatonSaver;
        this.renderer = renderer;
    }

    private void writeMenu() throws IOException {
        String prompt = "Available actions:\n" +
                "\t- shutdown : stops the game\n" +
                "\t- reset: resets the game's universe\n" +
                "\t- display_on: to show the universe generation evolution\n" +
                "\t- display_off: to stop the display of the universe generation evolution\n" +
                "Action: ";

        writer.write(prompt);
        writer.flush();
    }

    public void run() {
        boolean readNext = true;

        while(readNext) {
            try {
                writeMenu();
                readNext = executeCommand();
            } catch (Exception e) {
                readNext = false;
            }
        }
    }

    private void printErrorAndExit(Throwable error) {
        System.err.println("An error occurred.");
        error.printStackTrace();
        shutdown();
    }

    private void shutdown() {
        try {
            automatonSaver.stop();
            automatonProcessor.stop();
            writer.write("Bye.\n");
        } catch (Exception e) {
            // Does nothing.
        } finally {
            System.exit(0);
        }
    }

    private boolean executeCommand() throws IOException {
        String command = reader.next();

        if (Objects.isNull(command)) {
            return false;
        } else if ("shutdown".equals(command)) {
            shutdown();
        } else if (("reset").equals(command)) {
            automatonProcessor.reset().onFailure(this::printErrorAndExit);
        } else if (("display_on").equals(command)) {
            listenerId.updateAndGet(value -> {
                if (value != -1) {
                    return value;
                }
                return automatonProcessor.register(this);
            });
        } else if (("display_off").equals(command)) {
            listenerId.updateAndGet(value -> {
                if (value == -1) {
                    return value;
                }
                automatonProcessor.unregister(value, this);
                return -1;
            });
        } else {
            writer.write("Unknown command: '" + command + "'.\n");
        }
        return true;
    }

    @Override
    public void onNext(Automaton automaton) {
        renderer.render(automaton, originalOutputStream).onFailure(this::printErrorAndExit);
    }
}