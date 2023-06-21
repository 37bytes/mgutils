package ru.mrgrd56.mgutils.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;

public class ScopedLoggerFactory {
    @NotNull
    private final Logger logger;
    @Nullable
    private final CharSequence scopeName;

    public ScopedLoggerFactory(@NotNull Logger logger) {
        this(logger, null);
    }

    public ScopedLoggerFactory(@NotNull Logger logger, @Nullable CharSequence scopeName) {
        Objects.requireNonNull(logger);

        this.logger = logger;
        this.scopeName = scopeName;
    }

    public Logger createLogger() {
        return ScopedLogger.of(logger, scopeName);
    }

    public Logger createLogger(@Nullable Object scopeId) {
        return ScopedLogger.of(logger, scopeName, scopeId);
    }

    public Logger createLogger(@Nullable CharSequence scopeName, @Nullable Object scopeId) {
        return ScopedLogger.of(logger, scopeName, scopeId);
    }

    public Logger createLogger(@Nullable CharSequence scopeName) {
        return ScopedLogger.of(logger, scopeName);
    }
}
