package dev.b37.mgutils.concurrent.execution.cached;

import dev.b37.mgutils.delegate.DefaultRunnableFactory;
import dev.b37.mgutils.delegate.RunnableFactory;
import dev.b37.mgutils.logging.ScopedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @since 1.1
 */
public class CachedInvoker {
    private static final RunnableFactory DEFAULT_RUNNABLE_FACTORY = DefaultRunnableFactory.getInstance();

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ExecutorService executor;
    private final ConcurrentHashMap<Object, CachedInvocation<?>> invocations = new ConcurrentHashMap<>();

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

    @SuppressWarnings("unchecked")
    public <T> CachedInvocation<T> invoke(Object hash, Callable<T> supplier, RunnableFactory runnableFactory) {
        Logger logger = ScopedLogger.of(log, "CachedInvoker#invoke(" + hash + ")");

        return (CachedInvocation<T>) invocations.computeIfAbsent(hash, k -> {
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

            return new CachedInvocation<>(newFuture, () -> {
                invocations.remove(hash);
                logger.trace("invalidated the cached invocation");
            });
        });
    }
}
