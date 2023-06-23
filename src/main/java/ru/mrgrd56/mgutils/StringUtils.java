package ru.mrgrd56.mgutils;

import java.util.Objects;
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

    /**
     * Joins the {@code objects} as strings that are not empty, placing the {@code delimiter} between them.
     *
     * @param delimiter The string separator.
     * @param objects The strings to be joined.
     * @since 1.4.0
     */
    public static String joinNotBlank(CharSequence delimiter, Object... objects) {
        return Stream.of(objects)
                .map(ObjectUtils::toString)
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Joins the {@code strings} that are not empty, placing the {@code delimiter} between them.<br>
     * Removes unnecessary spaces between substrings.
     *
     * @param delimiter The string separator.
     * @param strings The strings to be joined.
     * @since 1.4.0
     */
    public static String joinNotBlankTrimming(CharSequence delimiter, CharSequence... strings) {
        return Stream.of(strings)
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .map(CharSequence::toString)
                .map(String::trim)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Joins the {@code objects} as strings that are not empty, placing the {@code delimiter} between them.<br>
     * Removes unnecessary spaces between substrings.
     *
     * @param delimiter The string separator.
     * @param objects The strings to be joined.
     * @since 1.4.0
     */
    public static String joinNotBlankTrimming(CharSequence delimiter, Object... objects) {
        return Stream.of(objects)
                .map(ObjectUtils::toString)
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(delimiter));
    }
}
