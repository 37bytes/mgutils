package ru.mrgrd56.mgutils.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provides an interface for creating a mutable {@link Map}.<br>
 * In created {@link Map}s, both keys and values can be {@code null}, unlike {@link Map#of} and {@link Map#ofEntries}.<br>
 * The created implementation of {@link Map} can be manually specified, by default the {@link HashMap} is used.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 1.0
 */
public class MapBuilder<K, V> {
    private final Map<K, V> map;

    /**
     * Creates an instance of {@link MapBuilder} using the {@link HashMap} implementation.
     */
    public MapBuilder() {
        this(HashMap::new);
    }

    /**
     * Creates an instance of {@link MapBuilder} using the provided {@code mapFactory} to create a {@link Map}.<br>
     * The {@link Map} created by {@code mapFactory} must support the {@link Map#put} operation.
     * @param mapFactory The object that creates a {@link Map} instance.
     */
    public MapBuilder(Supplier<Map<K, V>> mapFactory) {
        this.map = mapFactory.get();
    }

    /**
     * Inserts an entry into the created {@link Map}, using the provided {@code key} and {@code value}.
     * @param key The key associated with the value.
     * @param value The value.
     */
    public MapBuilder<K, V> put(@Nullable K key, @Nullable V value) {
        map.put(key, value);
        return this;
    }

    /**
     * Inserts an entry into the created {@link Map}, using the provided {@code key} and {@code value}.
     * @param entry The entry added to the created {@link Map}.
     */
    public MapBuilder<K, V> put(@NotNull Map.Entry<K, V> entry) {
        return put(entry.getKey(), entry.getValue());
    }

    /**
     * Returns the created {@link Map} instance, the implementation depends on the {@code mapFactory}
     * passed to the {@link MapBuilder#MapBuilder(Supplier)} constructor.
     */
    public Map<K, V> build() {
        return map;
    }

    /**
     * Creates an {@link Entry} instance.
     */
    public static <K, V> Map.Entry<K, V> entry(@Nullable K key, @Nullable V value) {
        return new Entry<>(key, value);
    }

    /**
     * Creates a {@link HashMap} instance, populating it with the provided {@code entries}.
     */
    @SafeVarargs
    public static <K, V> Map<K, V> fromEntries(@NotNull Map.Entry<K, V>... entries) {
        return fromEntriesInternal(new MapBuilder<>(), entries);
    }

    /**
     * Creates a {@link Map} instance using the specified {@code mapFactory}, populating it with the provided {@code entries}.
     */
    @SafeVarargs
    public static <K, V> Map<K, V> fromEntries(Supplier<Map<K, V>> mapFactory, @NotNull Map.Entry<K, V>... entries) {
        return fromEntriesInternal(new MapBuilder<>(mapFactory), entries);
    }

    @SafeVarargs
    private static <K, V> Map<K, V> fromEntriesInternal(MapBuilder<K, V> builder, @NotNull Map.Entry<K, V>... entries) {
        for (Map.Entry<K, V> entry : entries) {
            builder.put(entry);
        }
        return builder.build();
    }

    /**
     * {@link Map} entry used for creating {@link Map} using {@link MapBuilder}.
     */
    private static class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
