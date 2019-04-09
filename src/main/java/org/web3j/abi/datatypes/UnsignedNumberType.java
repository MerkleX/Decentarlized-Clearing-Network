package org.web3j.abi.datatypes;

import io.merklex.web3.Utils;

import java.math.BigInteger;

public class UnsignedNumberType extends Uint {
    public UnsignedNumberType(int bits, long value) {
        this(bits, Utils.ToUnsigned(bits, value));
    }

    public UnsignedNumberType(int bits, BigInteger value) {
        super(bits, value);
    }

    @Override
    boolean valid(int bitSize, BigInteger value) {
        return value.signum() >= 0 && isValidBitSize(bitSize) && bitSize + 1 >= value.bitLength();
    }
}
