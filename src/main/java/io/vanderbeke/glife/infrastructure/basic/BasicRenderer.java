package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.service.Renderer;
import io.vavr.control.Try;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicRenderer implements Renderer {

    @Override
    public Try<Automaton> render(Automaton automaton, OutputStream stream) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            Try<Automaton> result = render(automaton, writer);
            writer.flush();

            return result;
        } catch (Exception e) {
            return Try.failure(e);
        }
    }

    private void addLastCells(StringBuilder builder, AtomicInteger currentIndex, int lineLength, int size) {
        if (currentIndex.get() >= size) {
            return;
        }

        int minIndex = currentIndex.get();
        int maxIndex = size - 1;

        for (int i = minIndex; i <= maxIndex; i++) {
            builder.append('.');
            if (currentIndex.incrementAndGet() % lineLength == 0) {
                builder.append(getSeparator());
            }
        }
    }

    public String getSeparator() {
        return "\n";
    }

    private void addCells(StringBuilder builder, int nextAliveIndex, AtomicInteger currentIndex, int lineLength, int size) {
        if (currentIndex.get() >= size) {
            return;
        }

        for (int i = currentIndex.get(); i < nextAliveIndex; i++) {
            builder.append('.');
            if (currentIndex.incrementAndGet() % lineLength == 0) {
                builder.append(getSeparator());
            }
        }

        if (currentIndex.get() >= size) {
            return;
        }

        builder.append('*');

        if (currentIndex.incrementAndGet() % lineLength == 0) {
            builder.append(getSeparator());
        }
    }

    private Try<Automaton> render(Automaton automaton, BufferedWriter writer) throws Exception {
        int width = automaton.state().universe().width();
        int height = automaton.state().universe().height();
        int size = width * height;

        writer.write(width + " " + height + getSeparator());

        if (width * height == 0) {
            writer.write(getSeparator());
            return Try.success(automaton);
        }

        AtomicInteger currentCursor = new AtomicInteger(0);
        StringBuilder builder = new StringBuilder("");

        automaton.state().universe().aliveCells()
                .forEach(cellIndex -> addCells(builder, cellIndex, currentCursor, width, size));

        addLastCells(builder, currentCursor, width, size);

        writer.write(builder.toString());

        return Try.success(automaton);
    }
}
