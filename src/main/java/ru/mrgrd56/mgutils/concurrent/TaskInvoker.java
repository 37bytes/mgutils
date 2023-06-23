package ru.mrgrd56.mgutils.concurrent;

import ru.mrgrd56.mgutils.delegate.ExceptionalRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Used for executing a specific set of tasks, distributing them among threads using {@link ExecutorService}.<br><br>
 * Main methods:<br>
 * <ul>
 *     <li>{@link #submit(ExceptionalRunnable)} - accepts a task. The task <u>does not</u> start executing;</li>
 *     <li>{@link #completeAll()} - executes all accepted tasks using {@link #invokeAll}, waits for their completion, and returns the results.</li>
 * </ul>
 *
 * @param <T> The type of value returned by the executed tasks.
 * @since 1.0
 */
public class TaskInvoker<T> {
    private final List<InvokerCallable<T>> tasks = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService executor;

    public TaskInvoker(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Accepts a task by adding it to the list for execution. The task <u>does not</u> start executing.
     */
    public void submit(Callable<T> task) {
        tasks.add(new InvokerCallable<>(task));
    }

    /**
     * Accepts a task with no return value, adding it to the list for execution. The task <u>does not</u> start executing.
     *
     * <br><br><p>
     * Since v1.6.0 it takes {@link ExceptionalRunnable} instead of regular {@link Runnable}.
     */
    public void submit(ExceptionalRunnable task) {
        submit(() -> {
            task.run();
            return null;
        });
    }

    /**
     * Accepts tasks by adding them to the list for execution. The tasks <u>do not</u> start executing.
     *
     * @see #submit(Callable)
     */
    public void submitAll(Collection<Callable<T>> tasks) {
        this.tasks.addAll(tasks.stream().map(InvokerCallable::new).collect(Collectors.toList()));
    }

    /**
     * Accepts tasks with no return value, adding them to the list for execution. The tasks <u>do not</u> start executing.
     *
     * <br><br><p>
     * Since v1.6.0 it takes a list of {@link ExceptionalRunnable}s instead of regular {@link Runnable}s.
     *
     * @see #submit(ExceptionalRunnable)
     * @see #submitAll(Collection)
     */
    public void submitAllVoid(Collection<ExceptionalRunnable> tasks) {
        List<Callable<T>> voidTasks = tasks.stream()
                .map(task -> {
                    return (Callable<T>) () -> {
                        task.run();
                        return null;
                    };
                })
                .collect(Collectors.toList());

        submitAll(voidTasks);
    }

    /**
     * Executes all accepted tasks using {@link ExecutorService#invokeAll}. The list of accepted tasks is cleared.
     */
    public List<Future<T>> invokeAll() {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            tasks.clear();
        }
    }

    /**
     * Executes all accepted tasks using {@link ExecutorService#invokeAll}. The list of accepted tasks is cleared.<br>
     * Uses the provided timeout.
     *
     * @since 1.6.0
     */
    public List<Future<T>> invokeAll(long timeout, TimeUnit unit) {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return executor.invokeAll(tasks, timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            tasks.clear();
        }
    }

    /**
     * Executes all still uncompleted tasks using {@link #invokeAll}, waits for their completion, and returns the results.<br>
     * The list of accepted tasks is cleared.
     *
     * @throws CancellationException
     *         May be thrown if the {@link #cancelAll} method was called while the current tasks were being executed.
     */
    public List<T> completeAll() throws CancellationException {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        return completeFutures(invokeAll());
    }

    /**
     * Executes all still uncompleted tasks using {@link #invokeAll}, waits for their completion, and returns the results.<br>
     * The list of accepted tasks is cleared.<br>
     * Uses the provided timeout.
     *
     * @throws CancellationException
     *         May be thrown if the {@link #cancelAll} method was called while the current tasks were being executed.<br>
     *         It is also thrown if the timeout has been exceeded.
     *
     * @since 1.6.0
     */
    public List<T> completeAll(long timeout, TimeUnit unit) throws CancellationException {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        return completeFutures(invokeAll(timeout, unit));
    }

    /**
     * Cancels all the accepted tasks. If attempted to execute, these tasks will throw a {@link CancellationException}.<br>
     * Multiple calls to the method for the same tasks will not lead to anything.<br>
     * Clears the accepted tasks list.
     */
    public void cancelAll() {
        if (this.tasks.isEmpty()) {
            return;
        }

        synchronized (this.tasks) {
            if (!this.tasks.isEmpty()) {
                try {
                    for (InvokerCallable<T> task : this.tasks) {
                        task.cancel();
                    }
                } finally {
                    tasks.clear();
                }
            }
        }
    }

    private static <T> T getFutureResult(Future<T> future) throws CancellationException {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof CancellationException) {
                throw (CancellationException) e.getCause();
            }

            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> completeFutures(List<Future<T>> futures) {
        return futures.stream()
                .map(TaskInvoker::getFutureResult)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("TaskInvoker tasks: %s, executor: %s", tasks.size(), executor.toString());
    }

    private static class InvokerCallable<T> implements Callable<T> {
        private final Callable<T> callable;
        private final AtomicBoolean isCancelled = new AtomicBoolean(false);

        public InvokerCallable(Callable<T> callable) {
            this.callable = callable;
        }

        public void cancel() {
            isCancelled.set(true);
        }

        @Override
        public T call() throws Exception {
            if (isCancelled.get()) {
                throw new CancellationException("The task has been cancelled");
            }

            return callable.call();
        }
    }
}
