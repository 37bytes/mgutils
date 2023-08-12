package dev.b37.mgutils.concurrent.lock;

import com.google.common.util.concurrent.Striped;
import dev.b37.mgutils.RandomUtils;
import dev.b37.mgutils.collections.ImmutableMapBuilder;
import dev.b37.mgutils.concurrent.TaskInvoker;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LockStoreTests {
    @Test
    public void testLockStore() {
        LockStore lockStore = new LockStore();

        ExecutorService executor = Executors.newCachedThreadPool();
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        for (int i = 0; i < 3; i++) {
            int key = i;

            for (int j = 0; j < 3; j++) {
                int finalJ = j;

                invoker.submit(() -> {
                    Thread.sleep(50 + (key * 30) + (finalJ * 30));

                    System.out.println("Before-Lock key " + key + " - " + lockStore);
                    try (KeyLock ignored = lockStore.lock(key)) {
                        System.out.println("Lock key " + key + " - " + lockStore);
                        Thread.sleep(150);
                        System.out.println("Unlock key " + key + " - " + lockStore);
                    }
                    System.out.println("After-Unlock key " + key + " - " + lockStore);
                });
            }
        }

        invoker.completeAllVoid();
        Object __null = null;
    }

    @Test
    public void testBasicLockFunctionality() throws InterruptedException {
        LockStore lockStore = new LockStore();
        try (KeyLock keyLock = lockStore.lock("testKey")) {
            Thread.sleep(100);
        }
        // Make sure we can reach here without any exception
    }

    @Test
    public void testMultiThreadSameKey() throws InterruptedException {
        LockStore lockStore = new LockStore();
        ExecutorService service = Executors.newFixedThreadPool(2);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        service.execute(() -> {
            try (KeyLock keyLock = lockStore.lock("testKey")) {
                latch1.countDown();
                latch2.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        latch1.await(); // Wait for the first thread to acquire the lock

        service.execute(() -> {
            try (KeyLock keyLock = lockStore.lock("testKey")) {
                // This should wait for the first thread to release the lock
            }
        });

        TimeUnit.SECONDS.sleep(1); // Ensure that the second thread is waiting

        latch2.countDown(); // Let the first thread release the lock

        service.shutdown();
        assertTrue(service.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    public void testMultiThreadDifferentKeys() throws InterruptedException {
        LockStore lockStore = new LockStore();
        ExecutorService service = Executors.newFixedThreadPool(2);

        service.execute(() -> {
            try (KeyLock keyLock = lockStore.lock("key1")) {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        service.execute(() -> {
            try (KeyLock keyLock = lockStore.lock("key2")) {
                // Should not wait, as it's a different key
            }
        });

        service.shutdown();
        assertTrue(service.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    public void testReentrantLock() throws InterruptedException {
        LockStore lockStore = new LockStore();
        try (KeyLock ignored = lockStore.lock("key1")) {
            try (KeyLock ignored1 = lockStore.lock("key2")) {
                try (KeyLock ignored2 = lockStore.lock("key1")) {
                    try (KeyLock ignored3 = lockStore.lock("key2")) {
                        // Ensure we can lock the same key again
                    }
                }
            }
        }
        assertTrue(true);
    }

    @Test
    public void stressTestSameKey() throws InterruptedException {
        final int NUM_THREADS = 1000;

        LockStore lockStore = new LockStore();
        ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        CountDownLatch goLatch = new CountDownLatch(1);

        // All threads will try to lock the same key and then wait on goLatch.
        IntStream.range(0, NUM_THREADS).forEach(i -> service.execute(() -> {
            try {
                latch.countDown(); // Signal that this thread is about to acquire the lock.
                try (KeyLock keyLock = lockStore.lock("stressKey")) {
//                    System.out.println("goLatch.await();");
                    goLatch.await(); // Wait for the main test thread to release all threads.
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

// Ensure all threads are ready to acquire the lock
        assertTrue(latch.await(30, TimeUnit.SECONDS));
//        System.out.println("goLatch.countDown();");
        goLatch.countDown(); // Let all threads proceed.

        service.shutdown();
        assertTrue(service.awaitTermination(60, TimeUnit.SECONDS));
    }

    @Test
    public void stressTestMultipleKeys() throws InterruptedException {
        final int NUM_THREADS = 1000;
        final int NUM_KEYS = 10;
        final int THREADS_PER_KEY = NUM_THREADS / NUM_KEYS;

        LockStore lockStore = new LockStore();
        ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch readyLatch = new CountDownLatch(NUM_THREADS);
        CountDownLatch goLatch = new CountDownLatch(1);

        IntStream.range(0, NUM_KEYS).forEach(key -> {
            IntStream.range(0, THREADS_PER_KEY).forEach(j -> {
                service.execute(() -> {
                    try {
                        readyLatch.countDown(); // Signal that this thread is about to acquire the lock.
                        try (KeyLock ignored = lockStore.lock("Key-" + key)) {
                            goLatch.await(); // Wait for the main test thread to release all threads.
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        });

        // Ensure all threads are ready to acquire the lock
        assertTrue(readyLatch.await(30, TimeUnit.SECONDS));
        goLatch.countDown(); // Let all threads proceed.

        service.shutdown();
        assertTrue(service.awaitTermination(60, TimeUnit.SECONDS));
    }

    @RepeatedTest(5)
    public void testLockStoreMultipleTasks() throws InterruptedException {
        final int idsCount = 3;

        List<Task> tasks = new ArrayList<>(100);
        for (int i = 0; i < 1000; i++) {
            Task task = new Task();
            task.id = UUID.randomUUID();
            task.type = RandomUtils.nextItem(TaskType.values());
            task.entityId = org.apache.commons.lang3.RandomUtils.nextInt(0, idsCount);
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(64);
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        LockStore lockStore = new LockStore();

        ImmutableMapBuilder<Integer, Queue<Task>> mapBuilder = new ImmutableMapBuilder<>(ConcurrentHashMap::new);
        for (int i = 0; i < idsCount; i++) {
            mapBuilder.put(i, new LinkedBlockingQueue<>());
        }
        Map<Integer, Queue<Task>> entityUsagesArray = mapBuilder.build();

        for (Task task : tasks) {
            invoker.submit(() -> {
                try {
                    Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(10, 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try (KeyLock lock = lockStore.lock(task.entityId)) {
                    Queue<Task> usagesByEntity = entityUsagesArray.get(task.entityId);
                    usagesByEntity.add(task);

                    try {
                        Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(1, 5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Assertions.assertEquals(1, usagesByEntity.size(), () -> {
                        return "there are unexpected usages (" + usagesByEntity + ") of current usagesByEntity " +
                               entityUsagesArray + " " + lockStore;
                    });

                    Assertions.assertTrue(entityUsagesArray.values().stream().allMatch(usages -> usages.size() <= 1), () -> {
                        return "there are unexpected usages of entityUsagesMapByTypes " + entityUsagesArray + " " +
                               lockStore;
                    });

                    Task removedTask = usagesByEntity.remove();
                    if (removedTask != task) {
                        Assertions.fail("removed task is not the same as the added. " + task + " -> " + removedTask);
                    }
                }
            });
        }

        invoker.completeAllVoid();
        executor.shutdown();

        Assertions.assertTrue(entityUsagesArray.values().stream().allMatch(Collection::isEmpty));

        try {
            Map lockMap = (Map) FieldUtils.readField(lockStore, "lockMap", true);
            Assertions.assertTrue(lockMap.isEmpty());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @RepeatedTest(5)
    public void testStripedMultipleTasks() throws InterruptedException {
        final int idsCount = 3;

        List<Task> tasks = new ArrayList<>(100);
        for (int i = 0; i < 1000; i++) {
            Task task = new Task();
            task.id = UUID.randomUUID();
            task.type = RandomUtils.nextItem(TaskType.values());
            task.entityId = org.apache.commons.lang3.RandomUtils.nextInt(0, idsCount);
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(64);
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        Striped<Lock> lockStore = Striped.lock(idsCount);

        ImmutableMapBuilder<Integer, Queue<Task>> mapBuilder = new ImmutableMapBuilder<>(ConcurrentHashMap::new);
        for (int i = 0; i < idsCount; i++) {
            mapBuilder.put(i, new LinkedBlockingQueue<>());
        }
        Map<Integer, Queue<Task>> entityUsagesArray = mapBuilder.build();

        for (Task task : tasks) {
            invoker.submit(() -> {
                try {
                    Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(10, 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Lock lock = lockStore.get(task.entityId);
                lock.lock();
                try {
                    Queue<Task> usagesByEntity = entityUsagesArray.get(task.entityId);
                    usagesByEntity.add(task);

                    try {
                        Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(1, 5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Assertions.assertEquals(1, usagesByEntity.size(), () -> {
                        return "there are unexpected usages (" + usagesByEntity + ") of current usagesByEntity " +
                               entityUsagesArray + " " + lockStore;
                    });

                    Assertions.assertTrue(entityUsagesArray.values().stream().allMatch(usages -> usages.size() <= 1), () -> {
                        return "there are unexpected usages of entityUsagesMapByTypes " + entityUsagesArray + " " +
                               lockStore;
                    });

                    Task removedTask = usagesByEntity.remove();
                    if (removedTask != task) {
                        Assertions.fail("removed task is not the same as the added. " + task + " -> " + removedTask);
                    }
                } finally {
                    lock.unlock();
                }
            });
        }

        invoker.completeAllVoid();
        executor.shutdown();

        Assertions.assertTrue(entityUsagesArray.values().stream().allMatch(Collection::isEmpty));
    }

    @RepeatedTest(5)
    public void testLocksMapMultipleTasks() throws InterruptedException {
        final int idsCount = 3;

        List<Task> tasks = new ArrayList<>(100);
        for (int i = 0; i < 1000; i++) {
            Task task = new Task();
            task.id = UUID.randomUUID();
            task.type = RandomUtils.nextItem(TaskType.values());
            task.entityId = org.apache.commons.lang3.RandomUtils.nextInt(0, idsCount);
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(64);
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        Map<Object, Lock> lockStore = new ConcurrentHashMap<>();

        ImmutableMapBuilder<Integer, Queue<Task>> entityUsagesMapBuilder = new ImmutableMapBuilder<>(ConcurrentHashMap::new);
        for (int i = 0; i < idsCount; i++) {
            entityUsagesMapBuilder.put(i, new LinkedBlockingQueue<>());
        }
        Map<Integer, Queue<Task>> entityUsagesMap = entityUsagesMapBuilder.build();

        for (Task task : tasks) {
            invoker.submit(() -> {
                try {
                    Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(10, 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Lock lock = lockStore.computeIfAbsent(task.entityId, k -> new ReentrantLock());
                lock.lock();
                try {
                    Queue<Task> usagesByEntity = entityUsagesMap.get(task.entityId);
                    usagesByEntity.add(task);

                    try {
                        Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(1, 5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Assertions.assertEquals(1, usagesByEntity.size(), () -> {
                        return "there are unexpected usages (" + usagesByEntity + ") of current usagesByEntity " +
                               entityUsagesMap + " " + lockStore;
                    });

                    Assertions.assertTrue(entityUsagesMap.values().stream().allMatch(usages -> usages.size() <= 1), () -> {
                        return "there are unexpected usages of entityUsagesMapByTypes " + entityUsagesMap + " " +
                               lockStore;
                    });

                    Task removedTask = usagesByEntity.remove();
                    if (removedTask != task) {
                        Assertions.fail("removed task is not the same as the added. " + task + " -> " + removedTask);
                    }
                } finally {
                    lock.unlock();
                }
            });
        }

        invoker.completeAllVoid();
        executor.shutdown();

        Assertions.assertTrue(entityUsagesMap.values().stream().allMatch(Collection::isEmpty));
    }


    @RepeatedTest(10)
    public void testLockStoreMultipleTasksExtended() throws InterruptedException {
        List<Task> tasks = new ArrayList<>(10_000);
        for (int i = 0; i < 1000; i++) {
            Task task = new Task();
            task.id = UUID.randomUUID();
            task.type = RandomUtils.nextItem(TaskType.values());
            task.entityId = org.apache.commons.lang3.RandomUtils.nextInt(0, 50);
            tasks.add(task);
        }

        LockStore lockStore = new LockStore();

        ExecutorService executor = Executors.newFixedThreadPool(64);
        TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

        Map<Integer, Integer> entityUsagesMapByIds = new ConcurrentHashMap<>();
        Map<TaskType, Integer> entityUsagesMapByTypes = new ConcurrentHashMap<>();

        AtomicBoolean hasFailed = new AtomicBoolean(false);

        for (Task task : tasks) {
            invoker.submit(() -> {
                try {
                    Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(1, 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try (KeyLock lock = lockStore.lock(task.type)) {
//                    Integer usagesByEntity = entityUsagesMapByIds.compute(task.entityId, (k, v) -> {
//                        if (v == null) {
//                            return 1;
//                        } else {
//                            return v + 1;
//                        }
//                    });
                    Integer usagesByType = entityUsagesMapByTypes.compute(task.type, (k, v) -> {
                        if (v == null) {
                            return 1;
                        } else {
                            return v + 1;
                        }
                    });

                    Assertions.assertEquals(1, (int) usagesByType);

                    Assertions.assertTrue(entityUsagesMapByTypes.values().stream().allMatch(usages -> usages <= 1), () -> {
                        return "there are unexpected usages of entityUsagesMapByTypes " + entityUsagesMapByTypes + " " + lockStore;
                    });

                    try {
                        Thread.sleep(org.apache.commons.lang3.RandomUtils.nextLong(0, 10));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (!entityUsagesMapByIds.values().stream().allMatch(usages -> usages <= 1)) {
                        hasFailed.set(true);
                    }

                    entityUsagesMapByTypes.compute(task.type, (k, v) -> {
                        if (v == null) {
                            throw new IllegalStateException();
                        } else {
                            return v - 1;
                        }
                    });
//                    entityUsagesMapByIds.compute(task.entityId, (k, v) -> {
//                        if (v == null) {
//                            throw new IllegalStateException();
//                        } else {
//                            return v - 1;
//                        }
//                    });
                }
            });
        }

        invoker.completeAllVoid();
        executor.shutdown();

        Assertions.assertTrue(entityUsagesMapByTypes.values().stream().allMatch(usages -> usages == 0), () -> {
            return "there are unexpected usages of entityUsagesMapByTypes " + entityUsagesMapByTypes + " " + lockStore;
        });
        Assertions.assertTrue(hasFailed.get());

        try {
            Map lockMap = (Map) FieldUtils.readField(lockStore, "lockMap", true);
            Assertions.assertTrue(lockMap.isEmpty());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Task {
        private UUID id;
        private TaskType type;
        private Integer entityId;
    }

    private enum TaskType {
        PUBLISH, CHANGE, DELETE
    }
}
