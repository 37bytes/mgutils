package dev.b37.mgutils.concurrent;

import dev.b37.mgutils.logging.ScopedLoggerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyTest {
    private final ScopedLoggerFactory logs = new ScopedLoggerFactory(LoggerFactory.getLogger(this.getClass()));

    @Test
    public void testLazySingleton() {
        Assertions.assertSame(SingletonExample.getInstance(), SingletonExample.getInstance());
    }

    @Test
    public void testLazyNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Lazy<>(null);
        });

        Lazy<?> lazy = new Lazy<>(() -> null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            lazy.get();
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            lazy.get();
        });
    }

    @Test
    public void testLazyNullable() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new NullableLazy<>(null);
        });

        NullableLazy<?> lazy = new NullableLazy<>(() -> null);

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertNull(lazy.get());
        });

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertNull(lazy.get());
        });
    }

    @Test
    public void testLazyConcurrency() {
        Logger logger = logs.createLogger("testLazyConcurrency:", null);

        final String VALID_STRING = "Okay";

        AtomicInteger timesInitialized = new AtomicInteger();

        Lazy<String> lazy = new Lazy<>(() -> {
            timesInitialized.getAndIncrement();
            logger.info("Initializer called");

            try {
                Thread.sleep(100);
                return VALID_STRING;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        ExecutorService executor = Executors.newCachedThreadPool();
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        for (int i = 0; i < 100; i++) {
            invoker.submit(() -> {
                Assertions.assertEquals(VALID_STRING, lazy.get());
            });
        }

        invoker.completeAllVoid();

        Assertions.assertEquals(VALID_STRING, lazy.get());
        Assertions.assertEquals(1, timesInitialized.get());

        executor.shutdown();
    }

    private static class SingletonExample {
        private static final Lazy<SingletonExample> instance = new Lazy<>(SingletonExample::new);

        private SingletonExample() { }

        public static SingletonExample getInstance() {
            return instance.get();
        }
    }
}
