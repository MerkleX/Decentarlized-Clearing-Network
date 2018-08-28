package io.merklex.settlement.utils;

import org.junit.Assert;

import java.math.BigInteger;

public class BetterAssert {
    public static void assertEquals(BigInteger a, BigInteger b) {
        if (a.compareTo(b) != 0) {
            Assert.assertEquals(a, b);
        }
    }

    public static void assertEquals(Object a, Object b) {
        Assert.assertEquals(a, b);
    }
}
