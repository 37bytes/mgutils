package dev.b37.mgutils.delegate;

import dev.b37.mgutils.concurrent.Lazy;

/**
 * @since 1.1
 */
public class DefaultRunnableFactory implements RunnableFactory {
    private static final Lazy<DefaultRunnableFactory> instance = new Lazy<>(DefaultRunnableFactory::new);

    private DefaultRunnableFactory() { }

    public static DefaultRunnableFactory getInstance() {
        return instance.get();
    }

    @Override
    public Runnable create(Runnable runnable) {
        return runnable;
    }
}
