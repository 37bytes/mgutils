package dev.b37.mgutils.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Thread-safe lazy initialization implementation. The value produced is always non-null.
 * <p>
 * Can be used to implement the Singleton pattern.<br>
 * Example:
 * <pre>
 * public class MyClass {
 *     private static final Lazy&lt;MyClass&gt; instance = new Lazy&lt;&gt;(MyClass::new);
 *
 *     private MyClass() { }
 *
 *     public static MyClass getInstance() {
 *         return instance.get();
 *     }
 * }</pre>
 * @see NullableLazy
 * @since 1.5.0
 */
public class Lazy<T> implements Supplier<T> {
    private volatile boolean hasValue;
    private volatile T value;
    private Supplier<T> valueFactory;

    public Lazy(@NotNull Supplier<T> valueFactory) {
        this.valueFactory = Objects.requireNonNull(valueFactory, "valueFactory is null");
    }

    /**
     * When called for the first tame, calls the {@code valueFactory} and stores the returned value, returning it.<br>
     * As soon as the value is stored, it's just returned as is without calling the {@code valueFactory}.
     * @return The value returned by the {@code valueFactory}
     * @throws NullPointerException Thrown if {@code valueFactory} returns {@code null}
     */
    @NotNull
    @Override
    public T get() {
        if (!this.hasValue) {
            synchronized (this) {
                if (!this.hasValue) {
                    this.value = Objects.requireNonNull(this.valueFactory.get(),
                            "the value returned by valueFactory equals to null but must not");
                    this.hasValue = true;
                    this.valueFactory = null;
                }
            }
        }

        return this.value;
    }
}
