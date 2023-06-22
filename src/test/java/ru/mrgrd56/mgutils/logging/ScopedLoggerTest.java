package ru.mrgrd56.mgutils.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mrgrd56.mgutils.RandomUtils;

public class ScopedLoggerTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ScopedLoggerFactory logFactory = new ScopedLoggerFactory(log);

    @Test
    public void testScopedLogger() {
        Logger logger = logFactory.createLogger("testScopedLogger:");
        logger.info("Starting {}", 42);

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
