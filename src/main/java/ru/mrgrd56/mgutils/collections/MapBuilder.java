package ru.mrgrd56.mgutils.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provides an interface for creating a mutable {@link Map}.<br>
 * In created {@link Map}s, both keys and values can be {@code null}, unlike {@code Map#of} and {@code Map#ofEntries}.<br>
 * The created implementation of {@link Map} can be manually specified, by default the {@link HashMap} is used.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 1.0
 */
public class MapBuilder<K, V> extends AbstractMapBuilder<K, V, MapBuilder<K, V>> {
    /**
     * Creates an instance of {@link MapBuilder} using the {@link HashMap} implementation.
     */
    public MapBuilder() {
        super();
    }

    /**
     * Creates an instance of {@link MapBuilder} using the provided {@code mapFactory} to create a {@link Map}.<br>
     * The {@link Map} created by {@code mapFactory} must support the {@link Map#put} operation.
     * @param mapFactory The object that creates a {@link Map} instance.
     */
    public MapBuilder(@NotNull Supplier<? extends Map<K, V>> mapFactory) {
        super(mapFactory);
    }

    /**
     * Builds the instance of {@link MapBuilder} provided as {@code initialMap}.<br>
     * The {@link Map} provided in {@code initialMap} must support the {@link Map#put} operation.
     * @param initialMap The object that is used as an initial map for this builder.
     * @since 1.3.0
     */
    public MapBuilder(@NotNull Map<K, V> initialMap) {
        super(initialMap);
    }

    /**
     * Returns the created {@link Map} instance, the implementation depends on the {@code mapFactory}
     * passed to the {@link MapBuilder#MapBuilder(Supplier)} constructor.<br>
     * The returned {@link Map} has the same reference as the {@code initialMap} if passed.
     */
    @Override
    public Map<K, V> build() {
        return map;
    }

    /**
     * Returns the created {@link Map} instance, the implementation depends on the {@code mapFactory}
     * passed to the {@link MapBuilder#MapBuilder(Supplier)} constructor.<br>
     * <br>
     * Casts the built map to the {@link M} type.
     * @throws ClassCastException Thrown if the {@link M} type is not assignable from the actual type of the built {@code map}.
     * @since 1.8.0
     */
    @SuppressWarnings("unchecked")
    public <M extends Map<K, V>> M buildAs() throws ClassCastException {
        return (M) map;
    }

    /**
     * Creates a {@link HashMap} instance, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.<br>
     * <br>
     * Since 1.8.0 the replacement for {@code #fromEntries(Map.Entry[])} which was removed in v2.0.0.
     * @since 1.8.0
     */
    @SafeVarargs
    public static <K, V> Map<K, V> create(@Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(), entries).build();
    }

    /**
     * Builds a {@link Map} instance using the specified {@code initialMap}, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.<br>
     * It's guaranteed that the {@link Map} object returned by {@link #build()}
     * will have the same reference as the {@code initialMap}.<br>
     * <br>
     * Since 1.8.0 the replacement for {@code #fromEntries(Map, Map.Entry[])} which was removed in v2.0.0.
     * @since 1.8.0
     */
    @SafeVarargs
    public static <K, V, M extends Map<K, V>> M populate(@NotNull M initialMap, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(initialMap), entries).buildAs();
    }

    /**
     * Creates a {@link Map} instance using the specified {@code mapFactory}, populating the returning {@link Map} with the provided {@code entries}.<br>
     * Null entries are ignored.<br>
     * <br>
     * Since 1.8.0 the replacement for {@code #fromEntries(Supplier, Map.Entry[])} which was removed in v2.0.0.
     * @since 1.8.0
     */
    @SafeVarargs
    public static <K, V, M extends Map<K, V>> M create(@NotNull Supplier<M> mapFactory, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(mapFactory), entries).buildAs();
    }
}
