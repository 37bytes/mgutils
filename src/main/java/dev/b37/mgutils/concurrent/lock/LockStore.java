package dev.b37.mgutils.concurrent.lock;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A store for managing and providing {@link KeyLock} instances based on unique keys.
 * When a lock is requested for a specific key, the store either provides an
 * existing lock or creates a new one.
 *
 * @see KeyLock
 * @since 3.2.0
 */
public class LockStore {
    private final Map<Object, KeyLock> lockMap = new ConcurrentHashMap<>();

    /**
     * Acquires a {@link KeyLock} for the specified key. If no lock exists for
     * the key, a new one is created. If a lock already exists, its usage count is incremented.
     *
     * @param key the unique key for which a lock is requested.
     * @return a lock associated with the specified key.
     */
    public KeyLock lock(Object key) {
        KeyLock lock = lockMap.compute(key, (k, existingLock) -> {
            if (existingLock == null) {
                return new StoreKeyLock(key, lockMap);
            }

            existingLock.incrementUsages();
            return existingLock;
        });

        lock.lock();
        return lock;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("locks", lockMap)
                .toString();
    }

    private static class StoreKeyLock extends KeyLock {
        private final Object key;
        private final Map<Object, KeyLock> lockMap;

        public StoreKeyLock(Object key, Map<Object, KeyLock> lockMap) {
            this.key = key;
            this.lockMap = lockMap;
        }

        @Override
        public void close() {
            unlock();
            if (decrementUsages() == 0) {
                lockMap.remove(key, this);
            }
        }
    }
}
