package ru.mrgrd56.mgutils.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provides an interface for creating an immutable {@link Map}.<br>
 * In created {@link Map}s, both keys and values can be {@code null}, unlike {@code Map#of} and {@code Map#ofEntries}.<br>
 * The created implementation of {@link Map} can be manually specified, by default the {@link HashMap} is used.<br>
 * The built map is immutable, wrapped by {@link Collections#unmodifiableMap(Map)}.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 3.0.0
 */
public class ImmutableMapBuilder<K, V> extends AbstractMapBuilder<K, V, ImmutableMapBuilder<K, V>> {
    /**
     * Creates an instance of {@link ImmutableMapBuilder} using the {@link HashMap} implementation.
     */
    public ImmutableMapBuilder() {
        super();
    }

    /**
     * Creates an instance of {@link ImmutableMapBuilder} using the provided {@code mapFactory} to create a {@link Map}.<br>
     * The {@link Map} created by {@code mapFactory} must support the {@link Map#put} operation.
     * @param mapFactory The object that creates a {@link Map} instance.
     */
    public ImmutableMapBuilder(@NotNull Supplier<? extends Map<K, V>> mapFactory) {
        super(mapFactory);
    }

    /**
     * Builds the instance of {@link ImmutableMapBuilder} provided as {@code initialMap}.<br>
     * The {@link Map} provided in {@code initialMap} must support the {@link Map#put} operation.
     * @param initialMap The object that is used as an initial map for this builder.
     */
    public ImmutableMapBuilder(@NotNull Map<K, V> initialMap) {
        super(initialMap);
    }

    /**
     * Returns the created {@link Map} instance, the implementation depends on the {@code mapFactory}
     * passed to the {@link AbstractMapBuilder#AbstractMapBuilder(Supplier)} constructor.<br>
     * The returned {@link Map} is wrapped using {@link Collections#unmodifiableMap(Map)}.
     */
    @Override
    public Map<K, V> build() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Creates a {@link HashMap} instance, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.
     */
    @SafeVarargs
    public static <K, V> Map<K, V> create(@Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new ImmutableMapBuilder<>(), entries).build();
    }

    /**
     * Builds a {@link Map} instance using the specified {@code initialMap}, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.<br>
     * The returned map will be the populated {@code initialMap} wrapped by {@link Collections#unmodifiableMap(Map)}.<br>
     * The {@code initialMap} is populated (mutated!) by the {@code entries} provided.<br>
     * If the {@code initialMap} is mutated after calling this method, the built map is mutated as well!
     */
    @SafeVarargs
    public static <K, V> Map<K, V> populate(@NotNull Map<K, V> initialMap, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new ImmutableMapBuilder<>(initialMap), entries).build();
    }

    /**
     * Creates a {@link Map} instance using the specified {@code mapFactory}, populating the returning {@link Map} with the provided {@code entries}.<br>
     * Null entries are ignored.
     */
    @SafeVarargs
    public static <K, V> Map<K, V> create(@NotNull Supplier<Map<K, V>> mapFactory, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new ImmutableMapBuilder<>(mapFactory), entries).build();
    }
}
