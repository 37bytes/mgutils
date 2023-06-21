package ru.mrgrd56.mgutils.logging;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @since 1.0
 */
public abstract class PrefixedLogger implements Logger {
    protected final Logger logger;

    public PrefixedLogger(@NotNull Logger logger) {
        Objects.requireNonNull(logger, "logger must not be null");
        this.logger = logger;
    }

    protected abstract String getPrefixTemplate();
    protected abstract Object[] getPrefixArgs();

    private String prefixMessage(String message) {
        return getPrefixTemplate() + message;
    }

    private Object[] prefixArguments(Object[] arguments) {
        Object[] prefixArgs = prefixArguments();

        if (prefixArgs == null || prefixArgs.length == 0) {
            return arguments;
        }

        return Stream.concat(Arrays.stream(prefixArgs), Arrays.stream(arguments)).toArray();
    }

    private Object[] prefixArguments(Object argument) {
        Object[] prefixArgs = prefixArguments();

        if (prefixArgs == null || prefixArgs.length == 0) {
            return new Object[] {argument};
        }

        return Stream.concat(Arrays.stream(prefixArgs), Stream.of(argument)).toArray();
    }

    private Object[] prefixArguments() {
        return getPrefixArgs();
    }

    private Object[] prefixArguments(Object argument1, Object argument2) {
        return prefixArguments(new Object[] {argument1, argument2});
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.trace(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(marker, prefixMessage(format), prefixArguments(argArray));
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        logger.debug(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        logger.info(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        logger.warn(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        logger.error(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(marker, prefixMessage(msg), prefixArguments(t));
    }
}
