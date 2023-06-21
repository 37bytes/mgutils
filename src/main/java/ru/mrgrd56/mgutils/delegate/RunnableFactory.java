package ru.mrgrd56.mgutils.delegate;

/**
 * @since 1.1
 */
@FunctionalInterface
public interface RunnableFactory {
    Runnable create(Runnable runnable);
}
