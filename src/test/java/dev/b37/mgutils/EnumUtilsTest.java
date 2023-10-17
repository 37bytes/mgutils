package dev.b37.mgutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnumUtilsTest {
    @Test
    public void testMapEnum() {
        Map<Integer, ? extends Key> keyMap = EnumUtils.mapEnum(WindowsKey.class, Key::getCode);
        Assertions.assertEquals(WindowsKey.values().length, keyMap.size());
        Assertions.assertSame(WindowsKey.DOWN, keyMap.get(3));
        Assertions.assertNotSame(WindowsKey.DOWN, keyMap.get(4));

        Assertions.assertInstanceOf(LinkedHashMap.class, EnumUtils.mapEnum(WindowsKey.class, WindowsKey::getCode, LinkedHashMap::new));

        Map<String, WindowsKey> keyMapByName = EnumUtils.mapEnum(WindowsKey.class);
        Assertions.assertEquals(WindowsKey.values().length, keyMap.size());
        Assertions.assertEquals(WindowsKey.LEFT, keyMapByName.get("LEFT"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EnumUtils.mapEnum(LinuxKey.class, LinuxKey::getCode);
        });
    }

    private interface Key {
        int getCode();
    }

    private enum WindowsKey implements Key {
        UP(1),
        LEFT(2),
        DOWN(3),
        RIGHT(4);

        private final int code;

        WindowsKey(int code) {
            this.code = code;
        }

        @Override
        public int getCode() {
            return code;
        }
    }

    private enum LinuxKey implements Key {
        UP(1),
        LEFT(2),
        DOWN(3),
        RIGHT(4),
        SHIFT(5),
        LEFT_SHIFT(5);

        private final int code;

        LinuxKey(int code) {
            this.code = code;
        }

        @Override
        public int getCode() {
            return code;
        }
    }
}
