package io.merklex.dnc.models;

import java.math.BigInteger;

public class GetPositionLimitResult {
    public BigInteger version;
    public BigInteger minAssetQty;
    public BigInteger minEtherQty;
    public BigInteger longPrice;
    public BigInteger shortPrice;
    public BigInteger etherShift;
    public BigInteger assetShift;
}
