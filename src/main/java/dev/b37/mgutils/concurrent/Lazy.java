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
public class Lazy<T> {
    private volatile T value;
    private Supplier<T> valueFactory;

    public Lazy(@NotNull Supplier<T> valueFactory) {
        Objects.requireNonNull(valueFactory, "valueFactory is null");
        this.valueFactory = valueFactory;
    }

    @NotNull
    public T get() {
        if (this.value == null) {
            synchronized (this) {
                if (this.value == null) {
                    this.value = this.valueFactory.get();
                    Objects.requireNonNull(this.value, "the value returned by valueFactory equals to null but must not");
                    this.valueFactory = null;
                }
            }
        }

        return this.value;
    }
}
