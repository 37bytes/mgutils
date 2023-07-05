package dev.b37.mgutils.delegate;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents a {@link java.util.function.Consumer} that can accept multiple items at once, as well as single item.
 * @since 2.0.0
 */
public interface MultiConsumer<T> extends Consumer<T> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override
    void accept(T t);

    /**
     * Performs this operation on the given items.
     *
     * @param items the input items
     */
    void acceptAll(Collection<T> items);
}
