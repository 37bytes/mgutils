package ru.mrgrd56.mgutils.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TaskInvokerTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testTaskInvoker() {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        TaskInvoker<String> invoker = new TaskInvoker<>(executor);

        for (int i = 0; i < 60; i++) {
            int number = i;
            invoker.submit(() -> {
                // Here's your task returning some data
                Thread.sleep(50);
                return "Number " + number;
            });
        }

        List<String> results = invoker.completeAll();

        Assertions.assertEquals(60, results.size());
    }

    @Test
    public void testTaskInvokerVoid() {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        TaskInvoker<String> invoker = new TaskInvoker<>(executor);

        for (int i = 0; i < 60; i++) {
            invoker.submit(() -> {
                // Here's your task
                Thread.sleep(50);
            });
        }

        List<String> results = invoker.completeAll();

        Assertions.assertEquals(0, results.size());
    }

    @Test
    public void testTaskInvokerNull() {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        TaskInvoker<String> invoker = new TaskInvoker<>(executor);

        for (int i = 0; i < 60; i++) {
            invoker.submit(() -> {
                // Here's your task
                Thread.sleep(50);
                return null;
            });
        }

        List<String> results = invoker.completeAll();

        Assertions.assertEquals(60, results.size());
    }

    @Test
    public void testTaskInvokerConsumer() {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        TaskInvoker<Integer> invoker = new TaskInvoker<>(executor);

        for (int i = 0; i < 8; i++) {
            int number = i;
            invoker.submit((consumer) -> {
                for (int j = 0; j < 1_000_000; j++) {
                    consumer.accept(number * j);
                }
            });
        }

        List<Integer> results = invoker.completeAll();

        Assertions.assertEquals(8 * 1_000_000, results.size());
    }

    @Test
    public void testTaskInvokerCancellation() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        List<String> results = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger counter = new AtomicInteger(0);

        final int MAX_COUNT = 100;

        for (int i = 0; i < MAX_COUNT; i++) {
            int number = i;
            invoker.submit(() -> {
                // Here's your task returning some data
                Thread.sleep(50);

                if (counter.getAndIncrement() == 6) {
                    invoker.cancelAll();
                }

                results.add("Number " + number);
            });
        }

        try {
            invoker.completeAll();
            Assertions.fail("Cancellation exception was not thrown");
        } catch (CancellationException e) {
            log.info("[success] A cancellation exception was thrown: {} {}", e.getClass().getName(), e.getMessage());
        }

        log.info("Got results {} of {}", results.size(), MAX_COUNT);
        Assertions.assertTrue(results.size() < 30);
    }

    @Test
    public void testExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(50);

        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            int number = i;
            tasks.add(() -> {
                // Here's your task returning some data
                Thread.sleep(5);
                return "Number " + number;
            });
        }

        List<Future<String>> resultFutures;
        try {
            resultFutures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<String> results = resultFutures.stream()
                .map(stringFuture -> {
                    try {
                        return stringFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        Assertions.assertEquals(60, results.size());
    }
}
