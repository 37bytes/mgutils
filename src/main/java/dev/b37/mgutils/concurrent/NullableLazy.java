package dev.b37.mgutils.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Thread-safe lazy initialization implementation. Null value can be produced.
 * @see Lazy
 * @since 1.5.0
 */
public class NullableLazy<T> {
    private volatile boolean hasValue;
    private volatile T value;
    private Supplier<T> valueFactory;

    public NullableLazy(@NotNull Supplier<T> valueFactory) {
        Objects.requireNonNull(valueFactory, "valueFactory is null");
        this.valueFactory = valueFactory;
    }

    @Nullable
    public T get() {
        if (!this.hasValue) {
            synchronized (this) {
                if (!this.hasValue) {
                    this.value = this.valueFactory.get();
                    this.hasValue = true;
                    this.valueFactory = null;
                }
            }
        }

        return this.value;
    }
}