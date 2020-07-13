package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.factory.AutomatonFactory;
import io.vanderbeke.glife.api.model.*;
import io.vanderbeke.glife.api.repository.AutomatonRepository;
import io.vanderbeke.glife.api.service.AutomatonExecutor;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.infrastructure.rng.RngProvider;
import io.vavr.control.Try;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BasicAutomatonProcessorTest {

    private final AutomatonExecutor automatonExecutor = mock(AutomatonExecutor.class);
    private final AutomatonFactory automatonFactory = mock(AutomatonFactory.class);
    private final AutomatonRepository automatonRepository = mock(AutomatonRepository.class);
    private final RngProvider provider = mock(RngProvider.class);
    private final ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);

    private BasicAutomatonProcessor processor = new BasicAutomatonProcessor(automatonExecutor, automatonFactory, automatonRepository, provider, scheduler);

    @Test
    public void should_initialize_with_existing_automaton() {
        // GIVEN

        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(scheduler.schedule(isA(Runnable.class), anyLong(), any())).thenReturn(future);

        Properties properties = new Properties();

        properties.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_UNIT, TimeUnit.MILLISECONDS.name());
        properties.setProperty(Constants.CONF_UNIVERSE_REFRESH_RATE_VALUE, "200");

        // WHEN
        assertThat(processor.isStarted()).isFalse();
        assertThat(processor.isInitializing()).isFalse();

        Try<Automaton> result = processor.initialize(properties);
        assertThat(processor.isStarted()).isTrue();
        assertThat(processor.isInitializing()).isTrue();

        // THEN
        assertThat(result.get()).isEqualTo(automaton);
        verify(provider, never()).provide(any());
        verify(automatonFactory, never()).create(any());
        verify(scheduler, only()).schedule(isA(Runnable.class), eq(200l), eq(TimeUnit.MILLISECONDS));

        verify(automatonRepository, never()).delete();
    }

    @Test
    public void should_initialize_with_random() {
        // GIVEN

        Configuration configuration = mock(Configuration.class);

        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        when(automatonRepository.find()).thenReturn(Try.success(Optional.empty()));

        when(provider.provide(any())).thenReturn(Try.success(configuration));
        when(automatonFactory.create(eq(configuration))).thenReturn(Try.success(automaton));

        Properties properties = new Properties();

        // WHEN
        assertThat(processor.isStarted()).isFalse();
        assertThat(processor.isInitializing()).isFalse();

        Try<Automaton> result = processor.initialize(properties);
        assertThat(processor.isStarted()).isTrue();
        assertThat(processor.isInitializing()).isTrue();

        // THEN
        assertThat(result.get()).isEqualTo(automaton);
        verify(provider, only()).provide(any());
        verify(automatonFactory, only()).create(any());

    }

    @Test
    public void should_register_unregister_listener() {
        AutomatonProcessor.Listener listener = mock(AutomatonProcessor.Listener.class);

        assertThat(processor.isRegistered(listener)).isFalse();
        long id = processor.register(listener);

        assertThat(processor.isRegistered(id)).isTrue();
        assertThat(processor.isRegistered(listener)).isTrue();

        assertThat(processor.unregister(id, listener)).isTrue();

        assertThat(processor.isRegistered(id)).isFalse();
        assertThat(processor.isRegistered(listener)).isFalse();

        assertThat(processor.unregister(id, listener)).isFalse();
    }

    @Test
    public void should_call_listeners() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        AutomatonProcessor.Listener listener = mock(AutomatonProcessor.Listener.class);

        Properties properties = new Properties();

        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        when(automatonExecutor.performNext(eq(automaton))).thenReturn(Try.success(automaton));

        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(scheduler.schedule(isA(Runnable.class), anyLong(), any())).thenReturn(future);

        // WHEN
        processor.initialize(properties);
        processor.register(listener);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler, only()).schedule(captor.capture(), anyLong(), any());
        captor.getValue().run();

        // THEN
        verify(listener, times(2)).onNext(eq(automaton));
    }

    @Test
    public void should_not_call_unregistered_listeners() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        AutomatonProcessor.Listener listener = mock(AutomatonProcessor.Listener.class);

        Properties properties = new Properties();

        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        when(automatonExecutor.performNext(eq(automaton))).thenReturn(Try.success(automaton));

        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(scheduler.schedule(isA(Runnable.class), anyLong(), any())).thenReturn(future);

        // WHEN
        processor.initialize(properties);
        long id = processor.register(listener);
        processor.unregister(id, listener);

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler, only()).schedule(captor.capture(), anyLong(), any());
        captor.getValue().run();

        verify(listener, times(1)).onNext(any());
    }

    @Test
    public void should_reset_automaton() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        when(automatonFactory.create(any())).thenReturn(Try.success(automaton));
        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(scheduler.schedule(isA(Runnable.class), anyLong(), any())).thenReturn(future);

        Configuration configuration = mock(Configuration.class);
        when(provider.provide(any())).thenReturn(Try.success(configuration));

        Properties properties = new Properties();

        // WHEN
        assertThat(processor.isStarted()).isFalse();
        assertThat(processor.isInitializing()).isFalse();

        processor.initialize(properties);
        processor.reset();

        // THEN
        assertThat(processor.isStarted()).isTrue();
        verify(scheduler, never()).shutdown();
        verify(provider, only()).provide(any());
        verify(automatonFactory, only()).create(any());

        verify(automatonRepository, times(1)).delete();
    }

    @Test
    public void should_stop_and_save_automaton_processing() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        when(automatonFactory.create(any())).thenReturn(Try.success(automaton));
        when(automatonRepository.find()).thenReturn(Try.success(Optional.of(automaton)));
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(scheduler.schedule(isA(Runnable.class), anyLong(), any())).thenReturn(future);

        Configuration configuration = mock(Configuration.class);
        when(provider.provide(any())).thenReturn(Try.success(configuration));

        Properties properties = new Properties();

        // WHEN
        processor.initialize(properties);
        processor.stop();

        // THEN
        assertThat(processor.isStarted()).isFalse();
        verify(scheduler, times(1)).shutdown();
        verify(future, only()).cancel(eq(true));
        verify(automatonRepository, times(1)).save(any());
    }
}
