package io.merklex.dcn.utils;

import io.merklex.dcn.network.EtherDebugNet;
import io.merklex.dcn.contracts.DCN;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Stack;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;

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

    public static BigInteger GetBalance(String address) throws IOException {
        EthBlockNumber block = StaticNetwork.Web3().ethBlockNumber().send();
        return StaticNetwork.Web3().ethGetBalance(address, new DefaultBlockParameterNumber(block.getBlockNumber())).send().getBalance();
    }

    private static final Stack<BigInteger> checkpoints = new Stack<>();

    public static void Checkpoint() {
        try {
            checkpoints.push(network.checkpoint().send().id());
//            System.out.println("CHECKPOINT : " + checkpoints.peek());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Revert() {
//        System.out.println("REVERT : " + checkpoints.peek());
        try {
            network.revert(checkpoints.pop()).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            HashMap<String, String> accounts = new HashMap<>();
            for (String keyName : Genesis.KeyNames()) {
                String privateKey = Genesis.GetKey(keyName).getEcKeyPair().getPrivateKey().toString(16);
                String balance = Genesis.GetBalance(keyName);
                accounts.put(privateKey, balance);
            }

            network = new EtherDebugNet(5123, accounts, Long.parseUnsignedLong(Genesis.getGasLimit()));
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

    public static void DescribeCheckpoint() {
        beforeAll(StaticNetwork::Checkpoint);
        afterAll(StaticNetwork::Revert);
    }

    public static void DescribeCheckpointForEach() {
        beforeEach(StaticNetwork::Checkpoint);
        afterEach(StaticNetwork::Revert);
    }
}
