package dev.b37.mgutils.random;

import dev.b37.mgutils.concurrent.Lazy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class for generating random identifiers.
 * <p>
 * The RandomIdGenerator class is responsible for generating unique random identifiers.
 * Each identifier is a 12-character long string, consisting of alphanumeric characters.
 * The characters are generated based on a 36-radix (0-9, a-z) representation of a random long value.
 * <p>
 * The class supports generating identifiers using instances of {@link java.util.concurrent.ThreadLocalRandom}
 * and custom implementations of a {@link BoundedLongRandom}.
 * <p>
 * Example usage:
 * <pre>
 * RandomIdGenerator generator = RandomIdGenerator.getInstance();
 * String randomIdentifier = generator.createIdentifier();</pre>
 * Examples of identifiers:
 * <code>t9z8pfzh0hrd</code>, <code>356wqj2tw2xb</code>
 *
 * <br><br><p>
 * As of version 1.5.0 this class is singleton. Its instance can be obtained using the {@link #getInstance()} method.
 *
 * @since 1.2.0
 */
public class RandomIdGenerator {
    private static final Lazy<RandomIdGenerator> instance = new Lazy<>(RandomIdGenerator::new);

    private RandomIdGenerator() { }

    /**
     * @since 1.5.0
     */
    public static RandomIdGenerator getInstance() {
        return instance.get();
    }

    public static final int IDENTIFIER_LENGTH = 12;
    private static final long IDENTIFIER_MAX_VALUE = 4738381338321616895L; // zzzzzzzzzzzz in 36-radix
    private static final long IDENTIFIER_BOUND = IDENTIFIER_MAX_VALUE + 1L;
    private static final int IDENTIFIER_RADIX = 36;

    private static final String[] paddings = {
            /* [0]  */ "000000000000",
            /* [1]  */ "00000000000",
            /* [2]  */ "0000000000",
            /* [3]  */ "000000000",
            /* [4]  */ "00000000",
            /* [5]  */ "0000000",
            /* [6]  */ "000000",
            /* [7]  */ "00000",
            /* [8]  */ "0000",
            /* [9]  */ "000",
            /* [10] */ "00",
            /* [11] */ "0"
    };

    public String createIdentifier() {
        return createIdentifier(ThreadLocalRandom.current());
    }

    public String createIdentifier(ThreadLocalRandom random) {
        long value = random.nextLong(IDENTIFIER_BOUND);
        return formatIdentifier(value);
    }

    public String createIdentifier(BoundedLongRandom random) {
        long value = random.nextLong(IDENTIFIER_BOUND);
        return formatIdentifier(value);
    }

    protected String formatIdentifier(long value) {
        String unpreparedString = Long.toUnsignedString(value, IDENTIFIER_RADIX);

        if (unpreparedString.length() == IDENTIFIER_LENGTH) {
            return unpreparedString;
        }

        return padString(paddings[unpreparedString.length()], unpreparedString);
    }

    private static String padString(String padding, String stringToPad) {
        return new StringBuilder(padding).append(stringToPad).toString();
    }
}
