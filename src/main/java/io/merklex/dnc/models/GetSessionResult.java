package io.merklex.dnc.models;

import java.math.BigInteger;

public class GetSessionResult {
    public BigInteger positionCount;
    public BigInteger userId;
    public BigInteger exchangeId;
    public BigInteger maxEtherFees;
    public BigInteger expireTime;
    public String tradeAddress;
    public BigInteger etherBalance;
}
