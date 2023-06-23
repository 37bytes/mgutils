package ru.mrgrd56.mgutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {
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
