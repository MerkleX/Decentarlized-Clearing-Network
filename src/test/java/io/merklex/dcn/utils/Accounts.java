package io.merklex.dcn.utils;

import io.merklex.web3.EtherTransactions;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Accounts {
    public static final List<Credentials> keys = new ArrayList<>();
    private static final Random random = new Random(123);

    static {
        byte[] bucket = new byte[32];
        for (int i = 0; i < 100; i++) {
            random.nextBytes(bucket);
            keys.add(Credentials.create(Numeric.toHexString(bucket)));
        }
    }

    public static Credentials get(int index) {
        return keys.get(index);
    }

    public static EtherTransactions getTx(int index) {
        return new EtherTransactions(StaticNetwork.Web3(), keys.get(index))
                .withGas(BigInteger.ZERO, StaticNetwork.GAS_LIMIT);
    }
}
