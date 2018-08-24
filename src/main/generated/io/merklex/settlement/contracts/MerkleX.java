package io.merklex.settlement.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
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
public class MerkleX extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50306000556107bd806100246000396000f3006080604052600436106100485763ffffffff60e060020a60003504166322eac9a1811461004d5780634580a1c91461008157806348aae780146100b8578063dbe1501b14610111575b600080fd5b34801561005957600080fd5b5061007f63ffffffff6004351661ffff6024351667ffffffffffffffff60443516610186565b005b34801561008d57600080fd5b506100a663ffffffff6004351661ffff602435166101a7565b60408051918252519081900360200190f35b3480156100c457600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261007f9436949293602493928401919081908401838280828437509497506101cc9650505050505050565b34801561011d57600080fd5b5061013b63ffffffff6004351661ffff6024351660443515156106d4565b6040805160ff978816815295909616602086015267ffffffffffffffff938416858701529183166060850152909116608083015263ffffffff1660a082015290519081900360c00190f35b60066008830262080000850217016401000100020181815401815550505050565b60006101b1610757565b64010001000862080000850260088502170180548252602082f35b6101d4610757565b6101dc610776565b6101e4610776565b6101ec610757565b84516020860181810191505b818110156106cb57805160038201915061ffff7e010000000000000000000000000000000000000000000000000000000000008204168752601960ff7d0100000000000000000000000000000000000000000000000000000000008304160282018381111561026357005b8083101561069e57825167ffffffffffffffff73010000000000000000000000000000000000000082041667ffffffffffffffff6b010000000000000000000000830416600160ff60020a8404168a8a82156102c457505060208b8101908b015b848251018252838251018152505063ffffffff670100000000000000850416808a51018a5260088d51026607fffffff8000078200000000000000000000000000000000000000000000000008704166401000100020181811791506006810190508360008114610354576006830186815401815582548589018082101561034a57600080fd5b900383555061039b565b81548785118015610378578886038083101561036f57600080fd5b82038455610380565b858903820184555b506006840180548881101561039457600080fd5b8890039055505b505060017f400000000000000000000000000000000000000000000000000000000000000087041680156103d0576003820191505b815463ffffffff80821685011663ffffffff1990911617600160e160020a82041667ffffffffffffffff6c0100000000000000000000000083048116906401000000008404166000888414801561044c57928b0167ffffffffffffffff90811693928b0116918b84108b8410171561044757600080fd5b610495565b828b10801561046f578b840393508c85101561046a576000948d0392505b610493565b941960011694928b0392848d10801561048c57858e039350610491565b948d03945b505b505b5088156104a0576000035b6380000000850260ff60020a1660a060020a860467ffffffffffffffff161701680100000000000000008104777fffffffffffffffffffffffffffffffffffffffffffffff16156104f057600080fd5b63ffffffff60018604166401000000008302176c0100000000000000000000000084021760a060020a67ffffffffffffffff6001840416021760e060020a600160ff60020a840416021760e160020a850217945084875586896000811461055c57600182019150610563565b6002820191505b50547801000000000000000000000000000000000000000000000000810467ffffffffffffffff1683111561059757600080fd5b8980156105f95767ffffffffffffffff8216808511156105f357808503945085600f60a060020a850416600a0a860204630fffffff741000000000000000000000000000000000000000008504168110156105f157600080fd5b505b5061065b565b67ffffffffffffffff680100000000000000008304168086111561065957808603955085600f60a060020a850416600a0a860204630fffffff7410000000000000000000000000000000000000000085041681111561065757600080fd5b505b505b50426103e863ffffffff70010000000000000000000000000000000084041602111561068657600080fd5b50505050505050505050505050601983019250610263565b602087015187511419156106b157600080fd5b602086015186511419156106c457600080fd5b50506101f8565b50505050505050565b60008060008060008060088802620800008a0217640100010002018715156106fa576002015b54600160e160020a820481169b60e060020a83049091169a5067ffffffffffffffff60a060020a830481169a506c01000000000000000000000000830481169950640100000000830416975063ffffffff90911695509350505050565b6020604051908101604052806001906020820280388339509192915050565b604080518082018252906002908290803883395091929150505600a165627a7a72305820648dbae8b4217195a7cb96aeddeb048aaf321e934e944b6d1d8f85c116f68ccf0029";

    public static final String FUNC_SET_BALANCE = "set_balance";

    public static final String FUNC_GET_BALANCE = "get_balance";

    public static final String FUNC_SUBMIT_GROUP = "submit_group";

    public static final String FUNC_GET_POSITION = "get_position";

    protected MerkleX(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MerkleX(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> set_balance(BigInteger user_id, BigInteger token_id, BigInteger new_balance) {
        final Function function = new Function(
                FUNC_SET_BALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint16(token_id), 
                new org.web3j.abi.datatypes.generated.Uint64(new_balance)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> get_balance(BigInteger user_id, BigInteger token_id) {
        final Function function = new Function(FUNC_GET_BALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint16(token_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> submit_group(byte[] data) {
        final Function function = new Function(
                FUNC_SUBMIT_GROUP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> get_position(BigInteger user_id, BigInteger token_id, Boolean first) {
        final Function function = new Function(FUNC_GET_POSITION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint16(token_id), 
                new org.web3j.abi.datatypes.Bool(first)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}, new TypeReference<Uint64>() {}, new TypeReference<Uint64>() {}, new TypeReference<Uint64>() {}, new TypeReference<Uint32>() {}));
        return new RemoteCall<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public static RemoteCall<MerkleX> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MerkleX.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MerkleX> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MerkleX.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static MerkleX load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MerkleX(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MerkleX load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MerkleX(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
