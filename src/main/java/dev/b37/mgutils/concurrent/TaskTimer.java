package dev.b37.mgutils.concurrent;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @since 1.1
 */
public class TaskTimer {
    private final ExecutorService executor;

    private final AtomicBoolean isStoppedRef = new AtomicBoolean(false);
    private final Future<?> taskFuture;
    private final Runnable task;

    public TaskTimer(Runnable task, int intermediateDelay, int initialDelay) {
        this(task, intermediateDelay, initialDelay, Executors.newCachedThreadPool());
    }

    public TaskTimer(Runnable task, int intermediateDelay, int initialDelay, ExecutorService executor) {
        this.executor = executor;
        this.task = task;
        taskFuture = executor.submit(() -> {
            if (isStoppedRef.get()) return;

            try {
                Thread.sleep(initialDelay);
            } catch (InterruptedException e) {
                return;
            }

            while (true) {
                if (isStoppedRef.get()) return;

                task.run();

                if (isStoppedRef.get()) return;

                try {
                    Thread.sleep(intermediateDelay);
                } catch (InterruptedException e) {
                    return;
                }

                if (isStoppedRef.get()) return;
            }
        });
    }

    public void stop() {
        isStoppedRef.set(true);
        taskFuture.cancel(true);
    }

    public void invokeImmediately() {
        executor.submit(this.task);
    }

    public static void invokeImmediately(@Nullable TaskTimer taskTimer) {
        if (taskTimer != null) {
            taskTimer.invokeImmediately();
        }
    }
}
