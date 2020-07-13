package io.vanderbeke.glife;

import io.vanderbeke.glife.api.factory.AutomatonFactory;
import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.repository.RuleSetRepository;
import io.vanderbeke.glife.api.service.*;
import io.vanderbeke.glife.business.basic.*;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.infrastructure.basic.BasicAutomatonFactory;
import io.vanderbeke.glife.infrastructure.basic.BasicProvider;
import io.vanderbeke.glife.infrastructure.basic.BasicRenderer;
import io.vanderbeke.glife.infrastructure.basic.BasicRuleSetRepository;
import io.vavr.control.Try;
import org.junit.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class GlifeTest {
    private final Provider provider = new BasicProvider();
    private final RuleSetRepository ruleSetRepository = new BasicRuleSetRepository();
    private final CellStateComputer cellStateComputer = new BasicCellStateComputer(ruleSetRepository);

    private final NeighborhoodComputer circularNeighborhoodComputer = new CircularNeighborhoodComputer();
    private final NeighborhoodComputer fixedNeighborhoodComputer = new FixedNeighborhoodComputer();
    private final NeighborhoodComputers neighborhoodComputers = new NeighborhoodComputers(Arrays.asList(circularNeighborhoodComputer, fixedNeighborhoodComputer));

    private final AutomatonExecutor automatonExecutor = new BasicAutomatonExecutor(cellStateComputer, neighborhoodComputers);
    private final AutomatonFactory automatonFactory = new BasicAutomatonFactory();

    private final Renderer renderer = new BasicRenderer();

    @Test
    public void should_generate_new_universe_state() throws Exception {
        Path patternFile = Paths.get(this.getClass().getResource(".").toURI()).resolve("input-pattern.txt");

        Properties props = new Properties();

        props.setProperty(Constants.CONF_PATTERN_FILE_PATH, patternFile.toString());
        props.setProperty(Constants.CONF_UNIVERSE_WIDTH, "40");
        props.setProperty(Constants.CONF_UNIVERSE_HEIGHT, "20");
        props.setProperty(Constants.CONF_UNIVERSE_EXPANSION_STRATEGY, "FIXED");
        props.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_VALUE, "5");
        props.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_UNIT, TimeUnit.SECONDS.name());
        props.setProperty(Constants.CONF_UNIVERSE_RULE_SET, "CONWAY");

        Try<Automaton> rendered = provider.provide(props)
                .flatMap(automatonFactory::create)
                .flatMap(automaton -> renderer.render(automaton, System.out))
                .flatMap(automatonExecutor::performNext)
                .flatMap(automaton -> renderer.render(automaton, System.out))
                .flatMap(automatonExecutor::performNext)
                .flatMap(automaton -> renderer.render(automaton, System.out))
                .flatMap(automatonExecutor::performNext)
                .flatMap(automaton -> renderer.render(automaton, System.out));

        if (rendered.isFailure()) {
            throw new Exception(rendered.getCause());
        }
        assertThat(rendered.isSuccess()).isTrue();
    }
}
