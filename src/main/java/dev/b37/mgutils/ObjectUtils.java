package dev.b37.mgutils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.4.0
 */
public final class ObjectUtils {
    private ObjectUtils() { }

    @Nullable
    @Contract("null -> null")
    public static String toString(@Nullable Object object) {
        return object == null ? null : object.toString();
    }
}
