package dev.b37.mgutils.logging;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Objects;

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

        return ArrayUtils.addAll(prefixArgs, arguments);
    }

    private Object[] prefixArguments(Object argument) {
        Object[] prefixArgs = prefixArguments();

        if (prefixArgs == null || prefixArgs.length == 0) {
            return new Object[] {argument};
        }

        return ArrayUtils.addAll(prefixArgs, argument);
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
        if (!isTraceEnabled()) return;
        logger.trace(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void trace(String format, Object arg) {
        if (!isTraceEnabled()) return;
        logger.trace(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (!isTraceEnabled()) return;
        logger.trace(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (!isTraceEnabled()) return;
        logger.trace(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!isTraceEnabled()) return;
        logger.trace(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (!isTraceEnabled(marker)) return;
        logger.trace(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (!isTraceEnabled(marker)) return;
        logger.trace(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (!isTraceEnabled(marker)) return;
        logger.trace(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (!isTraceEnabled(marker)) return;
        logger.trace(marker, prefixMessage(format), prefixArguments(argArray));
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (!isTraceEnabled(marker)) return;
        logger.trace(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (!isDebugEnabled()) return;
        logger.debug(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void debug(String format, Object arg) {
        if (!isDebugEnabled()) return;
        logger.debug(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (!isDebugEnabled()) return;
        logger.debug(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!isDebugEnabled()) return;
        logger.debug(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!isDebugEnabled()) return;
        logger.debug(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (!isDebugEnabled(marker)) return;
        logger.debug(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (!isDebugEnabled(marker)) return;
        logger.debug(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (!isDebugEnabled(marker)) return;
        logger.debug(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (!isDebugEnabled(marker)) return;
        logger.debug(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (!isDebugEnabled(marker)) return;
        logger.debug(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if (!isInfoEnabled()) return;
        logger.info(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void info(String format, Object arg) {
        if (!isInfoEnabled()) return;
        logger.info(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (!isInfoEnabled()) return;
        logger.info(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        if (!isInfoEnabled()) return;
        logger.info(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        if (!isInfoEnabled()) return;
        logger.info(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (!isInfoEnabled(marker)) return;
        logger.info(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (!isInfoEnabled(marker)) return;
        logger.info(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (!isInfoEnabled(marker)) return;
        logger.info(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (!isInfoEnabled(marker)) return;
        logger.info(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (!isInfoEnabled(marker)) return;
        logger.info(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if (!isWarnEnabled()) return;
        logger.warn(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void warn(String format, Object arg) {
        if (!isWarnEnabled()) return;
        logger.warn(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (!isWarnEnabled()) return;
        logger.warn(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (!isWarnEnabled()) return;
        logger.warn(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (!isWarnEnabled()) return;
        logger.warn(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (!isWarnEnabled(marker)) return;
        logger.warn(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (!isWarnEnabled(marker)) return;
        logger.warn(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (!isWarnEnabled(marker)) return;
        logger.warn(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (!isWarnEnabled(marker)) return;
        logger.warn(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (!isWarnEnabled(marker)) return;
        logger.warn(marker, prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if (!isErrorEnabled()) return;
        logger.error(prefixMessage(msg), prefixArguments());
    }

    @Override
    public void error(String format, Object arg) {
        if (!isErrorEnabled()) return;
        logger.error(prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (!isErrorEnabled()) return;
        logger.error(prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        if (!isErrorEnabled()) return;
        logger.error(prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        if (!isErrorEnabled()) return;
        logger.error(prefixMessage(msg), prefixArguments(t));
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (!isErrorEnabled(marker)) return;
        logger.error(marker, prefixMessage(msg), prefixArguments());
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (!isErrorEnabled(marker)) return;
        logger.error(marker, prefixMessage(format), prefixArguments(arg));
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (!isErrorEnabled(marker)) return;
        logger.error(marker, prefixMessage(format), prefixArguments(arg1, arg2));
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (!isErrorEnabled(marker)) return;
        logger.error(marker, prefixMessage(format), prefixArguments(arguments));
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (!isErrorEnabled(marker)) return;
        logger.error(marker, prefixMessage(msg), prefixArguments(t));
    }
}
