package dev.b37.mgutils.concurrent.execution.priority;

import java.util.concurrent.Callable;

/**
 * @since 1.1
 */
class PriorityCallableImpl<T> implements PriorityCallable<T> {
    private final int priority;
    private final Callable<T> source;

    public PriorityCallableImpl(int priority, Callable<T> source) {
        this.priority = priority;
        this.source = source;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public T call() throws Exception {
        return source.call();
    }
}
