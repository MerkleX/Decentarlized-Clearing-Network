package io.merklex.web3;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.ChainId;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class Wallet {
    private final Web3j web3j;
    private final Credentials credentials;
    private final PollingTransactionReceiptProcessor receiptProcessor;
    private final TransactionManager transactionManager;

    public Wallet(Web3j web3j, Credentials credentials) {
        this(web3j, credentials, ChainId.NONE);
    }

    public Wallet(Web3j web3j, Credentials credentials, byte chainId) {
        this.web3j = web3j;
        this.credentials = credentials;
        receiptProcessor = new PollingTransactionReceiptProcessor(web3j, 1000, 600);
        transactionManager = new FastRawTransactionManager(web3j, credentials, chainId,
                receiptProcessor);
    }

    public Credentials credentials() {
        return credentials;
    }

    private BigInteger gasPrice = BigInteger.ONE;
    private BigInteger gasLimit = BigInteger.valueOf(1000000);

    public Wallet withGas(BigInteger gasPrice, BigInteger gasLimit) {
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        return this;
    }

    public EthSendTransaction sendCall(String contractAddress, Function function, BigInteger weiValue) throws IOException {
        return sendCall(gasPrice, gasLimit, contractAddress, function, weiValue);
    }

    public EthSendTransaction sendCall(BigInteger gasPrice, BigInteger gasLimit,
                                       String contractAddress, Function function, BigInteger weiValue) throws IOException {
        String encode = FunctionEncoder.encode(function);
        return sendCall(gasPrice, gasLimit, contractAddress, encode, weiValue);
    }

    public EthSendTransaction sendCall(BigInteger gasPrice, BigInteger gasLimit,
                                       String contractAddress, String data, BigInteger weiValue) throws IOException {
        return transactionManager.sendTransaction(gasPrice, gasLimit, contractAddress, data, weiValue);
    }

    public TransactionReceipt call(String contractAddress, Function function, BigInteger weiValue) throws IOException, TransactionException {
        return waitForResult(sendCall(contractAddress, function, weiValue));
    }

    public TransactionReceipt call(String contractAddress, Function function) throws IOException, TransactionException {
        return waitForResult(sendCall(contractAddress, function, BigInteger.ZERO));
    }

    public Optional<TransactionReceipt> getResult(EthSendTransaction ticket) throws IOException {
        EthGetTransactionReceipt transactionReceipt =
                web3j.ethGetTransactionReceipt(ticket.getTransactionHash()).send();
        return transactionReceipt.getTransactionReceipt();
    }

    public TransactionReceipt waitForResult(EthSendTransaction ticket) throws IOException, TransactionException {
        return receiptProcessor.waitForTransactionReceipt(ticket.getTransactionHash());
    }
}
