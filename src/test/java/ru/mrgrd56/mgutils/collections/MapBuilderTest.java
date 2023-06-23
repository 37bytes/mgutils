package ru.mrgrd56.mgutils.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MapBuilderTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testMapBuilderInstance() {
        Map<String, Object> initialMap = new HashMap<>();
        initialMap.put("init", "42");

        Map<String, Object> map = new MapBuilder<>(initialMap)
                .putIf(true, "hello", "world")
                .putIf(false, "never1", "error")
                .put("correc", "t")
                .putIf(false, () -> {
                    log.info("NEVER CALLED");
                    Assertions.fail("Never called - called");
                    return MapBuilder.entry("never2", "!");
                })
                .putIf(1 == 1, () -> {
                    log.info("ALWAYS CALLED");
                    return MapBuilder.entry("always", "yay!");
                })
                .build();

        Assertions.assertEquals(4, map.size());
        Assertions.assertTrue(map.containsKey("init"));
        Assertions.assertTrue(map.containsKey("hello"));
        Assertions.assertFalse(map.containsKey("never1"));
        Assertions.assertTrue(map.containsKey("correc"));
        Assertions.assertFalse(map.containsKey("never2"));
        Assertions.assertTrue(map.containsKey("always"));
    }

    @Test
    public void testMapBuilderStatic() {
        Map<String, Object> initialMap = new HashMap<>();
        initialMap.put("init", "42");

        Map<String, Object> map = MapBuilder.fromEntries(initialMap,
                MapBuilder.entry("hello", "world"),
                false ? MapBuilder.entry("never1", "error") : null,
                MapBuilder.entry("correc", "t"),
                null,
                MapBuilder.entry("always", "yay!")
        );

        Assertions.assertEquals(4, map.size());
        Assertions.assertTrue(map.containsKey("init"));
        Assertions.assertTrue(map.containsKey("hello"));
        Assertions.assertFalse(map.containsKey("never1"));
        Assertions.assertTrue(map.containsKey("correc"));
        Assertions.assertFalse(map.containsKey("never2"));
        Assertions.assertTrue(map.containsKey("always"));
    }
}
