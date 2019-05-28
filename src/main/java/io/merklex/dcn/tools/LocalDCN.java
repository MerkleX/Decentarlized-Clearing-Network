package io.merklex.dcn.tools;

import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.ether_net.EtherDebugNet;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.RevertCodeExtractor;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

public class LocalDCN {
    private static final String privateKey = "72E306CB314354289B148BEA907F6B504C0568379D75770E65F65190A2868639";

    public static void main(String[] args) throws IOException, InterruptedException, TransactionException {
        HashMap<String, String> initialHash = new HashMap<>();
        initialHash.put(privateKey, "1000000000000000000000000000000000");

        EtherDebugNet etherNet = new EtherDebugNet(8545, "127.0.0.1",
                initialHash, 8_000_000, 9999);

        EtherTransactions etherTransactions = new EtherTransactions(etherNet.web3(), Credentials.create(privateKey));
        String dcnAddress = etherTransactions.deployContract(BigInteger.ZERO, BigInteger.valueOf(8_000_000), DCN.DeployData(), BigInteger.ZERO);
        System.out.println(dcnAddress);

        Runtime.getRuntime().addShutdownHook(new Thread(etherNet::close));

        while (true) {
            Thread.sleep(1000);
        }
    }

    public static class RunLocal {
        public static void main(String[] args) throws IOException, TransactionException {
            String dcn = "0x8b3c2b010ca7676add606a0b09ac2b53ffc38d10";
            String erc20 = "0x603bc175a26c8f74eaf9bc6ef5dd288afb6fc4b8";

            Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
            EtherTransactions tx = new EtherTransactions(web3j, Credentials.create(privateKey));
//
            String erc20Actual = tx.deployContract(
                    BigInteger.ZERO,
                    BigInteger.valueOf(8_000_000),
                    ERC20.DeployData(
                            BigInteger.valueOf(10000000000L), "TEST", 6, "TEST"),
                    BigInteger.ZERO
            );

            if (!erc20.equals(erc20Actual)) {
                System.out.println(erc20Actual);
                throw new IllegalStateException();
            }

            success(tx.sendCall(dcn, DCN.add_asset("test1234", 10, erc20)));

            success(tx.sendCall(dcn, DCN.user_create()));

            success(tx.sendCall(dcn, DCN.add_exchange("merkleX    ", tx.getAddress())));
            success(tx.sendCall(dcn, DCN.user_session_set_unlock_at(0, 0, BigInteger.valueOf(System.currentTimeMillis() / 1000 + 28800 * 2))));


            success(tx.sendCall(erc20, ERC20.approve(dcn, BigInteger.valueOf(10000000000L))));
            success(tx.sendCall(dcn, DCN.user_deposit_to_session(0, 0, 0, 100)));

            success(tx.sendCall(dcn, DCN.add_asset("test1234", 10, erc20)));
        }

        private static void success(EthSendTransaction res) throws IOException {
            if (res.hasError()) {
                System.out.println(RevertCodeExtractor.GetRevert(res.getError()));
            }
            else {
                System.out.println(res.getTransactionHash());
            }
        }
    }
}
