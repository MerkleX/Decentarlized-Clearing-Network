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
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tuples.generated.Tuple7;
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
    private static final String BINARY = "608060405234801561001057600080fd5b50336000557f455448200000000005f5e1000000000000000000000000000000000000000000640200000004556111018061004c6000396000f3006080604052600436106101035763ffffffff60e060020a6000350416630faf09048114610108578063136a9bf71461012b578063386e7a991461018f578063431ec601146101b6578063432ea2be146101dd57806344f0e2111461022e578063541694cf146102585780635bc1d3391461030e57806362a86d201461039857806369772b53146103e25780637c73473614610400578063842585081461047157806390fd19e6146104ca5780639526f9f314610526578063a88d19021461054b578063aaf1e2b314610560578063ab340db914610597578063bf1f6232146105d5578063cefa919214610619578063f3924f291461064b578063fd33482d14610661575b600080fd5b34801561011457600080fd5b50610129600160a060020a0360043516610676565b005b34801561013757600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261012994369492936024939284019190819084018382808284375094975050509235600160a060020a0316935061068a92505050565b34801561019b57600080fd5b50610129600160a060020a03600435811690602435166106ea565b3480156101c257600080fd5b506101cb61073a565b60408051918252519081900360200190f35b3480156101e957600080fd5b506101fb63ffffffff6004351661074f565b604080519687526020870195909552858501939093526060850191909152608084015260a0830152519081900360c00190f35b34801561023a57600080fd5b5061012963ffffffff60043516600160a060020a03602435166107e9565b34801561026457600080fd5b5061027663ffffffff60043516610809565b604051808060200184600160a060020a0316600160a060020a03168152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156102d15781810151838201526020016102b9565b50505050905090810190601f1680156102fe5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561031a57600080fd5b5061032a61ffff60043516610860565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a0316815260200182810382528581815181526020019150805190602001908083836000838110156102d15781810151838201526020016102b9565b3480156103a457600080fd5b506103bc63ffffffff6004351660ff602435166108b4565b604080519485526020850193909352838301919091526060830152519081900360800190f35b3480156103ee57600080fd5b5061012963ffffffff60043516610937565b34801561040c57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526101299436949293602493928401919081908401838280828437509497505050833567ffffffffffffffff16945050505060200135600160a060020a03166109d3565b34801561047d57600080fd5b506040805160206004803580820135601f8101849004840285018401909552848452610129943694929360249392840191908190840183828082843750949750610a389650505050505050565b3480156104d657600080fd5b506104ee63ffffffff6004351660ff60243516610b9a565b604080519788526020880196909652868601949094526060860192909252608085015260a084015260c0830152519081900360e00190f35b34801561053257600080fd5b506101cb63ffffffff6004351661ffff60243516610d29565b34801561055757600080fd5b506101cb610d4f565b34801561056c57600080fd5b5061012963ffffffff6004358116906024358116906044351667ffffffffffffffff60643516610d64565b3480156105a357600080fd5b5061012963ffffffff6004358116906024351661ffff6044351660ff6064351667ffffffffffffffff60843516610deb565b3480156105e157600080fd5b506105f363ffffffff60043516610f35565b60408051600160a060020a03938416815291909216602082015281519081900390910190f35b34801561062557600080fd5b5061012963ffffffff60043516600160a060020a03602435166044351515606435610f64565b61012963ffffffff600435166024351515610fc9565b34801561066d57600080fd5b506101cb61100a565b6000543381141561068657816000555b5050565b61069261101f565b60005433811461069e57005b8351600c81146106aa57005b60025461ffff8111156106b957005b600281026004016020870151868117808355838752600184019350836002556004601c8801a0505050505050505050565b6106f261101f565b3383146106fb57005b60015463ffffffff81111561070c57005b62010003810264020001000401848155836001820155600182016001558183526004601c8401a05050505050565b600061074461101f565b600254808252602082f35b60008060008060008061076061103e565b602288026601000400010004018054600f60c060020a820416835263ffffffff60a060020a820416602084015263ffffffff700100000000000000000000000000000000820416604084015267ffffffffffffffff68010000000000000000820416606084015267ffffffffffffffff60018204166080840152600182015460a084015260c083f35b64020001000462010003830201805433811461080157005b506001015550565b606060008061081661105d565b60025480861061082257005b60028602600401805460608452600c6060850152806080850152600160a060020a036001820416602085015260018201549050806040850152608c84f35b606060008061086d61105d565b846402000000040154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0360018204166040830152608482f35b6000806000806108c261107c565b60228702660100040001000401601087116001881017156108df57005b600287028101805461ffff60f060020a820416845267ffffffffffffffff7001000000000000000000000000000000008204811660208601526801000000000000000082048116604086015281166060850152608084f35b6601000400010004602282020180544267ffffffffffffffff8216111561095a57005b6201000363ffffffff60a060020a8304160264020001000401600f60c060020a830416600284016002830160005b838110156109c957600281028301805461ffff60f060020a8204168401805467ffffffffffffffff9092169091019055600080855560019182015501610988565b5050505050505050565b6109db61101f565b6000543381146109e757005b60016003540161ffff8111156109f957005b855160048114610a0557005b60208701518560a060020a880217811780846402000000040155836003558386526002601e8701a0505050505050505050565b80516020820181810191505b81811015610b9557805163ffffffff60e060020a820416600f7b0100000000000000000000000000000000000000000000000000000083041667ffffffffffffffff7301000000000000000000000000000000000000008404166022830266010004000100040180546201000363ffffffff60a060020a83041602640200010004018460008114610b5957600f60c060020a840416861115610ae557600080fd5b600286028401805467ffffffffffffffff811680881115610b0557600080fd5b67ffffffffffffffff199190911690879003179081905564020000000460f060020a90910461ffff1690810154908301600201805460a060020a90920467ffffffffffffffff168702919091019055610b82565b60018401805480871115610b6c57600080fd5b86900390556002820180546305f5e10087020190555b5050505050505050600d81019050610a44565b505050565b6000806000806000806000610bad61109b565b60228a0266010004000100040160108a1160018b101715610bca57005b60028a028101600181015463ffffffff60e060020a8204168452600f790100000000000000000000000000000000000000000000000000820416600a0a620fffff79100000000000000000000000000000000000000000000000000083041602806020860152600f760100000000000000000000000000000000000000000000830416600a0a620fffff761000000000000000000000000000000000000000000000840416029050806040860152600f730100000000000000000000000000000000000000830416600a0a620fffff731000000000000000000000000000000000000000840416029050806060860152600f700100000000000000000000000000000000830416600a0a620fffff70100000000000000000000000000000000084041602905080608086015267ffffffffffffffff6801000000000000000083041660a086015267ffffffffffffffff600183041660c086015260e085f35b6000610d3361101f565b6201000384026402000100040183600282010154808352602083f35b6000610d5961101f565b600354808252602082f35b66010004000100046022850201805467ffffffffffffffff811615610d8557005b640200010004620100038602018054338114610d9d57005b600254868111610da957005b505060a060020a959095027001000000000000000000000000000000009490940290921792909217825550600191820154680100000000000000000291015550565b60035480841115610df857005b640200010004620100038602018054338114610e1057005b6601000400010004602289020180544267ffffffffffffffff82161115610e3357005b60a060020a810463ffffffff168914610e4857005b83880160020180546402000000048a015460a060020a900467ffffffffffffffff16880281811115610e7657005b8082038a1515610eaa578b15610e8857005b6001860180548b0167ffffffffffffffff811115610ea257005b828655808255005b600f60c060020a86041660108c11818d111715610ec357005b60028c028701818d1415610ef55782865560f060020a8e0267ffffffffffffffff8d1617815560e060020a6001820155005b805467ffffffffffffffff8082168e0190811115610f0f57005b9390965567ffffffffffffffff1990951690911790935550505050505050505050505050565b600080610f406110ba565b62010003840264020001000401805480835260018201549050806020840152604083f35b610f6c61101f565b640200010004620100038602018054338114610f8457005b84811415851615610f9157005b60028201805480861115610fa157005b8581038255600085600087898c6000f1801515610fbd57600080fd5b50505050505050505050565b64020001000462010003830201818015610fee578154338114610fe857005b50610ffc565b600154808510610ffa57005b505b506002018054340190555050565b600061101461101f565b600154808252602082f35b6020604051908101604052806001906020820280388339509192915050565b60c0604051908101604052806006906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b60e0604051908101604052806007906020820280388339509192915050565b604080518082018252906002908290803883395091929150505600a165627a7a72305820f125a07531e72d03aaa2fccaf69128b1d6a6354d0e176ccfca5567cce8b98b240029";

    public static final String FUNC_SET_CREATOR = "set_creator";

    public static final String FUNC_ADD_EXCHANGE = "add_exchange";

    public static final String FUNC_ADD_USER = "add_user";

    public static final String FUNC_GET_EXCHANGE_COUNT = "get_exchange_count";

    public static final String FUNC_GET_SESSION = "get_session";

    public static final String FUNC_UPDATE_USER_TRADE_ADDRESSES = "update_user_trade_addresses";

    public static final String FUNC_GET_EXCHANGE = "get_exchange";

    public static final String FUNC_GET_ASSET = "get_asset";

    public static final String FUNC_GET_POSITION = "get_position";

    public static final String FUNC_CLOSE_SESSION = "close_session";

    public static final String FUNC_ADD_ASSET = "add_asset";

    public static final String FUNC_PROCESS_WITHDRAWS = "process_withdraws";

    public static final String FUNC_GET_POSITION_LIMIT = "get_position_limit";

    public static final String FUNC_GET_USER_BALANCE = "get_user_balance";

    public static final String FUNC_GET_ASSET_COUNT = "get_asset_count";

    public static final String FUNC_START_SESSION = "start_session";

    public static final String FUNC_POSITION_DEPOSIT = "position_deposit";

    public static final String FUNC_GET_USER = "get_user";

    public static final String FUNC_WITHDRAW_ETH = "withdraw_eth";

    public static final String FUNC_DEPOSIT_ETH = "deposit_eth";

    public static final String FUNC_GET_USER_COUNT = "get_user_count";

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

    public RemoteCall<TransactionReceipt> add_user(String manage_address, String trade_address) {
        final Function function = new Function(
                FUNC_ADD_USER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(manage_address), 
                new org.web3j.abi.datatypes.Address(trade_address)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> get_exchange_count() {
        final Function function = new Function(FUNC_GET_EXCHANGE_COUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> get_session(BigInteger session_id) {
        final Function function = new Function(FUNC_GET_SESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
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

    public RemoteCall<TransactionReceipt> update_user_trade_addresses(BigInteger user_id, String trade_address) {
        final Function function = new Function(
                FUNC_UPDATE_USER_TRADE_ADDRESSES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.Address(trade_address)), 
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

    public RemoteCall<Tuple3<String, BigInteger, String>> get_asset(BigInteger asset_id) {
        final Function function = new Function(FUNC_GET_ASSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint16(asset_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint64>() {}, new TypeReference<Address>() {}));
        return new RemoteCall<Tuple3<String, BigInteger, String>>(
                new Callable<Tuple3<String, BigInteger, String>>() {
                    @Override
                    public Tuple3<String, BigInteger, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, BigInteger, String>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>> get_position(BigInteger session_id, BigInteger position_id) {
        final Function function = new Function(FUNC_GET_POSITION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id), 
                new org.web3j.abi.datatypes.generated.Uint8(position_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> close_session(BigInteger session_id) {
        final Function function = new Function(
                FUNC_CLOSE_SESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> add_asset(String symbol, BigInteger unit_scale, String contract_address) {
        final Function function = new Function(
                FUNC_ADD_ASSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(symbol), 
                new org.web3j.abi.datatypes.generated.Uint64(unit_scale), 
                new org.web3j.abi.datatypes.Address(contract_address)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> process_withdraws(byte[] requests) {
        final Function function = new Function(
                FUNC_PROCESS_WITHDRAWS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(requests)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> get_position_limit(BigInteger session_id, BigInteger position_id) {
        final Function function = new Function(FUNC_GET_POSITION_LIMIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id), 
                new org.web3j.abi.datatypes.generated.Uint8(position_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> get_user_balance(BigInteger user_id, BigInteger asset_id) {
        final Function function = new Function(FUNC_GET_USER_BALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint16(asset_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> get_asset_count() {
        final Function function = new Function(FUNC_GET_ASSET_COUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> start_session(BigInteger session_id, BigInteger user_id, BigInteger exchange_id, BigInteger expire_time) {
        final Function function = new Function(
                FUNC_START_SESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id), 
                new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id), 
                new org.web3j.abi.datatypes.generated.Uint64(expire_time)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> position_deposit(BigInteger session_id, BigInteger user_id, BigInteger asset_id, BigInteger position_id, BigInteger quantity) {
        final Function function = new Function(
                FUNC_POSITION_DEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id), 
                new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint16(asset_id), 
                new org.web3j.abi.datatypes.generated.Uint8(position_id), 
                new org.web3j.abi.datatypes.generated.Uint64(quantity)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<String, String>> get_user(BigInteger user_id) {
        final Function function = new Function(FUNC_GET_USER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        return new RemoteCall<Tuple2<String, String>>(
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> withdraw_eth(BigInteger user_id, String destination, Boolean check_self, BigInteger quantity) {
        final Function function = new Function(
                FUNC_WITHDRAW_ETH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.Address(destination), 
                new org.web3j.abi.datatypes.Bool(check_self), 
                new org.web3j.abi.datatypes.generated.Uint256(quantity)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deposit_eth(BigInteger user_id, Boolean check_self, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_DEPOSIT_ETH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.Bool(check_self)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> get_user_count() {
        final Function function = new Function(FUNC_GET_USER_COUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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
