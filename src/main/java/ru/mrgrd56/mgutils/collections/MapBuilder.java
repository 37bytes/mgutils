package ru.mrgrd56.mgutils.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provides an interface for creating a mutable {@link Map}.<br>
 * In created {@link Map}s, both keys and values can be {@code null}, unlike {@code Map#of} and {@code Map#ofEntries}.<br>
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
     * Creates an instance of {@link MapBuilder} using the provided {@code mapFactory} to fromEntries a {@link Map}.<br>
     * The {@link Map} created by {@code mapFactory} must support the {@link Map#put} operation.
     * @param mapFactory The object that creates a {@link Map} instance.
     */
    public MapBuilder(@NotNull Supplier<? extends Map<K, V>> mapFactory) {
        Objects.requireNonNull(mapFactory);
        this.map = Objects.requireNonNull(mapFactory.get());
    }

    /**
     * Builds the instance of {@link MapBuilder} provided as {@code initialMap}.<br>
     * The {@link Map} provided in {@code initialMap} must support the {@link Map#put} operation.
     * @param initialMap The object that is used as an initial map for this builder.
     * @since 1.3.0
     */
    public MapBuilder(@NotNull Map<K, V> initialMap) {
        Objects.requireNonNull(initialMap);
        this.map = initialMap;
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
     * Inserts an entry into the created {@link Map}, using the provided {@code entry}.
     * @param entry The entry to be added to the created {@link Map}.
     */
    public MapBuilder<K, V> put(@NotNull Map.Entry<K, V> entry) {
        return put(entry.getKey(), entry.getValue());
    }

    /**
     * Inserts all of the mappings from the specified map into the created {@link Map}.<br>
     * The used map must support the {@link Map#putAll} method.
     *
     * @param map mappings to be stored in the created {@link Map}; must not be null.
     * @throws NullPointerException if the specified map is null.
     * @since 1.8.0
     */
    public MapBuilder<K, V> putAll(@NotNull Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
        return this;
    }

    /**
     * If the {@code condition} equals to {@code true}, inserts an entry into the created {@link Map}, using the provided {@code key} and {@code value}.
     * @param condition Condition indicating whether the entry will be retrieved from the {@code entrySupplier} and added to the {@link Map}.
     * @param key The key associated with the value.
     * @param value The value.
     * @since 1.3.0
     */
    public MapBuilder<K, V> putIf(boolean condition, @Nullable K key, @Nullable V value) {
        if (condition) {
            put(key, value);
        }

        return this;
    }

    /**
     * If the {@code condition} equals to {@code true}, inserts the {@code entry} into the created {@link Map}.
     * @param condition Condition indicating whether the entry will be retrieved from the {@code entrySupplier} and added to the {@link Map}.
     * @param entry The entry to be added to the created {@link Map}.
     * @since 1.3.0
     */
    public MapBuilder<K, V> putIf(boolean condition, @NotNull Map.Entry<K, V> entry) {
        if (condition) {
            put(entry);
        }

        return this;
    }

    /**
     * If the {@code condition} equals to {@code true}, inserts an entry
     * into the created {@link Map}, getting the entry from the provided {@code entrySupplier}.
     * @param condition Condition indicating whether the entry will be retrieved from the {@code entrySupplier} and added to the {@link Map}.
     * @param entrySupplier Function returning the entry to be added to the created {@link Map}.
     * @since 1.3.0
     */
    public MapBuilder<K, V> putIf(boolean condition, @NotNull Supplier<Map.Entry<K, V>> entrySupplier) {
        if (condition) {
            put(entrySupplier.get());
        }

        return this;
    }

    /**
     * Returns the created {@link Map} instance, the implementation depends on the {@code mapFactory}
     * passed to the {@link MapBuilder#MapBuilder(Supplier)} constructor.<br>
     * The returned {@link Map} has the same reference as the {@code initialMap} if passed.
     */
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
     * Creates an {@link Entry} instance. Supports {@code null} key and value.
     */
    @NotNull
    public static <K, V> Map.Entry<K, V> entry(@Nullable K key, @Nullable V value) {
        return new Entry<>(key, value);
    }

    /**
     * Creates a {@link HashMap} instance, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.<br>
     * <br>
     * Since 1.8.0 the replacement for {@link #fromEntries(Map.Entry[])}.
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
     * Since 1.8.0 the replacement for {@link #fromEntries(Map, Map.Entry[])}.
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
     * Since 1.8.0 the replacement for {@link #fromEntries(Supplier, Map.Entry[])}.
     * @since 1.8.0
     */
    @SafeVarargs
    public static <K, V, M extends Map<K, V>> M create(@NotNull Supplier<M> mapFactory, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(mapFactory), entries).buildAs();
    }

    // region deprecated fromEntries
    /**
     * Creates a {@link HashMap} instance, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.
     * @deprecated Since 1.8.0 use {@link #create(Map.Entry[])} instead.
     */
    @Deprecated
    @SafeVarargs
    public static <K, V> Map<K, V> fromEntries(@Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(), entries).build();
    }

    /**
     * Builds a {@link Map} instance using the specified {@code initialMap}, populating it with the provided {@code entries}.<br>
     * Null entries are ignored.<br>
     * It's guaranteed that the {@link Map} object returned by {@link #build()}
     * will have the same reference as the {@code initialMap}.
     * @since 1.3.0
     * @deprecated Since 1.8.0 use {@link #populate(Map, Map.Entry[])} instead.
     */
    @Deprecated
    @SafeVarargs
    public static <K, V> Map<K, V> fromEntries(Map<K, V> initialMap, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(initialMap), entries).build();
    }

    /**
     * Creates a {@link Map} instance using the specified {@code mapFactory}, populating the returning {@link Map} with the provided {@code entries}.<br>
     * Null entries are ignored.
     * @deprecated Since 1.8.0 use {@link #create(Supplier, Map.Entry[])} instead.
     */
    @Deprecated
    @SafeVarargs
    public static <K, V> Map<K, V> fromEntries(Supplier<Map<K, V>> mapFactory, @Nullable Map.Entry<K, V>... entries) {
        return populateBuilder(new MapBuilder<>(mapFactory), entries).build();
    }
    // endregion

    @SafeVarargs
    private static <K, V> MapBuilder<K, V> populateBuilder(MapBuilder<K, V> builder, @Nullable Map.Entry<K, V>... entries) {
        Objects.requireNonNull(entries, "entries array must not be null itself");

        for (Map.Entry<K, V> entry : entries) {
            if (entry != null) {
                builder.put(entry);
            }
        }

        return builder;
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
