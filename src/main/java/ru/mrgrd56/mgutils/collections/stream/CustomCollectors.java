package ru.mrgrd56.mgutils.collections.stream;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 1.0
 */
public final class CustomCollectors {
    private CustomCollectors() { }

    public static <K, V> Collector<? super Map.Entry<K, V>, ?, Map<K, V>> toMapFromEntries() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toUnmodifiableMapFromEntries() {
        return Collectors.collectingAndThen(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                Collections::unmodifiableMap);
    }
}