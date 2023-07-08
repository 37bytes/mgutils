package dev.b37.mgutils.logging;

import dev.b37.mgutils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopedLoggerTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ScopedLoggerFactory logFactory = new ScopedLoggerFactory(log);

    @Test
    public void testScopedLogger() {
        Logger logger = logFactory.createLogger("testScopedLogger:");
        logger.info("Starting {}", 42);

        Assertions.assertTrue(ScopedLogger.isScopedLogger(logger));

        for (int i = 0; i < 3; i++) {
            Logger numberLogger = ScopedLogger.of(logger, "number:");
            numberLogger.warn("processing number {}", i);
            if (RandomUtils.nextBoolean(0.4)) {
                numberLogger.error("error processing number");
            } else {
                numberLogger.info("successfully processed number");
            }
        }

        logger.info("Finishing {}", 42);
    }
}
