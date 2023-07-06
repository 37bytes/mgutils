package dev.b37.mgutils.concurrent.execution.cached;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CachedInvokerTest {
    @Test
    public void testCachedInvoker() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        CachedInvoker cachedInvoker = new CachedInvoker(executor);

        AtomicInteger timesExecuted = new AtomicInteger(0);

        CachedInvocation<Double> mainInvocation1 = cachedInvoker.invoke("TASK-1", () -> {
            return fetchValue(timesExecuted); // executed
        });

        CachedInvocation<Double> mainInvocation2 = cachedInvoker.invoke("TASK-2", () -> {
            return fetchValue(timesExecuted); // executed
        });

        CompletableFuture<Double> mainFuture3 = cachedInvoker.invokeOnceAsync("TASK-3", () -> {
            return fetchValue(timesExecuted); // executed
        });

        AtomicReference<CompletableFuture<String>> mainFuture5 = new AtomicReference<>();
        AtomicInteger task5CalledTimes = new AtomicInteger(0);

        for (int i = 0; i < 100_000; i++) {
            cachedInvoker.invoke("TASK-1", () -> {
                Assertions.fail("This is not supposed to be executed");
                return fetchValue(timesExecuted);
            });

            cachedInvoker.invoke("TASK-2", () -> {
                Assertions.fail("This is not supposed to be executed");
                return fetchValue(timesExecuted);
            });

            mainFuture5.set(cachedInvoker.invokeOnceAsync("TASK-5", () -> {
                Thread.sleep(500);
                task5CalledTimes.getAndIncrement();

                return "task five";
            }));
        }

        for (int i = 0; i < 10; i++) {
            CachedInvocation<Double> invocation1 = cachedInvoker.invoke("TASK-1", () -> {
                Assertions.fail("This is not supposed to be executed");
                return fetchValue(timesExecuted);
            });

            CachedInvocation<Double> invocation2 = cachedInvoker.invoke("TASK-2", () -> {
                Assertions.fail("This is not supposed to be executed");
                return fetchValue(timesExecuted);
            });

            invocation1.future().thenApply((value) -> {
                Assertions.assertTrue(mainInvocation1.future().isDone());
                Assertions.assertEquals(mainInvocation1.get(), value);
                return value;
            });

            Assertions.assertSame(mainInvocation1, invocation1);
            Assertions.assertSame(mainInvocation2, invocation2);
            Assertions.assertSame(mainInvocation1.future(), invocation1.future());
            Assertions.assertSame(mainInvocation2.future(), invocation2.future());
        }

        mainInvocation1.getOnce();

        CachedInvocation<Double> invocation1 = cachedInvoker.invoke("TASK-1", () -> {
            return fetchValue(timesExecuted); // executed
        });

        CachedInvocation<Double> invocation2 = cachedInvoker.invoke("TASK-2", () -> {
            Assertions.fail("This is not supposed to be executed");
            return fetchValue(timesExecuted);
        });

        Assertions.assertNotSame(mainInvocation1, invocation1);
        Assertions.assertSame(mainInvocation2, invocation2);

        invocation1.close();

        mainInvocation2.future().join();
        mainInvocation2.close();
        mainFuture3.join();

        Double invocation3Value = cachedInvoker.invokeOnce("TASK-3", () -> {
            return fetchValue(timesExecuted); // executed
        });

        mainFuture5.get().join();

        Assertions.assertSame(5, timesExecuted.get());
        Assertions.assertSame(1, task5CalledTimes.get());
    }

    private double fetchValue(AtomicInteger timesExecuted) throws Exception {
        timesExecuted.getAndIncrement();

        Thread.sleep(200);
        return Math.random();
    }
}
