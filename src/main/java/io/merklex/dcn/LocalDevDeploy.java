package io.merklex.dcn;

import io.merklex.dcn.contracts.DCN;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;

public class LocalDevDeploy {
    private static final BigInteger ONE_ETHER = BigInteger.valueOf(10).pow(16);

    public static void main(String[] args) throws Exception {
        HttpService httpService = new HttpService("http://localhost:5123");
        Web3j web3 = Web3j.build(httpService);

        Credentials key = Credentials.create("0x9b53882960b27af0ceb5d1d7b426ff59b756449a7527d4d91119c9fc86a03b26");

        BigInteger gasLimit = BigInteger.valueOf(8000029);
//        RemoteCall<DCN> deploy = DCN.deploy(web3, key, BigInteger.ONE, gasLimit);
//        DCN dcn = deploy.send();

        RawTransactionManager tx = new RawTransactionManager(web3, key);
        EthSendTransaction ethSendTransaction = tx.sendTransaction(BigInteger.ONE, gasLimit, "0xa8075c256a69f646fc7aec1a932d75eaec5fc7ce", "0x0", ONE_ETHER);
        System.out.println(ethSendTransaction.getTransactionHash());

//        System.out.println(dcn.getContractAddress());
    }
}
