package io.merklex.dcn.utils;

import io.merklex.dcn.contracts.DCN;
import io.merklex.ether_net.EtherDebugNet;
import io.merklex.web3.EtherTransactions;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Stack;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;

public class StaticNetwork {
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(8000000);
    private static final EtherDebugNet network;
    private static final String dcnAddress;

    public static Web3j Web3() {
        return network.web3();
    }

    public static String DCN() {
        return dcnAddress;
    }

    public static BigInteger GetBalance(String address) throws IOException {
        EthBlockNumber block = StaticNetwork.Web3().ethBlockNumber().send();
        return Web3().ethGetBalance(address, new DefaultBlockParameterNumber(block.getBlockNumber())).send().getBalance();
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
            for (Credentials key : Accounts.keys) {
                accounts.put(key.getEcKeyPair().getPrivateKey().toString(16), "1000000000000000000000000000000000");
            }

            network = new EtherDebugNet(5123, "localhost", accounts, 8000000, 9999);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(network::close));

        EtherTransactions etherTransactions = Accounts.getTx(0);

        try {
            dcnAddress = etherTransactions.deployContract(BigInteger.ZERO, GAS_LIMIT, DCN.DeployData(), BigInteger.ZERO);
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

    public static void main(String[] args) {
        System.out.println(DCN());
    }
}
