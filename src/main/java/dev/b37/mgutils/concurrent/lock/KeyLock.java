package dev.b37.mgutils.concurrent.lock;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A specialized lock designed to work with a unique key. This lock keeps track
 * of the number of times it is in use (usage count) and will be removed from the
 * associated lock store once the usage count reaches zero.
 *
 * @since 3.2.0
 * @see LockStore
 */
public abstract class KeyLock implements AutoCloseable {
    private final Lock lock = createLock();
    private final AtomicInteger usagesCount = new AtomicInteger(1);

    /**
     * Releases the lock and decrements the usage count. If the usage count
     * reaches zero, the lock is removed from the associated lock map.
     */
    @Override
    public abstract void close();

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("usages", usagesCount)
                .toString();
    }

    final void lock() {
        lock.lock();
    }

    protected final void unlock() {
        lock.unlock();
    }

    final void incrementUsages() {
        usagesCount.incrementAndGet();
    }

    protected final int decrementUsages() {
        return usagesCount.decrementAndGet();
    }

    /**
     * Factory method to create a new instance of a {@link ReentrantLock}.
     * This can be overridden by subclasses to provide custom lock implementations.
     *
     * @return a new instance of a lock.
     */
    protected Lock createLock() {
        return new ReentrantLock();
    }
}