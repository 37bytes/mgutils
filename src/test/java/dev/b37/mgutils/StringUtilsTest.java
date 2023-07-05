package dev.b37.mgutils;

import dev.b37.mgutils._SUSPENDED_.performance.PerformanceTest;
import dev.b37.mgutils.concurrent.TaskInvoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class StringUtilsTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testJoinNotBlank() {
        Assertions.assertEquals(" lorem  ipsum dolor sit  amet",
                StringUtils.joinNotBlank(" ", " lorem", " ", null, " ipsum", "dolor", "", " ", "sit ", "amet"));

        Assertions.assertEquals("lorem ipsum dolor sit amet",
                StringUtils.joinNotBlankTrimming(" ", "lorem", " ", null, " ipsum", "dolor", "", " ", "sit ", "amet"));

        Assertions.assertEquals(" lorem  ipsum dolor sit  amet",
                StringUtils.joinNotBlank(" ", new StringLike(" lorem"), new StringLike(" "), new StringLike(null), null, new StringLike(" ipsum"), new StringLike("dolor"), new StringLike(""), new StringLike(" "), new StringLike("sit "), new StringLike("amet")));

        Assertions.assertEquals("lorem,ipsum,dolor,sit,amet",
                StringUtils.joinNotBlankTrimming(",", " lorem", null, new StringLike(" "), new StringLike(null), null, new StringLike(" ipsum"), new StringLike("dolor"), new StringLike(""), new StringLike(" "), new StringLike("sit "), new StringLike("amet")));

        Assertions.assertEquals("",
                StringUtils.joinNotBlank(" ", null, "    ", "", null, "", " ", ""));

        Assertions.assertEquals("",
                StringUtils.joinNotBlankTrimming(" ", null, new StringLike("    "), "", null, "", " ", ""));

        Assertions.assertEquals("",
                StringUtils.joinNotBlank(" ", new Object[0]));

        Assertions.assertEquals("",
                StringUtils.joinNotBlank(" ", (String) null));

        Assertions.assertEquals("",
                StringUtils.joinNotBlank(" ", (Object) null));

        Assertions.assertEquals("",
                StringUtils.joinNotBlank(" ", new StringLike(null)));

        Assertions.assertEquals("",
                StringUtils.joinNotBlank(" ", new StringLike("  ")));
    }

    @Test
    public void testReplacement() {
        Assertions.assertEquals("1,4.5", StringUtils.replaceOnce("1.4.5", '.', ','));
        Assertions.assertEquals("1.4.5", StringUtils.replaceOnce("1.4.5", 'a', ','));
        Assertions.assertEquals("1.4,5", StringUtils.replaceLastOnce("1.4.5", '.', ','));
        Assertions.assertEquals("1.4.5", StringUtils.replaceLastOnce("1.4.5", 'a', 'b'));
        Assertions.assertEquals("", StringUtils.replaceLastOnce("", '.', ','));
        Assertions.assertEquals("1.5.5", StringUtils.setCharAt("1.4.5", 2, '5'));
        Assertions.assertThrows(RuntimeException.class, () -> {
            StringUtils.setCharAt(null, 2, '5');
        });
    }

    @Test
    public void testReplaceOncePerformance() {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        TaskInvoker<String> invoker = new TaskInvoker<>(executor);

        PerformanceTest test = PerformanceTest.create("ReplaceOnce", log).start();

        for (int i = 0; i < 8; i++) {
            invoker.submit((consumer) -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = 0; j < 800_000; j++) {
                    consumer.accept(StringUtils.replaceOnce(Double.toString(random.nextDouble()), '.', ','));
                }
            });
        }

        List<String> initialDecimals = invoker.completeAll();

        test.split("Preparation");

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

    private static class StringLike {
        private final String string;

        public StringLike(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
