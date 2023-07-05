package dev.b37.mgutils.concurrent.execution.priority;

import java.util.concurrent.*;

// based on https://stackoverflow.com/a/16577733/14899408

/**
 * @since 1.1
 */
public class PriorityExecutor extends ThreadPoolExecutor {
    private static final int QUEUE_DEFAULT_INITIAL_CAPACITY = 11;

    public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int queueInitialCapacity) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<>(queueInitialCapacity, PriorityFuture.COMPARATOR));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        RunnableFuture<T> newTask = super.newTaskFor(callable);

        final int priority;
        if (callable instanceof PriorityCallable<?>) {
            priority = ((PriorityCallable<T>) callable).getPriority();
        } else {
            priority = Integer.MAX_VALUE; // lowest priority by default
        }

        return new PriorityFuture<>(newTask, priority);
    }

    public static PriorityExecutor newFixedThreadPool(int nThreads) {
        return newFixedThreadPool(nThreads, QUEUE_DEFAULT_INITIAL_CAPACITY);
    }

    public static PriorityExecutor newFixedThreadPool(int nThreads, int queueInitialCapacity) {
        return new PriorityExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                queueInitialCapacity);
    }
}
