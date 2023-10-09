package dev.b37.mgutils.delegate;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    /**
     * Performs this operation on the given items.
     *
     * @param items the input items
     * @since 3.3.0
     */
    default void acceptAll(Iterable<T> items) {
        items.forEach(this::accept);
    }

    /**
     * Performs this operation on the given items stream.
     *
     * @param items the input items
     * @since 3.3.0
     */
    default void acceptAll(Stream<T> items) {
        items.forEach(this::accept);
    }

    /**
     * Performs this operation on the remaining items iterator.
     *
     * @param items the input items
     * @since 3.3.0
     */
    default void acceptAll(Iterator<T> items) {
        items.forEachRemaining(this::accept);
    }
}
