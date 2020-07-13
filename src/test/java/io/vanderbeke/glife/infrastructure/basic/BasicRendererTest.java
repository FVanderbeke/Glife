package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.RuleSet;
import io.vanderbeke.glife.business.basic.BasicAutomaton;
import io.vanderbeke.glife.business.basic.BasicAutomatonState;
import io.vanderbeke.glife.business.basic.BasicUniverse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicRendererTest {

    @Test
    public void should_renderer_space() throws Exception {
        // GIVEN
        String expected = "10 10\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "....**....\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(10, 10, ExpansionStrategy.CIRCULAR, new int[] {34,35}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }

    @Test
    public void should_renderer_start() throws Exception {
        // GIVEN
        String expected = "10 10\n" +
                "*.........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(10, 10, ExpansionStrategy.CIRCULAR, new int[] {0}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }

    @Test
    public void should_renderer_end() throws Exception {
        // GIVEN
        String expected = "10 10\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                ".........*\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(10, 10, ExpansionStrategy.CIRCULAR, new int[] {99}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }


    @Test
    public void should_renderer_empty() throws Exception {
        // GIVEN
        String expected = "0 0\n" +
                "\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(0, 0, ExpansionStrategy.CIRCULAR, new int[] {}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }

    @Test
    public void should_renderer_one_alive() throws Exception {
        // GIVEN
        String expected = "1 1\n" +
                "*\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(1, 1, ExpansionStrategy.CIRCULAR, new int[] {0}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }

    @Test
    public void should_renderer_one_dead() throws Exception {
        // GIVEN
        String expected = "1 1\n" +
                ".\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(1, 1, ExpansionStrategy.CIRCULAR, new int[] {}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }

    @Test
    public void should_renderer_one_line() throws Exception {
        // GIVEN
        String expected = "1 3\n" +
                ".\n" +
                ".\n" +
                ".\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(1, 3, ExpansionStrategy.CIRCULAR, new int[] {}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }

    @Test
    public void should_renderer_one_row() throws Exception {
        // GIVEN
        String expected = "3 1\n" +
                "***\n";
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), new BasicAutomatonState(0, new BasicUniverse(3, 1, ExpansionStrategy.CIRCULAR, new int[] {0,1,2}, RuleSet.Basic.CONWAY.id())));
        BasicRenderer renderer = new BasicRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WHEN
        renderer.render(automaton, out);

        // THEN
        assertThat(out.toString()).isEqualTo(expected);

        out.close();
    }
}
