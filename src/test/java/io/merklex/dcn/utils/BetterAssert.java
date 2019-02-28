package io.merklex.dcn.utils;

import org.junit.Assert;

import java.math.BigInteger;

public class BetterAssert {
    public static void assertEquivalent(BigInteger expected, BigInteger actual) {
        if (expected.compareTo(actual) != 0) {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertNotEquals(BigInteger expected, BigInteger actual) {
        if (expected.compareTo(actual) == 0) {
            Assert.assertNotEquals(expected, actual);
        }
    }

    public static void assertEquivalent(long expected, BigInteger actual) {
        assertEquivalent(BigInteger.valueOf(expected), actual);
    }

    public static void assertEquivalent(long expected, long actual) {
        Assert.assertEquals(expected, actual);
    }

    public static <T> void assertEquivalent(T expected, T actual) {
        Assert.assertEquals(expected, actual);
    }
}
