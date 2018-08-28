package io.merklex.settlement.dcn;

import io.merklex.settlement.contracts.DCN;
import io.merklex.settlement.networks.EtherDebugNet;
import io.merklex.settlement.utils.Genesis;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;

public class StaticNetwork {
    private static final EtherDebugNet network;
    private static final DCN dcn;

    public static Web3j Web3() {
        return network.web3();
    }

    public static DCN DCN() {
        return dcn;
    }

    public static DCN DCN(String user) {
        Credentials credentials = Genesis.GetKey(user);
        if (credentials == null) {
            throw new IllegalArgumentException("Invalid user: " + user);
        }
        return DCN.load(dcn.getContractAddress(), network.web3(),
                credentials, BigInteger.ONE, BigInteger.valueOf(1000000));
    }

    private static BigInteger checkpointId;

    public static void Checkpoint() {
        try {
            checkpointId = network.checkpoint().send().id();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Revert() {
        try {
            network.revert(checkpointId).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            network = new EtherDebugNet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(network::close));

        try {
            dcn = DCN.deploy(network.web3(), Genesis.GetKey("merkle"), BigInteger.ONE, BigInteger.valueOf(8000000)).send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
