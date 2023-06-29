package ru.mrgrd56.mgutils.performance;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.9.0
 */
public class PerformanceTest {
    private static final String ONE_DEEPNESS_PREFIX = "║ ";
//    private static final String ONE_DEEPNESS_SPLIT_FIRST_PREFIX = "║";
//    private static final String ONE_DEEPNESS_SPLIT_PREFIX = " ";

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

    private PerformanceTest(String name) {
        this(name, NOPLogger.NOP_LOGGER);
    }

    private PerformanceTest(String name, Logger logger) {
        this(name, logger, null, 0, "");
    }

    private PerformanceTest(String name, Logger logger, PerformanceTest outerTest, int deepness, String deepnessPrefix) {
        this(name, logger, outerTest, deepness, deepnessPrefix, StopWatch.create(), StopWatch.create());
    }

    private PerformanceTest(String name, Logger logger, PerformanceTest outerTest, int deepness, String deepnessPrefix, StopWatch stopWatch, StopWatch splitStopWatch) {
        this.name = name;
        this.logger = logger;
        this.outerTest = outerTest;
        this.deepness = deepness;
        this.deepnessPrefix = deepnessPrefix;
        this.stopWatch = stopWatch;
        this.splitStopWatch = splitStopWatch;
    }

    public static PerformanceTest create(String name) {
        return new PerformanceTest(name);
    }

    public static PerformanceTest create(String name, Logger logger) {
        return new PerformanceTest(name, logger);
    }

    public PerformanceTest nested(String name) {
        PerformanceTest nestedTest = new PerformanceTest(
                name,
                logger,
                this,
                deepness + 1,
                deepnessPrefix + ONE_DEEPNESS_PREFIX);

        nestedTests.add(nestedTest);
        return nestedTest;
    }

    public PerformanceTest split(String name) {
        PerformanceTest nestedTest = new PerformanceTest(
                name,
                logger,
                this,
                deepness + 1,
                deepnessPrefix + ONE_DEEPNESS_PREFIX,
                null,
                null);

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

    public long getOperationTime() {
        if (state != State.FINISHED) {
            throw new IllegalStateException("The test has not been finished yet");
        }

        return operationTime;
    }

    public String formatOperationTime() {
        return DurationFormatUtils.formatDurationHMS(getOperationTime());
    }

    protected void logStart() {
        if (deepness == 0) {
            logger.debug("{}╔═══╣{}", deepnessPrefix, name);
        } else {
            logger.debug("{}╔═╣{}", deepnessPrefix, name);
        }
    }

    protected void logFinish() {
        if (deepness == 0) {
            logger.debug("{}╚═══╣{} ══ {}", deepnessPrefix, name, formatOperationTime());
        } else {
            logger.debug("{}╚═╣{} ══ {}", deepnessPrefix, name, formatOperationTime());
        }
    }

    protected void logSplit() {
        if (deepness <= 1) {
            logger.debug("{}  │{} ── {}", deepnessPrefix, name, formatOperationTime());
        } else {
            logger.debug("{}│{} ── {}", deepnessPrefix, name, formatOperationTime());
        }
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

    private enum State {
        CREATED,
        STARTED,
        FINISHED
    }
}
