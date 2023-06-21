package ru.mrgrd56.mgutils.concurrent.execution.timeouted;

import java.util.concurrent.*;

/**
 * @since 1.1
 */
public class TimeoutedCallable<T> implements Callable<T> {
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final Callable<T> source;

    public TimeoutedCallable(long timeout, TimeUnit timeoutUnit, Callable<T> source) {
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.source = source;
    }

    @Override
    public T call() throws Exception {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return source.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return future.get(timeout, timeoutUnit);
    }

    public static boolean isTimeoutException(Exception e) {
        return e instanceof TimeoutException || (e instanceof ExecutionException && e.getCause() instanceof TimeoutException);
    }
}
