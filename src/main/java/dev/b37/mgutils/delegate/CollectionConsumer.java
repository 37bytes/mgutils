package dev.b37.mgutils.delegate;

import java.util.Collection;

/**
 * Represents a {@link MultiConsumer} that can accept items adding them to the {@code collection}.
 * @since 2.0.0
 */
public class CollectionConsumer<T> implements MultiConsumer<T> {
    private final Collection<T> collection;

    public CollectionConsumer(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public void accept(T item) {
        collection.add(item);
    }

    @Override
    public void acceptAll(Collection<T> items) {
        collection.addAll(items);
    }
}
