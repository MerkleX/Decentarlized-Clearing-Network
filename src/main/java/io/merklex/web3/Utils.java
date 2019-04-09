package io.merklex.web3;

import java.math.BigInteger;

public class Utils {
    public static final BigInteger U64 = BigInteger.valueOf(2).shiftLeft(64);
    public static final BigInteger U32 = BigInteger.valueOf(2).shiftLeft(32);
    public static final BigInteger U16 = BigInteger.valueOf(2).shiftLeft(16);
    public static final BigInteger U8 = BigInteger.valueOf(2).shiftLeft(8);

    public static BigInteger ToUnsigned(int bits, long number) {
        BigInteger rotate = BigInteger.valueOf(2).shiftLeft(bits);
        if (number < 0) {
            return rotate.add(BigInteger.valueOf(number));
        }
        return BigInteger.valueOf(number);
    }

    public static BigInteger ToUnsigned(long number) {
        if (number < 0) {
            return U64.add(BigInteger.valueOf(number));
        }
        return BigInteger.valueOf(number);
    }

    public static BigInteger ToUnsigned(int number) {
        if (number < 0) {
            return U32.add(BigInteger.valueOf(number));
        }
        return BigInteger.valueOf(number);
    }

    public static BigInteger ToUnsigned(short number) {
        if (number < 0) {
            return U16.add(BigInteger.valueOf(number));
        }
        return BigInteger.valueOf(number);
    }

    public static BigInteger ToUnsigned(byte number) {
        if (number < 0) {
            return U8.add(BigInteger.valueOf(number));
        }
        return BigInteger.valueOf(number);
    }

    public static BigInteger ToUnsigned(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalStateException();
        }
        return number;
    }
}
