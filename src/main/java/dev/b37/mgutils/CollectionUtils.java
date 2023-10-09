package dev.b37.mgutils;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @since 3.3.0
 */
public final class CollectionUtils {
    private CollectionUtils() { }

    public static <K, T, M extends Map<K, T>> M mapByKey(Collection<T> items,
                                                         Function<? super T, ? extends K> keyMapper,
                                                         Supplier<M> mapFactory) throws IllegalArgumentException {
        Function<T, T> identity = Function.identity();

        return items.stream()
                .collect(
                        Collectors.toMap(
                                keyMapper,
                                identity,
                                (a, b) -> {
                                    throw new IllegalArgumentException(
                                            "Duplicated key [" + keyMapper.apply(b) + "] on: " + b);
                                },
                                mapFactory
                        )
                );
    }

    public static <K, T> Map<K, T> mapByKey(Collection<T> items, Function<? super T, ? extends K> keyMapper)
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
}
