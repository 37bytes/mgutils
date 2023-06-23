package ru.mrgrd56.mgutils.concurrent.execution.cached;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @since 1.1
 */
public class CachedInvocation<T> implements AutoCloseable {
    private final CompletableFuture<T> future;
    private final Runnable invalidate;

    private final AtomicBoolean isInvalidated = new AtomicBoolean(false);

    public CachedInvocation(CompletableFuture<T> future, Runnable invalidate) {
        this.future = future;
        this.invalidate = invalidate;
    }

    public CompletableFuture<T> future() {
        return future;
    }

    public T get() {
        try {
            return future().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @since 1.7.0
     */
    public T getOnce() {
        try {
            return get();
        } finally {
            close();
        }
    }

    /**
     * @since 1.7.0
     */
    public CompletableFuture<T> getOnceAsync() {
        try {
            return future().whenComplete((result, e) -> close());
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    @Override
    public void close() {
        // if isInvalidated is false then set it to true and invalidate.run()
        if (isInvalidated.compareAndSet(false, true)) {
            invalidate.run();
        }
    }
}
