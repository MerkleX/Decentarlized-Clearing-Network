package io.merklex.dnc.models;

import java.math.BigInteger;

public class GetPositionLimitResult {
    public BigInteger version;
    public BigInteger longAssetQty;
    public BigInteger shortAssetQty;
    public BigInteger longPrice;
    public BigInteger shortPrice;
    public BigInteger etherShift;
    public BigInteger assetShift;
}
