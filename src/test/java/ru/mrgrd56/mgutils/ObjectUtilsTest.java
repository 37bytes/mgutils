package ru.mrgrd56.mgutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectUtilsTest {
    @Test
    public void testToString() {
        Assertions.assertNull(ObjectUtils.toString(null));
        Assertions.assertEquals("", ObjectUtils.toString(""));
        Assertions.assertEquals("hellO ", ObjectUtils.toString("hellO "));
        Assertions.assertEquals("w0rld", ObjectUtils.toString(new Object() {
            @Override
            public String toString() {
                return "w0rld";
            }
        }));
    }
}
