package ru.mrgrd56.mgutils.delegate;

/**
 * Represents a {@link Runnable} task that can throw an {@link Exception}.
 * @since 1.6.0
 */
public interface ExceptionalRunnable {
    void run() throws Exception;
}
