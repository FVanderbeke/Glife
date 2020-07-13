package io.vanderbeke.glife.business.basic;

import io.vanderbeke.glife.api.factory.AutomatonFactory;
import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.repository.AutomatonRepository;
import io.vanderbeke.glife.api.service.AutomatonExecutor;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.core.PropertiesUtil;
import io.vanderbeke.glife.infrastructure.rng.RngProvider;
import io.vavr.control.Try;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic implementation of the main processor.
 *
 * Uses the {@link AutomatonExecutor} instance to periodically generate a new state.
 *
 * Each new state is notified to the registered listeners.
 */
public class BasicAutomatonProcessor implements AutomatonProcessor {

    private static final Logger LOGGER = Logger.getLogger(BasicAutomatonProcessor.class.getName());

    private final AutomatonExecutor automatonExecutor;
    private final AutomatonFactory automatonFactory;
    private final AutomatonRepository automatonRepository;

    private final RngProvider rngProvider;
    private final AtomicBoolean initializing = new AtomicBoolean(false);
    private final AtomicReference<Properties> propertiesRef = new AtomicReference<>(new Properties());
    private final AtomicBoolean started = new AtomicBoolean(false);

    private final AtomicLong listenerIdSeq = new AtomicLong(0);
    private final ConcurrentMap<Long, Listener> listeners = new ConcurrentHashMap<>(10, 0.9f, Runtime.getRuntime().availableProcessors());
    private final ScheduledExecutorService scheduler;

    private final AtomicReference<Automaton> currentGenerationRef = new AtomicReference<>(Automaton.notStarted());
    private final AtomicReference<ScheduledFuture<?>> nextGenerationTaskRef= new AtomicReference<>(null);
    private final AtomicLong refreshValueInMillis = new AtomicLong(0);

    public BasicAutomatonProcessor(AutomatonExecutor automatonExecutor, AutomatonFactory automatonFactory, AutomatonRepository automatonRepository, RngProvider rngProvider, ScheduledExecutorService scheduler) {
        this.automatonExecutor = automatonExecutor;
        this.automatonFactory = automatonFactory;
        this.automatonRepository = automatonRepository;
        this.rngProvider = rngProvider;
        this.scheduler = scheduler;
    }

    public BasicAutomatonProcessor(AutomatonExecutor automatonExecutor, AutomatonFactory automatonFactory, AutomatonRepository automatonRepository, RngProvider rngProvider) {
        this(automatonExecutor, automatonFactory, automatonRepository, rngProvider, Executors.newSingleThreadScheduledExecutor());
    }

    private Try<Automaton> loadOrCreate(Optional<Automaton> automatonOpt, Properties properties) {
        return automatonOpt.map(Try::success)
                .orElseGet(() -> rngProvider.provide(properties).flatMap(automatonFactory::create));
    }

    private long getRefreshRateInMillis(Properties properties) {
        TimeUnit refreshTimeUnit = PropertiesUtil.getTimeUnitProperty(properties, Constants.CONF_UNIVERSE_REFRESH_RATE_UNIT, Constants.DEFAULT_UNIVERSE_REFRESH_RATE_UNIT);
        long refreshValue = PropertiesUtil.getLongProperty(properties, Constants.CONF_UNIVERSE_REFRESH_RATE_VALUE, Constants.DEFAULT_UNIVERSE_REFRESH_RATE_VALUE);
        return refreshTimeUnit.toMillis(refreshValue);
    }

    @Override
    public Try<Automaton> initialize(Properties properties) {
        if (!initializing.compareAndSet(false, true)) {
            return Optional.ofNullable(currentGenerationRef.get())
                    .map(Try::success)
                    .orElseGet(() -> Try.success(Automaton.notStarted()));
        }

        Try<Automaton> result = automatonRepository.find()
                .flatMap(opt -> loadOrCreate(opt, properties))
                .peek(__ -> propertiesRef.set(properties))
                .peek(currentGenerationRef::set);

        refreshValueInMillis.set(getRefreshRateInMillis(properties));

        started.compareAndSet(false, result.isSuccess());

        scheduleGenerations();

        return result;
    }

