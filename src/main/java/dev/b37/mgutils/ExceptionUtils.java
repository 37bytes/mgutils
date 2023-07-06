package dev.b37.mgutils;

/**
 * @since 1.9.0
 */
public final class ExceptionUtils {
    private ExceptionUtils() { }

    /**
     * Returns a {@link RuntimeException} from the provided {@code exception}.<br>
     * If the initial exception already is a {@link RuntimeException}, returns it casting to {@link RuntimeException}.<br>
     * If it's not, wraps it using the {@link RuntimeException#RuntimeException(Throwable)} constructor.
     * @since 1.9.0
     */
    public static RuntimeException asRuntimeException(Throwable exception) {
        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        }

        return new RuntimeException(exception);
    }
}
