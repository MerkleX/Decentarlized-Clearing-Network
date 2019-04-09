package io.merklex.dcn;

import java.math.BigInteger;

public class FeatureLocks {
    public static final BigInteger ADD_ASSET = BigInteger.ONE.shiftLeft(0);
    public static final BigInteger ADD_EXCHANGE = BigInteger.ONE.shiftLeft(1);
    public static final BigInteger CREATE_USER = BigInteger.ONE.shiftLeft(2);
    public static final BigInteger EXCHANGE_DEPOSIT = BigInteger.ONE.shiftLeft(3);
    public static final BigInteger USER_DEPOSIT = BigInteger.ONE.shiftLeft(4);
    public static final BigInteger TRANSFER_TO_SESSION = BigInteger.ONE.shiftLeft(5);
    public static final BigInteger DEPOSIT_ASSET_TO_SESSION = BigInteger.ONE.shiftLeft(6);
    public static final BigInteger EXCHANGE_TRANSFER_FROM = BigInteger.ONE.shiftLeft(7);
    public static final BigInteger EXCHANGE_SET_LIMITS = BigInteger.ONE.shiftLeft(8);
    public static final BigInteger APPLY_SETTLEMENT_GROUPS = BigInteger.ONE.shiftLeft(9);
    public static final BigInteger ALL = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);
}
