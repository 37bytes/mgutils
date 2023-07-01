package ru.mrgrd56.mgutils.delegate;

import java.util.Objects;

/**
 * Represents a {@link java.util.function.Consumer} that can throw an {@link Exception}.
 * @since 2.0.0
 */
public interface ExceptionalConsumer<T> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws Exception;

    /**
     * Returns a composed {@code ExceptionalConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code ExceptionalConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ExceptionalConsumer<T> andThen(ExceptionalConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
