package dev.b37.mgutils.misc;

import dev.b37.mgutils._SUSPENDED_.performance.PerformanceTest;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Stream;

public class PerformanceTests {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

//    @Test
    public void compareArrayConcatenation() {
        PerformanceTest test = PerformanceTest.create("ArrayConcatenation", log);

        Object[] first = {"hello", "world", 42, "lorem", "xzeww", "rewtrwer", 42.599999, 0.1 + 0.2};
        Object[] second = {new Object(), PerformanceTest.create("none"), Integer.MAX_VALUE};

        test.start();

        for (int i = 0; i < 20_000_000; i++) {
            Stream.concat(Arrays.stream(first), Arrays.stream(second)).toArray();
        }

        test.split("StreamAPI");

        for (int i = 0; i < 20_000_000; i++) {
            ArrayUtils.addAll(first, second);
        }

        test.split("Commons");

        test.finish();
    }

//    @Test
    public void compareStringConcatenation() {
        PerformanceTest test = PerformanceTest.create("StringConcatenation", log);

        String first1 = "wefjw9i weu f98wje9f8 3ef9u 3hf9 3h4f973u4hf439 f9fui h93u4hf 9";
        String second1 = "{new Object(), PerformanceTest.create(\"none\"), Integer.MAX_VALUE}";

        String first2 = "amnffw jr-03 4jf-0ij3fo ij ewpo fjwe fkjwe ij29 fjw9iej 94- hf3";
        String second2 = "String r = new StringBuilder(first.length() + second.length())weewe";

        test.start();

        for (int i = 0; i < 50_000_000; i++) {
            String r = first1 + second1;
        }

        test.split("Plus");

        for (int i = 0; i < 50_000_000; i++) {
            String r = new StringBuilder(first2.length() + second2.length())
                    .append(first2)
                    .append(second2)
                    .toString();
        }

        test.split("StringBuilder");

        test.finish();
    }
}
