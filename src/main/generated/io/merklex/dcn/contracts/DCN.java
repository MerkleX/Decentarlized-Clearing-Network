package io.merklex.dcn.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

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
    private static final String BINARY = "608060405234801561001057600080fd5b50336000557f4554482000000002540be4000000000000000000000000000000000000000000640200000004556118a48061004c6000396000f30060806040526004361061013a5763ffffffff60e060020a600035041663043e2459811461013f5780630488941f1461017a5780630faf0904146101c3578063136a9bf7146101e45780632341f8b7146102485780632d581e7114610298578063431ec601146102b9578063432ea2be146102e057806344f0e2111461033f578063541694cf146103695780635bc1d3391461041f57806362a86d20146104a957806369772b53146104f35780637c73473614610511578063842585081461058257806390fd19e6146105db5780639526f9f3146106375780639d26d1631461065c578063a88d19021461067a578063aaf1e2b31461068f578063ab340db9146106c6578063bf1f623214610704578063cefa919214610748578063d46f0c081461077a578063f3924f29146107a2578063fd33482d146107b8575b600080fd5b34801561014b57600080fd5b5061017863ffffffff60043516602435151561ffff60443516600160a060020a03606435166084356107cd565b005b610178600160a060020a036004351663ffffffff6024358116906044351667ffffffffffffffff6064351661ffff60843581169060a43581169060c43581169060e435166108f0565b3480156101cf57600080fd5b50610178600160a060020a036004351661092b565b3480156101f057600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261017894369492936024939284019190819084018382808284375094975050509235600160a060020a0316935061093f92505050565b34801561025457600080fd5b5061017863ffffffff6004358116906024358116906044351667ffffffffffffffff6064351661ffff60843581169060a43581169060c43581169060e4351661099f565b3480156102a457600080fd5b50610178600160a060020a0360043516610b00565b3480156102c557600080fd5b506102ce610b46565b60408051918252519081900360200190f35b3480156102ec57600080fd5b506102fe63ffffffff60043516610b5b565b6040805197885260208801969096528686019490945260608601929092526080850152600160a060020a031660a084015260c0830152519081900360e00190f35b34801561034b57600080fd5b5061017863ffffffff60043516600160a060020a0360243516610c1f565b34801561037557600080fd5b5061038763ffffffff60043516610c62565b604051808060200184600160a060020a0316600160a060020a03168152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156103e25781810151838201526020016103ca565b50505050905090810190601f16801561040f5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561042b57600080fd5b5061043b61ffff60043516610cad565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a0316815260200182810382528581815181526020019150805190602001908083836000838110156103e25781810151838201526020016103ca565b3480156104b557600080fd5b506104cd63ffffffff6004351660ff60243516610d01565b604080519485526020850193909352838301919091526060830152519081900360800190f35b3480156104ff57600080fd5b5061017863ffffffff60043516610d84565b34801561051d57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526101789436949293602493928401919081908401838280828437509497505050833567ffffffffffffffff16945050505060200135600160a060020a0316610ebb565b34801561058e57600080fd5b506040805160206004803580820135601f8101849004840285018401909552848452610178943694929360249392840191908190840183828082843750949750610f299650505050505050565b3480156105e757600080fd5b506105ff63ffffffff6004351660ff6024351661108c565b604080519788526020880196909652868601949094526060860192909252608085015260a084015260c0830152519081900360e00190f35b34801561064357600080fd5b506102ce63ffffffff6004351661ffff6024351661121b565b34801561066857600080fd5b5061017863ffffffff60043516611241565b34801561068657600080fd5b506102ce6112c7565b34801561069b57600080fd5b5061017863ffffffff6004358116906024358116906044351667ffffffffffffffff606435166112dc565b3480156106d257600080fd5b5061017863ffffffff6004358116906024351661ffff6044351660ff6064351667ffffffffffffffff608435166113ac565b34801561071057600080fd5b5061072263ffffffff600435166115d7565b60408051600160a060020a03938416815291909216602082015281519081900390910190f35b34801561075457600080fd5b5061017863ffffffff60043516600160a060020a03602435166044351515606435611606565b34801561078657600080fd5b5061017863ffffffff6004351661ffff6024351660443561166b565b61017863ffffffff600435166024351515611763565b3480156107c457600080fd5b506102ce6117ad565b6107d56117c2565b6107dd6117e1565b6107e56117e1565b64020001000462010003890201805433811461080857600183526001601f8401a0005b8681141589161561082057600283526001601f8401a0005b5086640200000004015480158815171561084157600383526001601f8401a0005b87600283010180548781101561085e57600485526001601f8601a0005b7fa9059cbb000000000000000000000000000000000000000000000000000000008752886004880152876024880152600160a060020a03600184041660208760448a6000855af18015156108b957600587526001601f8801a0005b87518015156108cf57600688526001601f8901a0005b8a84038555600088526001601f8901a0505050505050505050505050505050565b6001546108fc89610b00565b600034111561091057610910816001611763565b610920818989898989898961099f565b505050505050505050565b6000543381141561093b57816000555b5050565b6109476117e1565b60005433811461095357005b8351600c811461095f57005b60025461ffff81111561096e57005b600281026004016020870151868117808355838752600184019350836002556004601c8801a0505050505050505050565b6109ab868989886112dc565b6022860266010004000100040167ffffffffffffffff6402540be4003404166001820181815417815550600354808711871517156109e557005b60f060020a8702600284015560e060020a6003840155825486158288111715610a1d5760c060020a600f0219811660c060020a178455005b60f060020a8702600485015560e060020a600585015585158287111715610a685760c060020a600f021981167802000000000000000000000000000000000000000000000000178455005b60f060020a8602600685015560e060020a600785015584158286111715610ab35760c060020a600f021981167803000000000000000000000000000000000000000000000000178455005b60f060020a949094026008840155505060e060020a600982015560c060020a600f021991909116780400000000000000000000000000000000000000000000000017905550505050505050565b610b086117e1565b60015463ffffffff811115610b1957005b62010003810264020001000401338155836001820155600182016001558183526004601c8401a050505050565b6000610b506117e1565b600254808252602082f35b6000806000806000806000610b6e611800565b602289026601000400010004018054600f60c060020a820416835263ffffffff60a060020a820416602084015263ffffffff700100000000000000000000000000000000820416604084015267ffffffffffffffff68010000000000000000820416606084015267ffffffffffffffff600182041660808401526001820154600160a060020a036801000000000000000082041660a085015267ffffffffffffffff600182041660c085015260e084f35b610c276117e1565b640200010004620100038402018054338114610c4a57600183526001601f8401a0005b836001830155600083526001601f8401a05050505050565b6060600080610c6f61181f565b60028502600401805460608352600c6060840152806080840152600160a060020a036001820416602084015260018201549050806040840152608c83f35b6060600080610cba61181f565b846402000000040154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0360018204166040830152608482f35b600080600080610d0f61183e565b6022870266010004000100040160108711600188101715610d2c57005b600287028101805461ffff60f060020a820416845267ffffffffffffffff7001000000000000000000000000000000008204811660208601526801000000000000000082048116604086015281166060850152608084f35b610d8c6117e1565b66010004000100046022830201805467ffffffffffffffff811642811181151715610dbe57600184526001601f8501a0005b78100000000000000000000000000000000000000000000000006001670fffffffffffffff7810000000000000000000000000000000000000000000000000850416010283556201000363ffffffff60a060020a840416026402000100040160028101805460018501546402540be40067ffffffffffffffff60018304160282019150818355600f60c060020a8704166002880160005b82811015610ea2576002810282015461ffff60f060020a820416640200000004810154908801805460a060020a90920467ffffffffffffffff908116931692909202019055600101610e55565b5060008a526001601f8b01a05050505050505050505050565b610ec36117e1565b600054338114610ecf57005b60016003540161ffff811115610ee157005b855160048114610eed57005b851515610ef657005b60208701518560a060020a880217811780846402000000040155836003558386526002601e8701a0505050505050505050565b80516020820181810191505b8181101561108757805163ffffffff60e060020a820416600f7b0100000000000000000000000000000000000000000000000000000083041667ffffffffffffffff7301000000000000000000000000000000000000008404166022830266010004000100040180546201000363ffffffff60a060020a8304160264020001000401846000811461104a57600f60c060020a840416861115610fd657600080fd5b600286028401805467ffffffffffffffff811680881115610ff657600080fd5b67ffffffffffffffff199190911690879003179081905564020000000460f060020a90910461ffff1690810154908301600201805460a060020a90920467ffffffffffffffff168702919091019055611074565b6001840180548087111561105d57600080fd5b86900390556002820180546402540be40087020190555b5050505050505050600d81019050610f35565b505050565b600080600080600080600061109f611800565b60228a0266010004000100040160108a1160018b1017156110bc57005b60028a028101600181015463ffffffff60e060020a8204168452600f790100000000000000000000000000000000000000000000000000820416600a0a620fffff79100000000000000000000000000000000000000000000000000083041602806020860152600f760100000000000000000000000000000000000000000000830416600a0a620fffff761000000000000000000000000000000000000000000000840416029050806040860152600f730100000000000000000000000000000000000000830416600a0a620fffff731000000000000000000000000000000000000000840416029050806060860152600f700100000000000000000000000000000000830416600a0a620fffff70100000000000000000000000000000000084041602905080608086015267ffffffffffffffff6801000000000000000083041660a086015267ffffffffffffffff600183041660c086015260e085f35b60006112256117e1565b6201000384026402000100040183600282010154808352602083f35b6112496117e1565b60228202660100040001000401805463ffffffff70010000000000000000000000000000000082041660028102600401805433600160a060020a03600183041614151561129d57600186526001601f8701a0005b50505067ffffffffffffffff191667ffffffffffffffff421617808255600083526001601f8401a0005b60006112d16117e1565b600354808252602082f35b6112e46117e1565b60228502660100040001000401805462278d00420184118461a8c0420111171561130a57005b67ffffffffffffffff81161561131c57005b64020001000462010003870201805433811461133457005b60025487811161134057005b60a060020a890270010000000000000000000000000000000089028817178086556001848101546801000000000000000002908701558a875293507fc3934e844399df6122666c45922384445cb616ed1402ecf7d2e39bd2529a2746602087a150505050505050505050565b6113b46117e1565b6113bc6117e1565b600354808611156113c957005b506402000100046201000387020180543381146113ed57600184526001601f8501a0005b5066010004000100046022890201805467ffffffffffffffff811642111561141c57600285526001601f8601a0005b60a060020a810463ffffffff16891461143c57600385526001601f8601a0005b82880160020180546402000000048a015460a060020a900467ffffffffffffffff1688028181111561147557600488526001601f8901a0005b8082038a15156114db578b1561149257600589526001601f8a01a0005b60018601805467ffffffffffffffff8082168d01908111156114bb5760068c526001601f8d01a0005b8387558067ffffffffffffffff19831617835560008c526001601f8d01a0005b6001600f60c060020a8704160160108c11818d1117156115025760078a526001601f8b01a0005b60028c028701818d141561157c5782865560c060020a600f0219871660c060020a830217885560f060020a8e0267ffffffffffffffff8d1617815560e060020a60018201558f8a527f3d0a2b1c6f8e72f333688e33b7fc1767f042d33eaef1d3b5db5968567033e91f60208ba160008b526001601f8c01a0005b805467ffffffffffffffff8082168e01908111156115a15760088d526001601f8e01a0005b84885567ffffffffffffffff1991909116811780835560008d52906001601f8e01a0505050505050505050505050505050505050565b6000806115e261185d565b62010003840264020001000401805480835260018201549050806020840152604083f35b61160e6117e1565b64020001000462010003860201805433811461162657005b8581141585161561163357005b6002820180548086111561164357005b8581038255600085600087898c6000f180151561165f57600080fd5b50505050505050505050565b6116736117e1565b61167b61183e565b6116836117e1565b6402000100046201000387020180543381146116a657600185526001601f8601a0005b508564020000000401548015871517156116c757600285526001601f8601a0005b7f23b872dd00000000000000000000000000000000000000000000000000000000845233600485015230602485015260448401869052600160a060020a0381166020846064876000855af180151561172657600387526001601f8801a0005b845180151561173c57600488526001601f8901a0005b89600286010180548a8101825560008a526001601f8b01a050505050505050505050505050565b61176b6117e1565b6402000100046201000384020182801561179057815433811461178a57005b5061179e565b60015480861061179c57005b505b50600201805434019055505050565b60006117b76117e1565b600154808252602082f35b6060604051908101604052806003906020820280388339509192915050565b6020604051908101604052806001906020820280388339509192915050565b60e0604051908101604052806007906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b604080518082018252906002908290803883395091929150505600a165627a7a72305820ba3d5898e86d30032613b579fb5b5ba57719956280fdc5c7c1f00d71f426fee30029";

    public static final String FUNC_WITHDRAW_ASSET = "withdraw_asset";

    public static final String FUNC_JUMPSTART_USER = "jumpstart_user";

    public static final String FUNC_SET_CREATOR = "set_creator";

    public static final String FUNC_ADD_EXCHANGE = "add_exchange";

    public static final String FUNC_JUMPSTART_SESSION = "jumpstart_session";

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

    public static final String FUNC_END_SESSION = "end_session";

    public static final String FUNC_GET_ASSET_COUNT = "get_asset_count";

    public static final String FUNC_START_SESSION = "start_session";

    public static final String FUNC_POSITION_DEPOSIT = "position_deposit";

    public static final String FUNC_GET_USER = "get_user";

    public static final String FUNC_WITHDRAW_ETH = "withdraw_eth";

    public static final String FUNC_DEPOSIT_ASSET = "deposit_asset";

    public static final String FUNC_DEPOSIT_ETH = "deposit_eth";

    public static final String FUNC_GET_USER_COUNT = "get_user_count";

    public static final Event SESSIONSTARTED_EVENT = new Event("SessionStarted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event POSITIONADDED_EVENT = new Event("PositionAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected DCN(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DCN(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> withdraw_asset(BigInteger user_id, Boolean check_self, BigInteger asset_id, String destination, BigInteger quantity) {
        final Function function = new Function(
                FUNC_WITHDRAW_ASSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.Bool(check_self), 
                new org.web3j.abi.datatypes.generated.Uint16(asset_id), 
                new org.web3j.abi.datatypes.Address(destination), 
                new org.web3j.abi.datatypes.generated.Uint256(quantity)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> jumpstart_user(String trade_address, BigInteger exchange_id, BigInteger session_id, BigInteger expire_time, BigInteger trade_asset_1, BigInteger trade_asset_2, BigInteger trade_asset_3, BigInteger trade_asset_4, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_JUMPSTART_USER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(trade_address), 
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id), 
                new org.web3j.abi.datatypes.generated.Uint32(session_id), 
                new org.web3j.abi.datatypes.generated.Uint64(expire_time), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_1), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_2), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_3), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_4)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
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

    public RemoteCall<TransactionReceipt> jumpstart_session(BigInteger user_id, BigInteger exchange_id, BigInteger session_id, BigInteger expire_time, BigInteger trade_asset_1, BigInteger trade_asset_2, BigInteger trade_asset_3, BigInteger trade_asset_4) {
        final Function function = new Function(
                FUNC_JUMPSTART_SESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id), 
                new org.web3j.abi.datatypes.generated.Uint32(session_id), 
                new org.web3j.abi.datatypes.generated.Uint64(expire_time), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_1), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_2), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_3), 
                new org.web3j.abi.datatypes.generated.Uint16(trade_asset_4)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> add_user(String trade_address) {
        final Function function = new Function(
                FUNC_ADD_USER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(trade_address)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> get_exchange_count() {
        final Function function = new Function(FUNC_GET_EXCHANGE_COUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>> get_session(BigInteger session_id) {
        final Function function = new Function(FUNC_GET_SESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>>(
                new Callable<Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
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

    public RemoteCall<TransactionReceipt> end_session(BigInteger session_id) {
        final Function function = new Function(
                FUNC_END_SESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(session_id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteCall<TransactionReceipt> deposit_asset(BigInteger user_id, BigInteger asset_id, BigInteger quantity) {
        final Function function = new Function(
                FUNC_DEPOSIT_ASSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(user_id), 
                new org.web3j.abi.datatypes.generated.Uint16(asset_id), 
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

    public List<SessionStartedEventResponse> getSessionStartedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SESSIONSTARTED_EVENT, transactionReceipt);
        ArrayList<SessionStartedEventResponse> responses = new ArrayList<SessionStartedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SessionStartedEventResponse typedResponse = new SessionStartedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SessionStartedEventResponse> sessionStartedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SessionStartedEventResponse>() {
            @Override
            public SessionStartedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SESSIONSTARTED_EVENT, log);
                SessionStartedEventResponse typedResponse = new SessionStartedEventResponse();
                typedResponse.log = log;
                typedResponse.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SessionStartedEventResponse> sessionStartedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SESSIONSTARTED_EVENT));
        return sessionStartedEventObservable(filter);
    }

    public List<PositionAddedEventResponse> getPositionAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(POSITIONADDED_EVENT, transactionReceipt);
        ArrayList<PositionAddedEventResponse> responses = new ArrayList<PositionAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PositionAddedEventResponse typedResponse = new PositionAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PositionAddedEventResponse> positionAddedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, PositionAddedEventResponse>() {
            @Override
            public PositionAddedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(POSITIONADDED_EVENT, log);
                PositionAddedEventResponse typedResponse = new PositionAddedEventResponse();
                typedResponse.log = log;
                typedResponse.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<PositionAddedEventResponse> positionAddedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(POSITIONADDED_EVENT));
        return positionAddedEventObservable(filter);
    }

    public static DCN load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DCN(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DCN load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DCN(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class SessionStartedEventResponse {
        public Log log;

        public BigInteger session_id;
    }

    public static class PositionAddedEventResponse {
        public Log log;

        public BigInteger session_id;
    }
}
