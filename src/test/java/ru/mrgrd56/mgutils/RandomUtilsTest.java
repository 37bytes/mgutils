package ru.mrgrd56.mgutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomUtilsTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testNextBooleanWithChance() {
        int trueCount = 0;
        int falseCount = 0;

        for (int i = 0; i < 5000; i++) {
            if (RandomUtils.nextBoolean(0.33)) {
                trueCount++;
            } else {
                falseCount++;
            }
        }

        log.info("true: {}, false: {}", trueCount, falseCount);

        Assertions.assertTrue(trueCount < falseCount);
        Assertions.assertNotEquals(0, trueCount);
        Assertions.assertNotEquals(0, falseCount);
    }

    @Test
    public void testNextBooleanWithTrue() {
        int trueCount = 0;
        int falseCount = 0;

        for (int i = 0; i < 10000; i++) {
            if (RandomUtils.nextBoolean(1)) {
                trueCount++;
            } else {
                falseCount++;
            }
        }

        log.info("true: {}, false: {}", trueCount, falseCount);

        Assertions.assertEquals(10000, trueCount);
        Assertions.assertEquals(0, falseCount);
    }

    @Test
    public void testNextBooleanWithFalse() {
        int trueCount = 0;
        int falseCount = 0;

        for (int i = 0; i < 10000; i++) {
            if (RandomUtils.nextBoolean(0)) {
                trueCount++;
            } else {
                falseCount++;
            }
        }

        log.info("true: {}, false: {}", trueCount, falseCount);

        Assertions.assertEquals(0, trueCount);
        Assertions.assertEquals(10000, falseCount);
    }
}
