package io.vanderbeke.glife.api.service;

import io.vanderbeke.glife.api.model.Automaton;
import io.vavr.control.Try;

import java.io.OutputStream;

/**
 * Service that manages the universe rendering.
 */
public interface Renderer {
    /**
     * Renders a specific automaton in the given output stream.
     *
     * @param automaton Automaton state to serialize in the output stream.
     * @param stream Output stream.
     *
     * @return If success, the rendered automaton.
     */
    Try<Automaton> render(Automaton automaton, OutputStream stream);
}
