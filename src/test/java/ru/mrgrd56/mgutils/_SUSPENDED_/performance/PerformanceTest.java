package ru.mrgrd56.mgutils._SUSPENDED_.performance;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * //TODO v2.1.0
 */
public class PerformanceTest {
    private static final String ONE_DEEPNESS_PREFIX = "  ";

    private final String name;
    private final Logger logger;
    private final StopWatch stopWatch;
    private final StopWatch splitStopWatch;
    private State state = State.CREATED;
    private long operationTime;

    private final List<PerformanceTest> nestedTests = Collections.synchronizedList(new ArrayList<>());
    private final PerformanceTest outerTest;
    private final int deepness;
    private final String deepnessPrefix;
    private final boolean isSplit;

    private PerformanceTest(String name) {
        this(name, NOPLogger.NOP_LOGGER);
    }

    private PerformanceTest(String name, Logger logger) {
        this(name, logger, null, 0, "");
    }

    protected PerformanceTest(String name, Logger logger, PerformanceTest outerTest, int deepness, String deepnessPrefix) {
        this(name, logger, outerTest, deepness, deepnessPrefix, StopWatch.create(), StopWatch.create(), false);
    }

    protected PerformanceTest(String name, Logger logger, PerformanceTest outerTest, int deepness, String deepnessPrefix, StopWatch stopWatch, StopWatch splitStopWatch, boolean isSplit) {
        this.name = name;
        this.logger = logger;
        this.outerTest = outerTest;
        this.deepness = deepness;
        this.deepnessPrefix = deepnessPrefix;
        this.stopWatch = stopWatch;
        this.splitStopWatch = splitStopWatch;
        this.isSplit = isSplit;
    }

    public static PerformanceTest create(String name) {
        return new PerformanceTest(name);
    }

    public static PerformanceTest create(String name, Logger logger) {
        return new PerformanceTest(name, logger);
    }

    public static PerformanceTest createStarted(String name) {
        return create(name).start();
    }

    public static PerformanceTest createStarted(String name, Logger logger) {
        return create(name, logger).start();
    }

    public PerformanceTest nested(String name) {
        if (isSplit) {
            throw new UnsupportedOperationException("Creating nested tests for a split is not allowed");
        }

        PerformanceTest nestedTest = new PerformanceTest(
                name,
                logger,
                this,
                deepness + 1,
                deepnessPrefix + ONE_DEEPNESS_PREFIX);

        nestedTests.add(nestedTest);
        return nestedTest;
    }

    public PerformanceTest nestedStarted(String name) {
        return nested(name).start();
    }

    public PerformanceTest split(String name) {
        if (isSplit) {
            throw new UnsupportedOperationException("Splitting a split is not allowed");
        }

        PerformanceTest nestedTest = new PerformanceTest(
                name,
                logger,
                this,
                deepness + 1,
                deepnessPrefix + ONE_DEEPNESS_PREFIX,
                null,
                null,
                true);

        nestedTest.operationTime = splitStopWatch.getTime();
        nestedTest.state = State.FINISHED;
        splitStopWatch.reset();
        splitStopWatch.start();
        nestedTest.logSplit();

        nestedTests.add(nestedTest);
        return nestedTest;
    }

    public PerformanceTest start() {
        if (state != State.CREATED) {
            throw new IllegalStateException("Unable to start the test");
        }

        state = State.STARTED;

        logStart();
        stopWatch.start();
        splitStopWatch.start();
        return this;
    }

    public PerformanceTest finish() {
        stop();

        logFinish();
        return this;
    }

    public List<PerformanceTest> getNestedTests() {
        return Collections.unmodifiableList(nestedTests);
    }

    @Nullable
    public PerformanceTest getOuterTest() {
        return outerTest;
    }

    public boolean isFinished() {
        return state == State.FINISHED;
    }

    public long getOperationTime() {
        if (state != State.FINISHED) {
            throw new IllegalStateException("The test has not been finished yet");
        }

        return operationTime;
    }

    public String formatOperationTime() {
        return DurationFormatUtils.formatDurationHMS(getOperationTime());
    }

    public String getName() {
        return name;
    }

    protected void logStart() {
        logger.debug("{}{}  started", getDeepnessPrefix(), getName());
    }

    protected void logFinish() {
        logger.debug("{}{}  {}", getDeepnessPrefix(), getName(), formatOperationTime());
    }

    protected void logSplit() {
        logger.debug("{}{}  {}", getDeepnessPrefix(), getName(), formatOperationTime());
    }

    protected boolean isSplit() {
        return isSplit;
    }

    protected String getDeepnessPrefix() {
        return deepnessPrefix;
    }

    protected enum State {
        CREATED,
        STARTED,
        FINISHED
    }

    private void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("Unable to stop the test");
        }

        state = State.FINISHED;

        if (outerTest != null && outerTest.splitStopWatch != null) {
            outerTest.splitStopWatch.reset();
            outerTest.splitStopWatch.start();
        }

        stopWatch.stop();
        operationTime = stopWatch.getTime();
    }
}
