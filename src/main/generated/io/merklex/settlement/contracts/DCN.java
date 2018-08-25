package io.merklex.settlement.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class DCN extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5030600055610334806100246000396000f30060806040526004361061006c5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630faf09048114610071578063136a9bf7146100a1578063541694cf14610112578063782a6471146101e2578063cd242c03146101e2575b600080fd5b34801561007d57600080fd5b5061009f73ffffffffffffffffffffffffffffffffffffffff600435166101f7565b005b3480156100ad57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261009f9436949293602493928401919081908401838280828437509497505050923573ffffffffffffffffffffffffffffffffffffffff16935061020b92505050565b34801561011e57600080fd5b5061013063ffffffff6004351661028a565b60405180806020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156101a557818101518382015260200161018d565b50505050905090810190601f1680156101d25780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156101ee57600080fd5b5061009f6102c8565b6000543081141561020757816000555b5050565b6102136102ca565b60005430811461021f57005b8351600c811461022b57005b60015461ffff81111561023a57005b60028102600201602087015162100000810490508674010000000000000000000000000000000000000000820217808355838752600184019350836001556004601c8801a0505050505050505050565b60606000806102976102e9565b6001548086106102a357005b600c825260028602600201805480602085015260018201549050806040850152606084f35b565b6020604051908101604052806001906020820280388339509192915050565b60606040519081016040528060039060208202803883395091929150505600a165627a7a7230582090b06c7fd39dacc2daf7dc66922482c713ed4343aa2047c4f3f4e915d42e79ce0029";

    public static final String FUNC_SET_CREATOR = "set_creator";

    public static final String FUNC_ADD_EXCHANGE = "add_exchange";

    public static final String FUNC_GET_EXCHANGE = "get_exchange";

    public static final String FUNC_ADD_ASSET = "add_asset";

    public static final String FUNC_ADD_USER = "add_user";

    protected DCN(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DCN(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> set_creator(String new_creator) {
        final Function function = new Function(
                FUNC_SET_CREATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(new_creator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> add_exchange(String name, String addr) {
        final Function function = new Function(
                FUNC_ADD_EXCHANGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name), 
                new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<String, String, BigInteger>> get_exchange(BigInteger id) {
        final Function function = new Function(FUNC_GET_EXCHANGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<String, String, BigInteger>>(
                new Callable<Tuple3<String, String, BigInteger>>() {
                    @Override
                    public Tuple3<String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> add_asset() {
        final Function function = new Function(
                FUNC_ADD_ASSET, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> add_user() {
        final Function function = new Function(
                FUNC_ADD_USER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<DCN> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DCN.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DCN> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DCN.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static DCN load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DCN(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DCN load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DCN(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
