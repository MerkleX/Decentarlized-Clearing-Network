package io.merklex.dcn.tools;

import io.merklex.dcn.contracts.DCN;
import io.merklex.ether_net.EtherDebugNet;
import io.merklex.web3.EtherTransactions;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

public class LocalDCN {
    public static void main(String[] args) throws IOException, InterruptedException, TransactionException {
        HashMap<String, String> initialHash = new HashMap<>();
        initialHash.put("72E306CB314354289B148BEA907F6B504C0568379D75770E65F65190A2868639", "1000000000000000000000000000000000");

        EtherDebugNet etherNet = new EtherDebugNet(8545, "127.0.0.1",
                initialHash, 8_000_000, 9999);

        String privateKey = "72E306CB314354289B148BEA907F6B504C0568379D75770E65F65190A2868639";
        EtherTransactions etherTransactions = new EtherTransactions(etherNet.web3(), Credentials.create(privateKey));
        String dcnAddress = etherTransactions.deployContract(BigInteger.ZERO, BigInteger.valueOf(8_000_000), DCN.DeployData(), BigInteger.ZERO);
        System.out.println(dcnAddress);


        Runtime.getRuntime().addShutdownHook(new Thread(etherNet::close));

        while (true) {
            Thread.sleep(1000);
        }
    }
}
