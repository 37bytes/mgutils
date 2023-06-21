package ru.mrgrd56.mgutils.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.stream.Stream;

@Deprecated
abstract class AbstractScopedLogger extends PrefixedLogger {
    private final String prefixTemplate;
    private final Object[] prefixArgs;

    private AbstractScopedLogger(@NotNull Logger logger, CharSequence prefixTemplate, Object[] prefixArgs) {
        super(logger);
        this.prefixTemplate = prefixTemplate == null ? null : prefixTemplate.toString();
        this.prefixArgs = prefixArgs;
    }

    public AbstractScopedLogger(@NotNull Logger logger, Object scopeId, CharSequence scopeName) {
        this(logger, createPrefixTemplate(scopeName, scopeId), createPrefixArgs(scopeId));
    }

    protected AbstractScopedLogger(@NotNull AbstractScopedLogger outerScopeLogger, Object scopeId, CharSequence scopeName) {
        this(outerScopeLogger.logger,
                outerScopeLogger.prefixTemplate + createPrefixTemplate(scopeName, scopeId),
                createPrefixArgs(outerScopeLogger.prefixArgs, scopeId));
    }

    private static Object[] createPrefixArgs(Object scopeId) {
        if (scopeId == null) {
            return new Object[0];
        }

        return new Object[] {scopeId};
    }

    private static Object[] createPrefixArgs(Object[] outerPrefixArgs, Object scopeId) {
        if (outerPrefixArgs == null || outerPrefixArgs.length == 0) {
            return createPrefixArgs(scopeId);
        }

        return Stream.concat(Arrays.stream(outerPrefixArgs), Arrays.stream(createPrefixArgs(scopeId))).toArray();
    }

    private static String createPrefixTemplate(@Nullable CharSequence scopeName, @Nullable Object scopeId) {
        String idTemplate = scopeId == null ? "" : "[{}] ";

        if (scopeName == null) {
            return idTemplate;
        }

        return idTemplate + scopeName + " ";
    }

    @Override
    protected String getPrefixTemplate() {
        return prefixTemplate;
    }

    @Override
    protected Object[] getPrefixArgs() {
        return prefixArgs;
    }
}
