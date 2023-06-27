package ru.mrgrd56.mgutils.delegate;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @since 1.8.0
 */
@FunctionalInterface
public interface MapSupplier<M extends Map<?, ?>> extends Supplier<M> {
}