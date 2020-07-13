package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.repository.AutomatonRepository;
import io.vanderbeke.glife.core.Constants;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BasicAutomatonSaverTest {
    private Automaton automaton;
    private AutomatonRepository repository;
    private AutomatonProcessor processor;
    private ScheduledExecutorService executorService;
    private ScheduledFuture task;
    private Properties properties;
    private BasicAutomatonSaver saver;
    private final AtomicReference<Runnable> methodToCall = new AtomicReference<>();

    @Before
    public void before() {

        automaton = mock(Automaton.class);
        repository = mock(AutomatonRepository.class);
        when(repository.save(eq(automaton))).thenReturn(Try.success(automaton));
        processor = mock(AutomatonProcessor.class);
        executorService = mock(ScheduledExecutorService.class);
        task = mock(ScheduledFuture.class);


        when(executorService.schedule(isA(Runnable.class), eq(1000l), eq(TimeUnit.MILLISECONDS))).then((Answer<ScheduledFuture>) invocationOnMock -> {
            methodToCall.set(invocationOnMock.getArgumentAt(0, Runnable.class));
            return task;
        });
        when(processor.register(any())).thenReturn(10l);
         properties = new Properties();
        properties.setProperty(Constants.CONF_SERVER_SAVE_RATE_VALUE, "1");
        properties.setProperty(Constants.CONF_SERVER_SAVE_RATE_UNIT, "SECONDS");

        saver = new BasicAutomatonSaver(repository, processor, executorService, properties);
    }

    @Test
    public void should_start_saving() throws Exception {
        // GIVEN

        assertThat(saver.isScheduled()).isFalse();
        assertThat(saver.isStarted()).isFalse();
        // WHEN

        saver.start();
        saver.start();
        saver.onNext(automaton);
        methodToCall.get().run();
        saver.onNext(automaton);
        methodToCall.get().run();

        // THEN

        verify(processor, times(1)).register(eq(saver));
        verify(repository, times(2)).save(eq(automaton));
        assertThat(saver.isScheduled()).isTrue();
        assertThat(saver.isStarted()).isTrue();

    }

    @Test
    public void should_stop_saving() {

        // GIVEN

        assertThat(saver.isScheduled()).isFalse();
        assertThat(saver.isStarted()).isFalse();
        // WHEN

        saver.stop();
        saver.start();
        assertThat(saver.isScheduled()).isTrue();
        assertThat(saver.isStarted()).isTrue();
        saver.stop();
        saver.stop();

        // THEN

        verify(processor, times(1)).register(eq(saver));
        verify(processor, times(1)).unregister(eq(10l), eq(saver));

        verify(repository, times(0)).save(eq(automaton));

        assertThat(saver.isScheduled()).isFalse();
        assertThat(saver.isStarted()).isFalse();
    }

}
