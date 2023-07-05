package dev.b37.mgutils.random;

/**
 * @since 1.2.0
 */
@FunctionalInterface
public interface BoundedLongRandom {
    long nextLong(long bound);
}
