package ru.mrgrd56.mgutils.concurrent.execution.cached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mrgrd56.mgutils.delegate.DefaultRunnableFactory;
import ru.mrgrd56.mgutils.delegate.RunnableFactory;
import ru.mrgrd56.mgutils.logging.ScopedLogger;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @since 1.1
 */
public class CachedInvoker {
    private static final RunnableFactory DEFAULT_RUNNABLE_FACTORY = DefaultRunnableFactory.getInstance();

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ExecutorService executor;
    private final Map<Object, CachedInvocation<?>> invocations = new ConcurrentHashMap<>();

    public CachedInvoker() {
        this(Executors.newCachedThreadPool());
    }

    public CachedInvoker(ExecutorService executor) {
        this.executor = executor;
    }

    public <T> T invokeOnce(Object hash, Callable<T> supplier) {
        return invokeOnce(hash, supplier, DEFAULT_RUNNABLE_FACTORY);
    }

    public <T> T invokeOnce(Object hash, Callable<T> supplier, RunnableFactory runnableFactory) {
        CachedInvocation<T> invocation = invoke(hash, supplier, runnableFactory);
        return invocation.getOnce();
    }

    public <T> CompletableFuture<T> invokeOnceAsync(Object hash, Callable<T> supplier) {
        return invokeOnceAsync(hash, supplier, DEFAULT_RUNNABLE_FACTORY);
    }

    public <T> CompletableFuture<T> invokeOnceAsync(Object hash, Callable<T> supplier, RunnableFactory runnableFactory) {
        CachedInvocation<T> invocation = invoke(hash, supplier, runnableFactory);
        return invocation.getOnceAsync();
    }

    public <T> CachedInvocation<T> invoke(Object hash, Callable<T> supplier) {
        return invoke(hash, supplier, DEFAULT_RUNNABLE_FACTORY);
    }

    public synchronized <T> CachedInvocation<T> invoke(Object hash, Callable<T> supplier, RunnableFactory runnableFactory) {
        Logger logger = ScopedLogger.of(log, "CachedInvoker#invoke(" + hash + ")");

        if (invocations.containsKey(hash)) {
            logger.trace("using cached invocation");
            return (CachedInvocation<T>) invocations.get(hash);
        }

        logger.trace("starting new invocation");

        CompletableFuture<T> newFuture = new CompletableFuture<T>();

        executor.submit(runnableFactory.create(() -> {
            logger.trace("started new invocation");

            boolean isSuccess = true;

            try {
                T result = supplier.call();
                newFuture.complete(result);
            } catch (Throwable e) {
                isSuccess = false;
                newFuture.completeExceptionally(e);
            } finally {
                String successfulness = isSuccess ? "successfully" : "exceptionally";
                logger.trace("{} finished the invocation", successfulness);
            }
        }));

        CachedInvocation<T> invocation = new CachedInvocation<T>(newFuture, () -> {
            invocations.remove(hash);
            logger.trace("invalidated the cached invocation");
        });

        invocations.put(hash, invocation);

        return invocation;
    }
}
