package dev.b37.mgutils.performance;

import dev.b37.mgutils.StringUtils;
import dev.b37.mgutils.concurrent.TaskInvoker;
import dev.b37.mgutils.logging.ScopedLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.b37.mgutils._SUSPENDED_.performance.PerformanceTest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class PerformanceTestTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testPerformanceTest() {
        Logger logger = ScopedLogger.of(log, "tpt:");

        ExecutorService executor = Executors.newFixedThreadPool(8);
        TaskInvoker<String> invoker = new TaskInvoker<>(executor);

        PerformanceTest test = PerformanceTest.create("ReplaceOnce", logger).start();

        for (int i = 0; i < 8; i++) {
            invoker.submit((consumer) -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = 0; j < 40_000; j++) {
                    consumer.accept(StringUtils.replaceOnce(Double.toString(random.nextDouble()), '.', ','));
                }
            });
        }

        Assertions.assertThrows(IllegalStateException.class, () -> {
            test.start();
        });

        List<String> initialDecimals = invoker.completeAll();

        test.split("Preparation");

        PerformanceTest nestedTest = test.nested("Nested1");
        test.nested("Nested2").start().finish();
        PerformanceTest nestedTestSplit = nestedTest.split("Hellow!");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            PerformanceTest nestedTestSplitSplit = nestedTestSplit.split("Spli7");
        });
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            nestedTestSplit.nested("NestedOfSplit").start().finish();
        });

        for (String decimal : initialDecimals) {
            decimal.replace(',', '.');
        }

        test.split("Native");

        for (String decimal : initialDecimals) {
            StringUtils.replaceOnce(decimal, ',', '.');
        }

        test.split("Mgutils");

        for (String decimal : initialDecimals) {
            int index = decimal.indexOf(',');

            if (index != -1) {
                StringBuilder stringBuilder = new StringBuilder(decimal);
                stringBuilder.setCharAt(index, '.');
                stringBuilder.toString();
            }
        }

        test.split("Mgutils-SB");

        for (String decimal : initialDecimals) {
            org.apache.commons.lang3.StringUtils.replaceOnce(decimal, ",", ".");
        }

        test.split("Commons");

        test.finish();
    }
}
