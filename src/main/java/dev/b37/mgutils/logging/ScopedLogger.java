package dev.b37.mgutils.logging;

import dev.b37.mgutils.random.RandomIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * ScopedLogger is a class used for adding scope to logging operations.
 * This class helps to group related log messages together by adding a scope name and a unique scope ID to each log message.
 * This is also useful when there are nested scopes and one needs to track the flow of control in the logs.
 * <p>
 * The scope name typically represents a method or block of code. The scope ID is a unique identifier for each new instance of ScopedLogger,
 * created at the time of a new invocation of a block of code represented by the scope name.
 * <p>
 * When a ScopedLogger is created from another ScopedLogger, all the scope names and IDs are included in the log messages. This helps to track nested and interdependent log entries.
 * <br><br>
 * <p>
 * For example:
 * <pre>
 * public class Example {
 *     Logger log = LoggerFactory.getLogger(this.getClass());
 *
 *     public Post fetchPost(int postId) {
 *         Logger logger = ScopedLogger.of(log, "fetchPost:");
 *
 *         logger.trace("fetching postId={}", postId);
 *
 *         Response response;
 *         try {
 *             response = http.request("http://localhost:8080/posts/" + postId);
 *             logger.trace("got the response {}", response);
 *         } catch (Exception e) {
 *             logger.error("unable to fetch the person", e);
 *             throw e;
 *         }
 *
 *         Post post = response.getBody();
 *         logger.trace("got the post {}", post);
 *
 *         for (Comment comment : post.getComments()) {
 *             Logger commentLogger = ScopedLogger.of(logger, "fetchComment:", comment.getId());
 *
 *             commentLogger.trace("fetching commentId={}", comment.getId());
 *
 *             Response commentResponse;
 *
 *             try {
 *                 commentResponse = http.request("http://localhost:8080/posts/" + postId);
 *                 commentLogger.trace("got the response {}", response);
 *             } catch (Exception e) {
 *                 commentLogger.error("unable to fetch the comment", e);
 *                 throw e;
 *             }
 *
 *             commentsHelper.populateComment(comment, commentResponse.getBody());
 *
 *             commentLogger.trace("populated comment");
 *         }
 *
 *         logger.trace("successfully fetched the post");
 *
 *         return post;
 *     }
 * }
 *
 * </pre>
 * Calling {@code Example#fetchPost} will output:
 * <pre>
 * [48y9zeqq2c2d] fetchPost: fetching postId=2690201
 * [48y9zeqq2c2d] fetchPost: got the response Response@3769fecf
 * [48y9zeqq2c2d] fetchPost: got the post Post@5d244b79
 * [48y9zeqq2c2d] fetchPost: [52] fetchComment: fetching commentId=52
 * [48y9zeqq2c2d] fetchPost: [52] fetchComment: got the response Response@14cb4e66
 * [48y9zeqq2c2d] fetchPost: [52] fetchComment: populated comment
 * [48y9zeqq2c2d] fetchPost: [63] fetchComment: fetching commentId=63
 * [48y9zeqq2c2d] fetchPost: [63] fetchComment: got the response Response@4a7b18fd
 * [48y9zeqq2c2d] fetchPost: [63] fetchComment: populated comment
 * [48y9zeqq2c2d] fetchPost: successfully fetched the post
 * </pre>
 * Second calling {@code Example#fetchPost} will output logs with different random scopeId:
 * <pre>
 * [juo0n1nal8m5] fetchPost: fetching postId=2690201
 * [juo0n1nal8m5] fetchPost: got the response Response@3769fecf
 * ...
 * [juo0n1nal8m5] fetchPost: [63] fetchComment: populated comment
 * [juo0n1nal8m5] fetchPost: successfully fetched the post
 * </pre>
 * @since 1.0
 */
public class ScopedLogger extends PrefixedLogger {
    private static final RandomIdGenerator idGenerator = RandomIdGenerator.getInstance();

    private final String prefixTemplate;
    private final Object[] prefixArgs;

    protected ScopedLogger(Logger logger, CharSequence prefixTemplate, Object[] prefixArgs) {
        super(logger);
        this.prefixTemplate = prefixTemplate == null ? null : prefixTemplate.toString();
        this.prefixArgs = prefixArgs;
    }

    protected ScopedLogger(Logger logger, CharSequence scopeName, Object scopeId) {
        this(logger, createPrefixTemplate(scopeName, scopeId), createPrefixArgs(scopeId));
    }

    protected ScopedLogger(ScopedLogger outerScopedLogger, CharSequence scopeName, Object scopeId) {
        this(outerScopedLogger.logger,
                outerScopedLogger.prefixTemplate + createPrefixTemplate(scopeName, scopeId),
                createPrefixArgs(outerScopedLogger.prefixArgs, scopeId));
    }

    /**
     * Creates a new {@link ScopedLogger} with a generated unique scope ID.
     *
     * @param logger The {@link Logger} instance to be wrapped by {@link ScopedLogger}.
     * @return The new {@link ScopedLogger} instance.
     */
    @NotNull
    public static Logger of(@NotNull Logger logger) {
        return createLogger(logger, null, createScopeId());
    }

    /**
     * Creates a new {@link ScopedLogger} with a given scope name and a generated unique scope ID.
     *
     * @param logger The {@link Logger} instance to be wrapped by {@link ScopedLogger}.
     * @param scopeName The scope name, typically representing a method or block of code.
     * @return The new {@link ScopedLogger} instance.
     */
    @NotNull
    public static Logger of(@NotNull Logger logger, CharSequence scopeName) {
        return createLogger(logger, scopeName, createScopeId());
    }

    /**
     * Creates a new {@link ScopedLogger} with a given scope name and scope ID.
     *
     * @param logger The {@link Logger} instance to be wrapped by {@link ScopedLogger}.
     * @param scopeName The scope name, typically representing a method or block of code.
     * @param scopeId The scope ID, typically used to identify the invocation of a block of code represented by scopeName.
     * @return The new {@link ScopedLogger} instance.
     */
    @NotNull
    public static Logger of(@NotNull Logger logger, CharSequence scopeName, @Nullable Object scopeId) {
        return createLogger(logger, scopeName, scopeId);
    }

    /**
     * Generates a unique ID for a new scope.
     *
     * @return The unique scope ID.
     */
    public static Object createScopeId() {
        return createScopeId(ThreadLocalRandom.current());
    }

    /**
     * Generates a unique ID for a new scope using the passed {@link ThreadLocalRandom} instance.
     *
     * @return The unique scope ID.
     * @since 1.2.0 - signature changed
     */
    public static Object createScopeId(ThreadLocalRandom random) {
        return idGenerator.createIdentifier(random);
    }

    /**
     * Checks whether the given {@link Logger} is a {@link ScopedLogger}.
     *
     * @param logger The {@link Logger} instance to be checked.
     * @return {@code true} if the Logger is a ScopedLogger, {@code false} otherwise.
     */
    public static boolean isScopedLogger(Logger logger) {
        return logger instanceof ScopedLogger;
    }

    protected static Logger createLogger(Logger logger, @Nullable CharSequence scopeName, Object scopeId) {
        if (logger instanceof ScopedLogger) {
            return new ScopedLogger((ScopedLogger) logger, scopeName, scopeId);
        }

        return new ScopedLogger(logger, scopeName, scopeId);
    }

    @Override
    protected String getPrefixTemplate() {
        return prefixTemplate;
    }

    @Override
    protected Object[] getPrefixArgs() {
        return prefixArgs;
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
}
