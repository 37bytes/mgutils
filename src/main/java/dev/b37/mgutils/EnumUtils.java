package dev.b37.mgutils;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 3.4.0
 */
public final class EnumUtils {
    private EnumUtils() { }

    /**
     * Maps the enum members using the {@code keyMapper} provided to a map returned by the {@code mapFactory}.
     * @since 3.4.0
     */
    public static <K, T extends Enum<T>, M extends Map<K, T>> M mapEnum(Class<T> enumType,
                                                                        Function<? super T, ? extends K> keyMapper,
                                                                        Supplier<M> mapFactory) throws IllegalArgumentException {
        T[] enumMembers = enumType.getEnumConstants();
        return CollectionUtils.mapByKey(enumMembers, keyMapper, mapFactory);
    }

    /**
     * Maps the enum members using the {@code keyMapper} provided.
     * @since 3.4.0
     */
    public static <K, T extends Enum<T>> Map<K, T> mapEnum(Class<T> enumType,
                                                           Function<? super T, ? extends K> keyMapper) throws IllegalArgumentException {
        T[] enumMembers = enumType.getEnumConstants();
        return CollectionUtils.mapByKey(enumMembers, keyMapper);
    }

    /**
     * Maps the enum members by name.
     * @since 3.4.0
     */
    public static <T extends Enum<T>> Map<String, T> mapEnum(Class<T> enumType) throws IllegalArgumentException {
        T[] enumMembers = enumType.getEnumConstants();
        return CollectionUtils.mapByKey(enumMembers, T::name);
    }
}
