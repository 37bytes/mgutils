package ru.mrgrd56.mgutils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 1.1
 */
public final class StringUtils {
    private StringUtils() { }

    /**
     * Joins the {@code strings} that are not empty, placing the {@code delimiter} between them.
     *
     * @param delimiter The string separator.
     * @param strings The strings to be joined.
     */
    public static String joinNotBlank(CharSequence delimiter, CharSequence... strings) {
        return Stream.of(strings)
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .collect(Collectors.joining(delimiter));
    }
}
