package dev.b37.mgutils;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 3.3.0
 */
public final class CollectionUtils {
    private CollectionUtils() { }

    /**
     * Maps the {@code items} using the {@code keyMapper} provided to a map returned by the {@code mapFactory}.
     * @since 3.3.0
     */
    public static <K, T, M extends Map<K, T>> M mapByKey(Collection<T> items,
                                                         Function<? super T, ? extends K> keyMapper,
                                                         Supplier<M> mapFactory) throws IllegalArgumentException {
        M result = mapFactory.get();

        for (T item : items) {
            addValueMappingByKey(keyMapper, item, result);
        }

        return result;
    }

    /**
     * Maps the {@code items} using the {@code keyMapper} provided.
     * @since 3.3.0
     */
    public static <K, T> Map<K, T> mapByKey(Collection<T> items, Function<? super T, ? extends K> keyMapper)
            throws IllegalArgumentException {
        return mapByKey(items, keyMapper, HashMap::new);
    }

    /**
     * Maps the {@code items} using the {@code keyMapper} provided to a map returned by the {@code mapFactory}.
     * @since 3.4.0
     */
    public static <K, T, M extends Map<K, T>> M mapByKey(T[] items,
                                                         Function<? super T, ? extends K> keyMapper,
                                                         Supplier<M> mapFactory) throws IllegalArgumentException {
        M result = mapFactory.get();

        for (T item : items) {
            addValueMappingByKey(keyMapper, item, result);
        }

        return result;
    }

    /**
     * Maps the {@code items} using the {@code keyMapper} provided.
     * @since 3.4.0
     */
    public static <K, T> Map<K, T> mapByKey(T[] items, Function<? super T, ? extends K> keyMapper)
            throws IllegalArgumentException {
        return mapByKey(items, keyMapper, HashMap::new);
    }

    /**
     * @param collection The collection to be checked.
     * @param fallback The value to be returned if the collection is null or empty.
     */
    @Nullable
    public static <C extends Collection<?>> C defaultIfEmpty(@Nullable C collection, @Nullable C fallback) {
        if (collection == null || collection.isEmpty()) {
            return fallback;
        }

        return collection;
    }

    /**
     * @param collection The collection to be checked.
     */
    @Nullable
    public static <C extends Collection<?>> C nullIfEmpty(@Nullable C collection) {
        return defaultIfEmpty(collection, null);
    }

    /**
     * @since 3.4.0
     */
    private static <K, T, M extends Map<K, T>> void addValueMappingByKey(Function<? super T, ? extends K> keyMapper, T item, M result) {
        T existingItem = result.putIfAbsent(keyMapper.apply(item), item);
        if (existingItem != null) {
            throw new IllegalArgumentException(
                    "Duplicated key [" + keyMapper.apply(existingItem) + "] on: " + existingItem);
        }
    }
}
