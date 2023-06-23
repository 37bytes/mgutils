package ru.mrgrd56.mgutils.common;

/**
 * A generic class to hold a reference to an object of type {@link T}.
 *
 * <p>This class can be used to store a mutable reference to an object.
 * It allows you to get and set the value of the reference.
 *
 * @param <T> the type of the object being referred to
 * @since 1.5.0
 */
public class Reference<T> {

    /**
     * The value being referred to.
     */
    private T value;

    /**
     * Constructs an empty Reference object.
     */
    public Reference() {
        this.value = null;
    }

    /**
     * Constructs a Reference object with the specified initial value.
     *
     * @param value the initial value to be stored in this reference
     */
    public Reference(T value) {
        this.value = value;
    }

    /**
     * Returns the current value stored in this reference.
     *
     * @return the current value of the reference
     */
    public T get() {
        return value;
    }

    /**
     * Sets the value stored in this reference to the specified value.
     *
     * @param value the value to be stored in this reference
     */
    public void set(T value) {
        this.value = value;
    }
}