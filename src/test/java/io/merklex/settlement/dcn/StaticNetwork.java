package io.merklex.settlement.dcn;

import io.merklex.settlement.contracts.DCN;
import io.merklex.settlement.networks.EtherDebugNet;
import io.merklex.settlement.utils.Genesis;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Stack;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;

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

    public static void DescribeCheckpoint() {
        beforeAll(StaticNetwork::Checkpoint);
        afterAll(StaticNetwork::Revert);
    }
}