    private void scheduleGenerations() {
        if (!started.get() || currentGenerationRef.get().state().universe().isEmpty()) {
            return;
        }

        nextGenerationTaskRef.set(scheduler.schedule(this::next, refreshValueInMillis.get(), TimeUnit.MILLISECONDS));
    }

    private void next() {
        if (!started.get() || currentGenerationRef.get().state().universe().isEmpty()) {
            return;
        }

        Try<Automaton> next = automatonExecutor.performNext(currentGenerationRef.get())
                .peek(currentGenerationRef::set);

        if (next.isFailure()) {
            LOGGER.log(Level.SEVERE, "An error occurred during automaton generation.", next.getCause());
            next.getCause().printStackTrace();
        } else {
           notifyAllListener();
        }

        scheduleGenerations();
    }

    @Override
    public Try<Automaton> stop() {
        if (!started.compareAndSet(true, false)) {
            return Try.success(Automaton.notStarted());
        }

        try {
            scheduler.shutdown();
            ScheduledFuture<?> task = nextGenerationTaskRef.get();
            if (Objects.nonNull(task)) {
                task.cancel(true);
            }
        } catch (Exception e) {
            // Does nothing.
        }

        propertiesRef.set(new Properties());

        listeners.clear();

        Automaton lastGeneration = currentGenerationRef.getAndSet(Automaton.notStarted());

        if (Objects.isNull(lastGeneration) || lastGeneration.isNotStarted()) {
            return Try.success(Automaton.notStarted());
        }

        return automatonRepository.save(lastGeneration);
    }

    @Override
    public Try<Automaton> reset() {
        LOGGER.log(Level.INFO, "resetting automaton...");
        if (!started.get() || currentGenerationRef.get().isNotStarted()) {
            return Try.success(Automaton.notStarted());
        }

        automatonRepository.delete();

        return rngProvider.provide(propertiesRef.get())
                .flatMap(automatonFactory::create)
                .peek(currentGenerationRef::set)
                .peek(automaton -> LOGGER.log(Level.INFO, "Automaton reset."))
                .peek(automaton -> scheduler.execute(() -> notifyAllListener()));
    }

    private void notifyAllListener() {
        Automaton currentState = currentGenerationRef.get();
        if (!isStarted() || currentState.isNotStarted()) {
            return;
        }

        try {
            // notify all listeners.
            listeners.values()
                    .parallelStream()
                    .forEach(listener -> listener.onNext(currentState));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception occurred when trying to notify listeners from ");
        }
    }

    @Override
    public boolean isInitializing() {
        return initializing.get();
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public Optional<Automaton> currentGeneration() {
        if (!isStarted()) {
            return Optional.of(Automaton.notStarted());
        }
        return Optional.ofNullable(currentGenerationRef.get());
    }

    @Override
    public long register(Listener onNext) {
        long id = listenerIdSeq.incrementAndGet();

        listeners.put(id, onNext);

        if (!isStarted()) {
            return id;
        }

        Automaton currentGeneration = currentGenerationRef.get();

        if (Objects.nonNull(currentGeneration) && !currentGeneration.isNotStarted()) {
            onNext.onNext(currentGeneration);
        }

        return id;
    }

    @Override
    public boolean unregister(long registerId, Listener listener) {
        return listeners.remove(registerId, listener);
    }

    @Override
    public long refreshRate(TimeUnit unit) {
        return unit.convert(refreshValueInMillis.get(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isRegistered(Listener listener) {
        return listeners.containsValue(listener);
    }

    @Override
    public boolean isRegistered(long listenerId) {
        return listeners.containsKey(listenerId);
    }
}
