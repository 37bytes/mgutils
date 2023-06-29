package ru.mrgrd56.mgutils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 1.1
 */
public final class StringUtils {
    private StringUtils() { }

    /**
     * Replaces the first occurrence of the character {@code oldChar} in the {@code string} with the character {@code newChar}.
     *
     * @param string  The original string in which the replacement needs to be performed.
     * @param oldChar The character that needs to be replaced.
     * @param newChar The character to replace with.
     * @return The string with the character replaced.
     * @since 1.9.0
     */
    public static String replaceOnce(String string, char oldChar, char newChar) {
        int index = string.indexOf(oldChar);

        if (index == -1) {
            return string;
        }

        char[] chars = string.toCharArray();
        chars[index] = newChar;
        return String.valueOf(chars);
    }

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
