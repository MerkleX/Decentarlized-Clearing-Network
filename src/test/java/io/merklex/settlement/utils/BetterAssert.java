package io.merklex.settlement.utils;

import org.junit.Assert;

import java.math.BigInteger;

public class BetterAssert {
    public static void assertEquals(BigInteger a, BigInteger b) {
        if (a.compareTo(b) != 0) {
            Assert.assertEquals(a, b);
        }
    }

    public static void assertEquals(long expected, BigInteger b) {
        assertEquals(BigInteger.valueOf(expected), b);
    }

    public static <T> void assertEquals(T a, T b) {
        Assert.assertEquals(a, b);
    }
}
