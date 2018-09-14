package io.merklex.dnc;

import io.merklex.dnc.models.*;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tuples.generated.Tuple8;

import java.math.BigInteger;

public class DCNResults {
    public static GetExchangeResult GetExchange(GetExchangeResult res, Tuple3<String, String, BigInteger> value) {
        res.name = value.getValue1();
        res.address = value.getValue2();
        res.feeBalance = value.getValue3();
        return res;
    }

    public static GetAssetResult GetAsset(GetAssetResult res, Tuple3<String, BigInteger, String> value) {
        res.symbol = value.getValue1();
        res.unitScale = value.getValue2();
        res.contractAddress = value.getValue3();
        return res;
    }

    public static GetSessionResult GetSession(GetSessionResult res, Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> value) {
        res.turnOver = value.getValue1();
        res.positionCount = value.getValue2();
        res.userId = value.getValue3();
        res.exchangeId = value.getValue4();
        res.maxEtherFees = value.getValue5();
        res.expireTime = value.getValue6();
        res.tradeAddress = value.getValue7();
        res.etherBalance = value.getValue8();
        return res;
    }

    public static GetPositionResult GetPosition(GetPositionResult res, Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> value) {
        res.assetId = value.getValue1();
        res.etherQty = value.getValue2();
        res.assetQty = value.getValue3();
        res.assetBalance = value.getValue4();
        return res;
    }

    public static GetPositionLimitResult GetPositionLimit(GetPositionLimitResult res, Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> value) {
        res.version = value.getValue1();
        res.longAssetQty = value.getValue2();
        res.shortAssetQty = value.getValue3();
        res.longPrice = value.getValue4();
        res.shortPrice = value.getValue5();
        res.etherShift = value.getValue6();
        res.assetShift = value.getValue7();
        return res;
    }
}
