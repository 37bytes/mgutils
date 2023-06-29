package ru.mrgrd56.mgutils.collections;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

        Map<String, Object> map = MapBuilder.populate(initialMap,
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

    @Test
    public void testBuildAs() {
        CustomMap customMap = new CustomMap(
                MapBuilder.create(
                        MapBuilder.entry("lorem", "ipsum")
                )
        );

        CustomMap customMap2 = new MapBuilder<>(customMap)
                .put("lorem", "sit amet")
                .buildAs();

        Assertions.assertInstanceOf(CustomMap.class, customMap2);
        Assertions.assertSame(customMap, customMap2);
        Assertions.assertEquals("sit amet", customMap2.getLorem());
    }

    @Test
    public void testBuildAsError() {
        Map<String, Object> customMap = MapBuilder.create(
                MapBuilder.entry("lorem", "ipsum")
        );

        try {
            CustomMap customMap2 = new MapBuilder<>(customMap)
                    .put("lorem", "sit amet")
                    .buildAs();

            Assertions.fail("ClassCastException not thrown");
        } catch (ClassCastException e) {
            // expected!
            log.info("ClassCastException thrown (success)", e);
        }
    }

    @Test
    public void testAutoCasting() {
        CustomMap customMap = new CustomMap(
                MapBuilder.create(
                        MapBuilder.entry("lorem", "ipsum")
                )
        );

        CustomMap customMap2 = MapBuilder.populate(customMap,
                MapBuilder.entry("lorem", "sit amet"));

        Assertions.assertInstanceOf(CustomMap.class, customMap2);
        Assertions.assertSame(customMap, customMap2);
        Assertions.assertEquals("sit amet", customMap2.getLorem());

        HashMap<String, Object> customMap3 = MapBuilder.populate(customMap,
                MapBuilder.entry("lorem3", "dolor sit amet"));

        Assertions.assertInstanceOf(HashMap.class, customMap3);
        Assertions.assertSame(customMap, customMap3);
        Assertions.assertEquals("dolor sit amet", customMap3.get("lorem3"));
        Assertions.assertEquals("sit amet", customMap3.get("lorem"));
    }

    @Test
    public void testAutoCasting2() {
        ConcurrentHashMap<String, Object> map = MapBuilder.create(ConcurrentHashMap::new,
                MapBuilder.entry("one", 1),
                MapBuilder.entry("two", 2),
                MapBuilder.entry("three", 3));

        Map<String, Object> map2 = MapBuilder.populate(new ConcurrentHashMap<>(map),
                MapBuilder.entry("lorem2_1", "sit amet 2 1"),
                MapBuilder.entry("lorem2_2", "sit amet 2 2"));

        ConcurrentHashMap<String, Object> map3 = MapBuilder.populate(map,
                MapBuilder.entry("lorem3", "sit amet"));

        Assertions.assertInstanceOf(ConcurrentHashMap.class, map);
        Assertions.assertInstanceOf(ConcurrentHashMap.class, map2);
        Assertions.assertInstanceOf(ConcurrentHashMap.class, map3);

        Assertions.assertSame(map, map3);
        Assertions.assertNotSame(map, map2);
        Assertions.assertEquals("sit amet 2 2", map2.get("lorem2_2"));
        Assertions.assertEquals("sit amet", map3.get("lorem3"));
    }

    @Test
    public void testPutAll() {
        Map<String, Object> map = new MapBuilder<String, Object>(
                MapBuilder.create(
                        MapBuilder.entry("Hello", "World")
                )
        )
                .put("World", "sekai")
                .putAll(
                        MapBuilder.create(LinkedHashMap::new,
                                MapBuilder.entry("h", "e")
                        )
                )
                .build();

        Assertions.assertEquals("e", map.get("h"));
        Assertions.assertEquals("sekai", map.get("World"));
        Assertions.assertEquals("World", map.get("Hello"));
    }

    @Test
    public void testCastingPerformance() {
        StopWatch stopWatch1 = StopWatch.createStarted();

        for (int i = 0; i < 10_000_000; i++) {
            Map<String, Object> customMap = MapBuilder.create(
                    MapBuilder.entry("lorem", "ipsum")
            );
        }

        log.info("NO_CASTING took {}", stopWatch1.formatTime());

        StopWatch stopWatch2 = StopWatch.createStarted();

        for (int i = 0; i < 10_000_000; i++) {
            Map<String, Object> customMap = MapBuilder.create(HashMap::new,
                    MapBuilder.entry("lorem", "ipsum")
            );
        }

        log.info("CASTING took {}", stopWatch2.formatTime());
    }

    @Test
    public void testMapBuilder() {
        // creating new map
        ConcurrentHashMap<String, Object> response = MapBuilder.create(ConcurrentHashMap::new, // specifying custom map implementation
                MapBuilder.entry("code", 200),
                MapBuilder.entry("status", "OK"),
                MapBuilder.entry("data", MapBuilder.create( // using the default (HashMap) implementation
                        MapBuilder.entry("person", MapBuilder.create(LinkedHashMap::new, // specifying custom map implementation
                                MapBuilder.entry("id", 42125124),
                                MapBuilder.entry("name", "John")
                        ))
                ))
        );

        // populating existing map, returns the same map
        // `response` is modified
        // `sameMap` and `response` refer to the same object
        ConcurrentMap<String, Object> sameMap = MapBuilder.populate(response,
                MapBuilder.entry("version", "1.4.2"),
                MapBuilder.entry("hasData", response.get("data") != null));

        // using alternative syntax
        Map<String, Object> response2 = new MapBuilder<String, Object>(ConcurrentHashMap::new)
                .put("code", 200)
                .put("data", new MapBuilder<>()
                        .put("personId", 42125124)
                        .build())
                .build();
    }

    private HashMap<String, Object> createMap() {
        return new HashMap<>();
    }

    private static class CustomMap extends HashMap<String, Object> {
        public CustomMap(Map<? extends String, ?> m) {
            super(m);
        }

        public String getLorem() {
            return (String) this.get("lorem");
        }

        public void setLorem(String lorem) {
            this.put("lorem", lorem);
        }
    }
}
