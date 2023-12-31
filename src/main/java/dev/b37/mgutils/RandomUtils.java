package dev.b37.mgutils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @since 1.1
 */
public final class RandomUtils {
    private RandomUtils() { }

    /**
     * Generates a random boolean with the specified {@code chance} of getting {@code true}.
     * @param chance Chance to get {@code true}. A decimal number from 0 to 1.
     * @throws IllegalArgumentException Thrown if the {@code chance} provided is not in the required range.
     */
    public static boolean nextBoolean(double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("chance must be in range from 0 to 1");
        }

        if (chance == 0) {
            return false;
        }

        if (chance == 1) {
            return true;
        }

        return chance > random().nextDouble();
    }

    /**
     * Returns a random item of the {@code list}
     */
    public static <T> T nextItem(List<T> list) {
        return list.get(random().nextInt(list.size()));
    }

    /**
     * Returns a random element of the {@code array}
     */
    public static <T> T nextItem(T[] array) {
        return array[random().nextInt(array.length)];
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }
}