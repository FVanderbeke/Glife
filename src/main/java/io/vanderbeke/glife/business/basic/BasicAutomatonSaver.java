package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.repository.AutomatonRepository;
import io.vanderbeke.glife.api.service.AutomatonSaver;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.core.PropertiesUtil;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Basic {@link AutomatonSaver} implementation. Using a scheduler to periodically schedule a new save.
 */
public class BasicAutomatonSaver implements AutomatonSaver {

    private final AutomatonRepository automatonRepository;
    private final AutomatonProcessor automatonProcessor;
    private final ScheduledExecutorService scheduler;
    private final long saveRateInMillis;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicReference<Automaton> refToSave = new AtomicReference<>(null);
    private final AtomicReference<ScheduledFuture<?>> scheduledTask = new AtomicReference<>(null);
    private final AtomicLong listenerId = new AtomicLong(-1);


    public BasicAutomatonSaver(AutomatonRepository automatonRepository, AutomatonProcessor automatonProcessor, Properties properties) {
        this(automatonRepository, automatonProcessor, Executors.newSingleThreadScheduledExecutor(), properties);
    }

    public BasicAutomatonSaver(AutomatonRepository automatonRepository, AutomatonProcessor automatonProcessor, ScheduledExecutorService scheduler, Properties properties) {
        this.automatonRepository = automatonRepository;
        this.automatonProcessor = automatonProcessor;
        this.scheduler = scheduler;
        long saveRateValue = PropertiesUtil.getLongProperty(properties, Constants.CONF_SERVER_SAVE_RATE_VALUE, Constants.DEFAULT_SERVER_SAVE_RATE_VALUE);
        TimeUnit saveRateUnit = PropertiesUtil.getTimeUnitProperty(properties, Constants.CONF_SERVER_SAVE_RATE_UNIT, Constants.DEFAULT_SERVER_SAVE_RATE_UNIT);

        this.saveRateInMillis = saveRateUnit.toMillis(saveRateValue);
    }

    @Override
    public void onNext(Automaton automaton) {
        refToSave.set(automaton);
    }

    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            this.save();
            this.listenerId.compareAndSet(-1, this.automatonProcessor.register(this));
            schedule();
        }
    }

    private void schedule() {
        scheduledTask.compareAndSet(null, scheduler.schedule(this::save, saveRateInMillis, TimeUnit.MILLISECONDS));
    }

    private void save() {
        scheduledTask.set(null);
        Automaton toSave = refToSave.getAndSet(null);
        try {
            if (Objects.nonNull(toSave)) {
                automatonRepository.save(toSave);
            }
        } finally {
            if (this.started.get()){
                this.schedule();
            }
        }
    }

    @Override
    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }

        listenerId.updateAndGet(value -> {
            if (value >= 0) {
                automatonProcessor.unregister(value, this);
            }
            return -1;
        });

        try {
            ScheduledFuture<?> task = scheduledTask.getAndSet(null);
            task.cancel(true);
        } catch (Exception e) {
            // Does nothing
        } finally {
            scheduler.shutdownNow();
        }
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public boolean isScheduled() {
        return Objects.nonNull(scheduledTask.get());
    }
}
