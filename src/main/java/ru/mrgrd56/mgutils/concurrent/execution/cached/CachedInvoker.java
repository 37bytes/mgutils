package ru.mrgrd56.mgutils.concurrent.execution.cached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mrgrd56.mgutils.delegate.DefaultRunnableFactory;
import ru.mrgrd56.mgutils.delegate.RunnableFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

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

    public <T> T invokeOnce(Object hash, Supplier<T> supplier) {
        return invokeOnce(hash, supplier, DEFAULT_RUNNABLE_FACTORY);
    }

    public <T> T invokeOnce(Object hash, Supplier<T> supplier, RunnableFactory runnableFactory) {
        try (CachedInvocation<T> invocation = invoke(hash, supplier, runnableFactory)) {
            return invocation.get();
        }
    }

    public <T> CompletableFuture<T> invokeOnceAsync(Object hash, Supplier<T> supplier) {
        return invokeOnceAsync(hash, supplier, DEFAULT_RUNNABLE_FACTORY);
    }

    public <T> CompletableFuture<T> invokeOnceAsync(Object hash, Supplier<T> supplier, RunnableFactory runnableFactory) {
        CachedInvocation<T> invocation = invoke(hash, supplier, runnableFactory);
        try {
            return invocation.future()
                    .thenApply(result -> {
                        invocation.close();
                        return result;
                    })
                    .exceptionally(e -> {
                        log.error("An error occurred in invokeOnceAsync", e);
                        invocation.close();
                        throw new RuntimeException(e);
                    });
        } catch (Exception e) {
            invocation.close();
            throw e;
        }
    }

    public <T> CachedInvocation<T> invoke(Object hash, Supplier<T> supplier) {
        return invoke(hash, supplier, DEFAULT_RUNNABLE_FACTORY);
    }

    public synchronized <T> CachedInvocation<T> invoke(Object hash, Supplier<T> supplier, RunnableFactory runnableFactory) {
        UUID callId = UUID.randomUUID();

        if (invocations.containsKey(hash)) {
            log.trace("[{}] CachedInvoker#invoke({}) using cached invocation", callId, hash);
            return (CachedInvocation<T>) invocations.get(hash);
        }

        log.trace("[{}] CachedInvoker#invoke({}) starting new invocation", callId, hash);

        CompletableFuture<T> newFuture = new CompletableFuture<T>();

        executor.submit(runnableFactory.create(() -> {
            log.trace("[{}] CachedInvoker#invoke({}) started new invocation", callId, hash);

            boolean isSuccess = true;

            try {
                T result = supplier.get();
                newFuture.complete(result);
            } catch (Throwable e) {
                isSuccess = false;
                newFuture.completeExceptionally(e);
            } finally {
                String successfulness = isSuccess ? "successfully" : "exceptionally";
                log.trace("[{}] CachedInvoker#invoke({}) {} finished the invocation", callId, hash, successfulness);
            }
        }));

        CachedInvocation<T> invocation = new CachedInvocation<T>(newFuture, () -> {
            invocations.remove(hash);
            log.trace("[{}] CachedInvoker#invoke({}) invalidated the cached invocation", callId, hash);
        });

        invocations.put(hash, invocation);

        return invocation;
    }
}
