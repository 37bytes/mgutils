package dev.b37.mgutils.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @since 3.0.0
 */
public abstract class AbstractMapBuilder<K, V, MB extends AbstractMapBuilder<K, V, ?>> {
    protected final Map<K, V> map;

    @SuppressWarnings("unchecked")
    private final Class<MB> mapBuilderClass = (Class<MB>) this.getClass();

    /**
     * Creates an instance of {@link AbstractMapBuilder} using the {@link HashMap} implementation.
     */
    protected AbstractMapBuilder() {
        this(HashMap::new);
    }

    /**
     * Creates an instance of {@link AbstractMapBuilder} using the provided {@code mapFactory} to create a {@link Map}.<br>
     * The {@link Map} created by {@code mapFactory} must support the {@link Map#put} operation.
     * @param mapFactory The object that creates a {@link Map} instance.
     */
    protected AbstractMapBuilder(@NotNull Supplier<? extends Map<K, V>> mapFactory) {
        Objects.requireNonNull(mapFactory);
        this.map = Objects.requireNonNull(mapFactory.get());
    }

    /**
     * Builds an instance of {@link AbstractMapBuilder} using the provided {@code initialMap}.<br>
     * The {@link Map} provided as {@code initialMap} must support the {@link Map#put} operation.
     * @param initialMap The object that is used as an initial map for this builder.
     * @since 1.3.0
     */
    protected AbstractMapBuilder(@NotNull Map<K, V> initialMap) {
        Objects.requireNonNull(initialMap);
        this.map = initialMap;
    }

    /**
     * Inserts an entry into the created {@link Map}, using the provided {@code key} and {@code value}.
     * @param key The key associated with the value.
     * @param value The value.
     */
    public MB put(@Nullable K key, @Nullable V value) {
        map.put(key, value);
        return mapBuilderClass.cast(this);
    }

    /**
     * Inserts an entry into the created {@link Map}, using the provided {@code entry}.
     * @param entry The entry to be added to the created {@link Map}.
     */
    public MB put(@NotNull Map.Entry<K, V> entry) {
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
    public MB putAll(@NotNull Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
        return mapBuilderClass.cast(this);
    }

    /**
     * If the {@code condition} equals to {@code true}, inserts an entry into the created {@link Map}, using the provided {@code key} and {@code value}.
     * @param condition Condition indicating whether the entry will be retrieved from the {@code entrySupplier} and added to the {@link Map}.
     * @param key The key associated with the value.
     * @param value The value.
     * @since 1.3.0
     */
    public MB putIf(boolean condition, @Nullable K key, @Nullable V value) {
        if (condition) {
            put(key, value);
        }

        return mapBuilderClass.cast(this);
    }

    /**
     * If the {@code condition} equals to {@code true}, inserts the {@code entry} into the created {@link Map}.
     * @param condition Condition indicating whether the entry will be retrieved from the {@code entrySupplier} and added to the {@link Map}.
     * @param entry The entry to be added to the created {@link Map}.
     * @since 1.3.0
     */
    public MB putIf(boolean condition, @NotNull Map.Entry<K, V> entry) {
        if (condition) {
            put(entry);
        }

        return mapBuilderClass.cast(this);
    }

    /**
     * If the {@code condition} equals to {@code true}, inserts an entry
     * into the created {@link Map}, getting the entry from the provided {@code entrySupplier}.
     * @param condition Condition indicating whether the entry will be retrieved from the {@code entrySupplier} and added to the {@link Map}.
     * @param entrySupplier Function returning the entry to be added to the created {@link Map}.
     * @since 1.3.0
     */
    public MB putIf(boolean condition, @NotNull Supplier<Map.Entry<K, V>> entrySupplier) {
        if (condition) {
            put(entrySupplier.get());
        }

        return mapBuilderClass.cast(this);
    }

    /**
     * Returns the created {@link Map} instance, the implementation depends on the {@code mapFactory}
     * passed to the {@link AbstractMapBuilder#AbstractMapBuilder(Supplier)} constructor.
     */
    public abstract Map<K, V> build();

    /**
     * Creates an {@link Entry} instance. Supports {@code null} key and value.
     */
    @NotNull
    public static <K, V> Map.Entry<K, V> entry(@Nullable K key, @Nullable V value) {
        return new AbstractMapBuilder.Entry<>(key, value);
    }

    @SafeVarargs
    protected static <K, V, MB extends AbstractMapBuilder<K, V, ?>> MB populateBuilder(MB builder, Map.Entry<K, V>... entries) {
        Objects.requireNonNull(entries, "entries array must not be null itself");

        for (Map.Entry<K, V> entry : entries) {
            if (entry != null) {
                builder.put(entry);
            }
        }

        return builder;
    }

    /**
     * {@link Map} entry used for creating {@link Map} using {@link AbstractMapBuilder}.<br>
     * Implements {@link Map.Entry}.
     */
    protected static class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        protected Entry(K key, V value) {
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
