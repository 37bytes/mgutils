package dev.b37.mgutils.delegate;

/**
 * @since 1.1
 */
@FunctionalInterface
public interface RunnableFactory {
    Runnable create(Runnable runnable);
}
