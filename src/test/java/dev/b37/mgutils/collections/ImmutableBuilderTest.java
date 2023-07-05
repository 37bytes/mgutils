package dev.b37.mgutils.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImmutableBuilderTest {
    @Test
    void testImmutableBuilder() {
        Map<String, Object> map = ImmutableMapBuilder.create(
                ImmutableMapBuilder.entry("key", "value"),
                ImmutableMapBuilder.entry("hello", 42),
                ImmutableMapBuilder.entry("wh", ImmutableMapBuilder.create(
                        ImmutableMapBuilder.entry("at", "000")
                ))
        );

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            map.put("hell0w", "some value");
        });

        Assertions.assertInstanceOf(Collections.unmodifiableMap(new HashMap<>()).getClass(), map);
        Assertions.assertInstanceOf(Collections.unmodifiableMap(new HashMap<>()).getClass(), map.get("wh"));

        Assertions.assertFalse(map.containsKey("hell0w"));
        Assertions.assertEquals("value", map.get("key"));
        Assertions.assertEquals(42, map.get("hello"));
    }

    @Test
    void testImmutableBuilderPopulate() {
        Map<String, Object> initialMap = MapBuilder.create(LinkedHashMap::new,
                MapBuilder.entry("morning", "utro"),
                MapBuilder.entry("night", "noch"));

        Map<String, Object> map = ImmutableMapBuilder.populate(initialMap,
                ImmutableMapBuilder.entry("key", "value"),
                ImmutableMapBuilder.entry("hello", 42),
                ImmutableMapBuilder.entry("wh", ImmutableMapBuilder.create(
                        ImmutableMapBuilder.entry("at", "000")
                ))
        );

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            map.put("hell0w", "some value");
        });

        initialMap.put("hello2", "some value 2");

        Assertions.assertInstanceOf(Collections.unmodifiableMap(new HashMap<>()).getClass(), map);
        Assertions.assertInstanceOf(Collections.unmodifiableMap(new HashMap<>()).getClass(), map.get("wh"));

        Assertions.assertInstanceOf(MapBuilder.create().getClass(), initialMap);

        Assertions.assertFalse(map.containsKey("hell0w"));
        Assertions.assertEquals("noch", map.get("night"));
        Assertions.assertEquals("value", map.get("key"));
        Assertions.assertEquals(42, map.get("hello"));
        Assertions.assertEquals("some value 2", map.get("hello2"));

        Assertions.assertFalse(map.containsKey("hell0w"));
        Assertions.assertEquals("utro", initialMap.get("morning"));
        Assertions.assertEquals("value", initialMap.get("key"));
        Assertions.assertEquals(42, initialMap.get("hello"));
        Assertions.assertEquals("some value 2", initialMap.get("hello2"));
    }
}
