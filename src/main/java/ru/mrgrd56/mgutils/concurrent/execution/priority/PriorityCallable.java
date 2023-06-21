package ru.mrgrd56.mgutils.concurrent.execution.priority;

import java.util.concurrent.Callable;

// based on https://stackoverflow.com/a/16577733/14899408

/**
 * @since 1.1
 */
public interface PriorityCallable<T> extends Callable<T> {
    int getPriority();

    static <T> PriorityCallable<T> of(int priority, Callable<T> action) {
        return new PriorityCallableImpl<>(priority, action);
    }
}