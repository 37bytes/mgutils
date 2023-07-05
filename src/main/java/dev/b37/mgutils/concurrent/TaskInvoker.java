package dev.b37.mgutils.concurrent;

import dev.b37.mgutils.delegate.ExceptionalRunnable;
import dev.b37.mgutils.delegate.MultiConsumer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import dev.b37.mgutils.delegate.CollectionConsumer;
import dev.b37.mgutils.delegate.ExceptionalConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used for executing a specific set of tasks, distributing them among threads using {@link ExecutorService}.<br><br>
 * Main methods:<br>
 * <ul>
 *     <li>{@link #submit(ExceptionalRunnable)} - accepts a task. The task <em>does not</em> start executing;</li>
 *     <li>{@link #completeAll()} - executes all accepted tasks using {@link #invokeAllTasks}, waits for their completion, and returns the results.</li>
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
     * Accepts a task by adding it to the list for execution. The task <em>does not</em> start executing.<br>
     * The task accepted returns a single value which will be added to the output list.
     */
    public void submit(Callable<T> task) {
        tasks.add(InvokerCallable.ofCallable(task));
    }

    /**
     * Accepts a task with no return value, adding it to the list for execution. The task <em>does not</em> start executing.<br>
     * No values will be added to the output list.
     *
     * <br><br><p>
     * Since v1.6.0 it takes {@link ExceptionalRunnable} instead of regular {@link Runnable}.<br>
     * Since v2.0.0 {@code null} is not added to the output list.
     */
    public void submit(ExceptionalRunnable task) {
        tasks.add(InvokerCallable.ofRunnable(task));
    }

    /**
     * Accepts a task by adding it to the list for execution. The task <em>does not</em> start executing.<br>
     * The values that are passed to the {@code consumer} will be added to the output list.<br>
     * These values are firstly collected to the newly created <i>synchronized</i> {@link ArrayList}.<br>
     * <br>
     * It's recommended to use this method only if you need to add multiple values by one task,
     * otherwise consider using {@link #submit(Callable)} instead.
     *
     * @since 2.0.0
     */
    public void submit(ExceptionalConsumer<MultiConsumer<T>> task) {
        submit(task, () -> Collections.synchronizedList(new ArrayList<>()));
    }

    /**
     * Accepts a task by adding it to the list for execution. The task <em>does not</em> start executing.<br>
     * The values that are passed to the {@code consumer} will be added to the output list.<br>
     * These values are firstly collected to the {@link List} newly created from the provided {@code listFactory}.
     *
     * @param listFactory The factory used to create a {@link List} to which the accepted values will be collected.<br>
     *                    <br>
     *                    It's recommended to use this method only if you need to add multiple values by one task,
     *                    otherwise consider using {@link #submit(Callable)} instead.
     * @since 2.0.0
     */
    public void submit(ExceptionalConsumer<MultiConsumer<T>> task, Supplier<List<T>> listFactory) {
        tasks.add(new InvokerCallable<>(() -> {
            List<T> appliedValues = listFactory.get();
            MultiConsumer<T> valueConsumer = new CollectionConsumer<>(appliedValues);
            task.accept(valueConsumer);
            return new TaskValue.Multi<>(appliedValues);
        }));
    }

    /**
     * Accepts tasks by adding them to the list for execution. The tasks <u>do not</u> start executing.
     *
     * @see #submit(Callable)
     */
    public void submitAll(Collection<Callable<T>> tasks) {
        List<InvokerCallable<T>> invokerCallables = tasks.stream()
                .map(InvokerCallable::ofCallable)
                .collect(Collectors.toList());

        this.tasks.addAll(invokerCallables);
    }

    /**
     * Accepts tasks with no return value, adding them to the list for execution. The tasks <u>do not</u> start executing.<br>
     * No values will be added to the output list.
     *
     * <br><br><p>
     * Since v1.6.0 it takes a list of {@link ExceptionalRunnable}s instead of regular {@link Runnable}s.
     * <br>
     * Since v2.0.0 {@code null}s are not added to the output list.
     *
     * @see #submit(ExceptionalRunnable)
     * @see #submitAll(Collection)
     */
    public void submitAllVoid(Collection<ExceptionalRunnable> tasks) {
        List<InvokerCallable<T>> invokerCallables = tasks.stream()
                .map(InvokerCallable::<T>ofRunnable)
                .collect(Collectors.toList());

        this.tasks.addAll(invokerCallables);
    }

    /**
     * Executes all still uncompleted tasks using {@link #invokeAllTasks()}, waits for their completion, and returns the results.<br>
     * Calling this method clears the list of accepted tasks.
     *
     * @throws CancellationException May be thrown if the {@link #cancelAll} method was called while the current tasks were being executed.
     */
    public List<T> completeAll() throws CancellationException {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        return completeFutures(invokeAllTasks());
    }

    /**
     * Executes all still uncompleted tasks using {@link #invokeAllTasks()}, waits for their completion, and returns the results.<br>
     * Calling this method clears the list of accepted tasks.<br>
     * Uses the provided timeout.
     *
     * @throws CancellationException May be thrown if the {@link #cancelAll} method was called while the current tasks were being executed.<br>
     *                               It is also thrown if the timeout has been exceeded.
     * @since 1.6.0
     */
    public List<T> completeAll(long timeout, TimeUnit unit) throws CancellationException {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        return completeFutures(invokeAllTasks(timeout, unit));
    }

    /**
     * Executes all still uncompleted tasks using {@link #invokeAllTasks()}, waits for their completion, and returns the results.<br>
     * Calling this method clears the list of accepted tasks, which means that the results can't be obtained afterward.<br>
     *
     * @throws CancellationException May be thrown if the {@link #cancelAll} method was called while the current tasks were being executed.
     *
     * @since 3.0.0
     */
    public void completeAllVoid() throws CancellationException {
        completeFuturesVoid(invokeAllTasks());
    }

    /**
     * Executes all still uncompleted tasks using {@link #invokeAllTasks()} and waits for their completion.<br>
     * Calling this method clears the list of accepted tasks, which means that the results can't be obtained afterward.<br>
     * Uses the provided timeout.
     *
     * @throws CancellationException May be thrown if the {@link #cancelAll} method was called while the current tasks were being executed.<br>
     *                               It is also thrown if the timeout has been exceeded.
     * @since 3.0.0
     */
    public void completeAllVoid(long timeout, TimeUnit unit) throws CancellationException {
        completeFuturesVoid(invokeAllTasks(timeout, unit));
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("tasks", tasks.size())
                .append("executor", executor)
                .toString();
    }

    private List<Future<TaskValue<T>>> invokeAllTasks() {
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

    private List<Future<TaskValue<T>>> invokeAllTasks(long timeout, TimeUnit unit) {
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

    private static <T> List<T> completeFutures(List<Future<TaskValue<T>>> futures) {
        return futures.stream()
                .map(TaskInvoker::getFutureResult)
                .filter(taskValue -> !taskValue.isVoid())
                .flatMap(taskValue -> {
                    if (taskValue.isSingle()) {
                        return Stream.of(taskValue.asSingle().getValue());
                    }
                    if (taskValue.isMulti()) {
                        return taskValue.asMulti().getValues().stream();
                    }

                    throw new IllegalStateException(
                            "taskValue is an instance of an unsupported class: " + taskValue.getClass().getName());
                })
                .collect(Collectors.toList());
    }

    private static <T> void completeFuturesVoid(List<Future<TaskValue<T>>> futures) {
        futures.forEach(TaskInvoker::getFutureResult);
    }

    private static class InvokerCallable<T> implements Callable<TaskValue<T>> {
        private final Callable<TaskValue<T>> callable;
        private final AtomicBoolean isCancelled = new AtomicBoolean(false);

        private InvokerCallable(Callable<TaskValue<T>> callable) {
            this.callable = callable;
        }

        public static <T> InvokerCallable<T> ofCallable(Callable<T> callable) {
            return new InvokerCallable<>(() -> {
                return new TaskValue.Single<>(callable.call());
            });
        }

        public static <T> InvokerCallable<T> ofRunnable(ExceptionalRunnable runnable) {
            return new InvokerCallable<>(() -> {
                runnable.run();
                return TaskValue.Void.getInstance();
            });
        }

        public void cancel() {
            isCancelled.set(true);
        }

        @Override
        public TaskValue<T> call() throws Exception {
            if (isCancelled.get()) {
                throw new CancellationException("The task has been cancelled");
            }

            return callable.call();
        }
    }

    private interface TaskValue<T> {
        default boolean isVoid() {
            return false;
        }

        default boolean isSingle() {
            return false;
        }

        default boolean isMulti() {
            return false;
        }

        default Single<T> asSingle() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        default Multi<T> asMulti() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        class Void<T> implements TaskValue<T> {
            private Void() { }

            @SuppressWarnings("rawtypes")
            private static final Lazy<Void> instance = new Lazy<>(Void::new);

            @SuppressWarnings("unchecked")
            private static <T> Void<T> getInstance() {
                return instance.get();
            }

            @Override
            public boolean isVoid() {
                return true;
            }
        }

        class Single<T> implements TaskValue<T> {
            private final T value;

            private Single(T value) {
                this.value = value;
            }

            public T getValue() {
                return value;
            }

            @Override
            public boolean isSingle() {
                return true;
            }

            @Override
            public Single<T> asSingle() {
                return this;
            }
        }

        class Multi<T> implements TaskValue<T> {
            private final List<T> values;

            private Multi(List<T> values) {
                this.values = values;
            }

            public List<T> getValues() {
                return values;
            }

            @Override
            public boolean isMulti() {
                return true;
            }

            @Override
            public Multi<T> asMulti() {
                return this;
            }
        }
    }
}
