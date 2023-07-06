package dev.b37.mgutils.concurrent.execution.priority;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/**
 * @since 1.1
 */
public class PriorityThreadFactory implements ThreadFactory {
    private final int priority;

    public PriorityThreadFactory(int priority) {
        this.priority = priority;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(priority);
        return thread;
    }
}
