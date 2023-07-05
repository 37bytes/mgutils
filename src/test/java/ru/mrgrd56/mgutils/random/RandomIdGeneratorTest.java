package ru.mrgrd56.mgutils.random;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mrgrd56.mgutils._SUSPENDED_.performance.PerformanceTest;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomIdGeneratorTest {
    RandomIdGenerator idGenerator = RandomIdGenerator.getInstance();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testCreateIdentifier() {
        StopWatch stopWatch = StopWatch.createStarted();

        for (int i = 0; i < 10; i++) {
            log.info(idGenerator.createIdentifier());
        }

        for (int i = 0; i < 500_000; i++) {
            String identifier = idGenerator.createIdentifier();
            Assertions.assertTrue(identifier.matches("^[0-9a-z]{12}$"));
        } // 00:00:03.310 -> 00:00:03.763 for 50_000_000

        log.info("testCreateIdentifier took {}", stopWatch.formatTime());
    }

    @Test
    public void testCreateIdentifierPerformance() {
        PerformanceTest test = PerformanceTest.create("IDENTIFIER", log);

        for (int i = 0; i < 10; i++) {
            log.info(idGenerator.createIdentifier());
        }

        test.start();

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 5_000_000; i++) {
            String identifier = idGenerator.createIdentifier(random);
        }

        test.split("mgutils-id");

        for (int i = 0; i < 5_000_000; i++) {
            String identifier = UUID.randomUUID().toString();
        }

        test.split("uuid");

        test.finish();
    }
}
