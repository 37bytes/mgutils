package ru.mrgrd56.mgutils.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;
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
 * [6b635a25b53cb0ba] fetchPost: fetching postId=2690201
 * [6b635a25b53cb0ba] fetchPost: got the response Response@3769fecf
 * [6b635a25b53cb0ba] fetchPost: got the post Post@5d244b79
 * [6b635a25b53cb0ba] fetchPost: [52] fetchComment: fetching commentId=52
 * [6b635a25b53cb0ba] fetchPost: [52] fetchComment: got the response Response@14cb4e66
 * [6b635a25b53cb0ba] fetchPost: [52] fetchComment: populated comment
 * [6b635a25b53cb0ba] fetchPost: [63] fetchComment: fetching commentId=63
 * [6b635a25b53cb0ba] fetchPost: [63] fetchComment: got the response Response@4a7b18fd
 * [6b635a25b53cb0ba] fetchPost: [63] fetchComment: populated comment
 * [6b635a25b53cb0ba] fetchPost: [632] fetchComment: fetching commentId=632
 * [6b635a25b53cb0ba] fetchPost: [632] fetchComment: got the response Response@321c18fb
 * [6b635a25b53cb0ba] fetchPost: [632] fetchComment: populated comment
 * [6b635a25b53cb0ba] fetchPost: [21] fetchComment: fetching commentId=21
 * [6b635a25b53cb0ba] fetchPost: [21] fetchComment: got the response Response@4d9ce242
 * [6b635a25b53cb0ba] fetchPost: [21] fetchComment: populated comment
 * [6b635a25b53cb0ba] fetchPost: successfully fetched the post
 * </pre>
 */
public class ScopedLogger extends PrefixedLogger {
    private static final Random random = new Random();

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
     * Creates a new ScopedLogger with a generated unique scope ID.
     *
     * @param logger The Logger instance to be wrapped by ScopedLogger.
     * @return The new ScopedLogger instance.
     */
    @NotNull
    public static Logger of(@NotNull Logger logger) {
        return createLogger(logger, null, createScopeId());
    }

    /**
     * Creates a new ScopedLogger with a given scope name and a generated unique scope ID.
     *
     * @param logger The Logger instance to be wrapped by ScopedLogger.
     * @param scopeName The scope name, typically representing a method or block of code.
     * @return The new ScopedLogger instance.
     */
    @NotNull
    public static Logger of(@NotNull Logger logger, CharSequence scopeName) {
        return createLogger(logger, scopeName, createScopeId());
    }

    /**
     * Creates a new ScopedLogger with a given scope name and scope ID.
     *
     * @param logger The Logger instance to be wrapped by ScopedLogger.
     * @param scopeName The scope name, typically representing a method or block of code.
     * @param scopeId The scope ID, typically used to identify the invocation of a block of code represented by scopeName.
     * @return The new ScopedLogger instance.
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
        long value = random.nextLong() & Long.MAX_VALUE;
        return String.format("%016x", value);
    }

    /**
     * Checks whether the given Logger is a ScopedLogger.
     *
     * @param logger The Logger instance to be checked.
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

    static class Example {
        public static void main(String[] args) {
            new Example().fetchPost(8192557);
        }

        Logger log = LoggerFactory.getLogger(this.getClass());

        public void fetchPost(int postId) {
            Logger logger = ScopedLogger.of(log, "fetchPost:");

            logger.trace("fetching postId={}", postId);

//                Response response;
//                try {
//                    response = http.request("http://localhost:8080/posts/" + postId);
            logger.trace("got the response {}", new Object());
//                } catch (Exception e) {
//                    logger.error("unable to fetch the person", e);
//                    throw e;
//                }

//                Post post = response.getBody();
            logger.trace("got the post {}", new Object());

            for (int commentId : new int[] {52, 63, 632, 21}) {
                Logger commentLogger = ScopedLogger.of(logger, "fetchComment:", commentId);

                commentLogger.trace("fetching commentId={}", commentId);

//                    Response commentResponse;

//                    try {
//                        commentResponse = http.request("http://localhost:8080/posts/" + postId);
                commentLogger.trace("got the response {}", new Object());
//                    } catch (Exception e) {
//                        commentLogger.error("unable to fetch the comment", e);
//                        throw e;
//                    }

//                    commentsHelper.populateComment(comment, commentResponse.getBody());

                commentLogger.trace("populated comment");
            }

            logger.trace("successfully fetched the post");

//                return post;
        }
    }
}
