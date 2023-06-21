package ru.mrgrd56.mgutils.concurrent.execution.priority;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// https://stackoverflow.com/a/16577733/14899408

/**
 * @since 1.1
 */
public class PriorityFuture<T> implements RunnableFuture<T> {

    private final RunnableFuture<T> source;
    private final int priority;

    public PriorityFuture(RunnableFuture<T> source, int priority) {
        this.source = source;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return source.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return source.isCancelled();
    }

    @Override
    public boolean isDone() {
        return source.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return source.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return source.get();
    }

    @Override
    public void run() {
        source.run();
    }

    public static Comparator<Runnable> COMPARATOR = (Runnable o1, Runnable o2) -> {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            int p1 = ((PriorityFuture<?>) o1).getPriority();
            int p2 = ((PriorityFuture<?>) o2).getPriority();

            return Integer.compare(p1, p2);
        }
    };
}