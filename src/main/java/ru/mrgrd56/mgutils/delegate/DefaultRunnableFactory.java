package ru.mrgrd56.mgutils.delegate;

/**
 * @since 1.1
 */
public class DefaultRunnableFactory implements RunnableFactory {
    private static DefaultRunnableFactory instance;

    private DefaultRunnableFactory() { }

    public static DefaultRunnableFactory getInstance() {
        if (instance == null) {
            instance = new DefaultRunnableFactory();
        }

        return instance;
    }

    @Override
    public Runnable create(Runnable runnable) {
        return runnable;
    }
}
