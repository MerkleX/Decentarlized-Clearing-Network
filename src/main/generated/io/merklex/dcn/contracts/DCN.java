package io.merklex.dcn.contracts;

import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@javax.annotation.Generated(value="merklex-code-gen")
public class DCN {
    public static final String BINARY = "608060405234801561001057600080fd5b50336000557f4554482000000002540be4000000000000000000000000000000000000000000640200000003556113338061004c6000396000f30060806040526004361061011c5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630faf09048114610121578063136a9bf71461014457806314f604b8146101a85780631d601567146101d9578063274b3df4146101f757806337f265e6146101ff578063400ebbcf14610247578063431ec6011461029d578063541694cf146102c45780637c7347361461037a578063831b55d6146103eb5780639f9d8b4314610415578063a68c68b414610439578063a88d1902146104c5578063ace1ed07146104da578063b71a6dd61461050b578063c3ba7a481461052c578063e45565491461053d578063f12cafd014610586578063f894d398146105e1578063fb6b38571461060e575b600080fd5b34801561012d57600080fd5b50610142600160a060020a036004351661063f565b005b34801561015057600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261014294369492936024939284019190819084018382808284375094975050509235600160a060020a0316935061065392505050565b3480156101b457600080fd5b5061014263ffffffff6004358116906024351667ffffffffffffffff604435166106dc565b61014263ffffffff6004351667ffffffffffffffff60243516610878565b6101426109cc565b34801561020b57600080fd5b50610229600160a060020a036004351663ffffffff602435166109e3565b60408051938452602084019290925282820152519081900360600190f35b34801561025357600080fd5b50610277600160a060020a036004351663ffffffff60243581169060443516610a6a565b604080519485526020850193909352838301919091526060830152519081900360800190f35b3480156102a957600080fd5b506102b2610b21565b60408051918252519081900360200190f35b3480156102d057600080fd5b506102e263ffffffff60043516610b36565b604051808060200184600160a060020a0316600160a060020a03168152602001838152602001828103825285818151815260200191508051906020019080838360005b8381101561033d578181015183820152602001610325565b50505050905090810190601f16801561036a5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561038657600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526101429436949293602493928401919081908401838280828437509497505050833567ffffffffffffffff16945050505060200135600160a060020a0316610b81565b3480156103f757600080fd5b506102b2600160a060020a036004351663ffffffff60243516610c33565b34801561042157600080fd5b50610142600160a060020a0360043516602435610c58565b34801561044557600080fd5b5061045763ffffffff60043516610c97565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360008381101561033d578181015183820152602001610325565b3480156104d157600080fd5b506102b2610cfc565b3480156104e657600080fd5b506104ef610d11565b60408051600160a060020a039092168252519081900360200190f35b34801561051757600080fd5b5061014263ffffffff60043516602435610d17565b61014263ffffffff60043516610dea565b34801561054957600080fd5b5061056d600160a060020a036004351663ffffffff60243581169060443516610f09565b6040805192835260208301919091528051918290030190f35b34801561059257600080fd5b506105b6600160a060020a036004351663ffffffff60243581169060443516610f69565b6040805195865260208601949094528484019290925260608401526080830152519081900360a00190f35b3480156105ed57600080fd5b5061014263ffffffff60043516600160a060020a0360243516604435611048565b34801561061a57600080fd5b5061014263ffffffff6004358116906024351667ffffffffffffffff60443516611139565b6000543381141561064f57816000555b5050565b61065b611270565b610663611270565b60005433811461067957600183526001601f8401fd5b8451600c811461068f57600284526001601f8501fd5b60015463ffffffff8111156106aa57600385526001601f8601fd5b600281026003016020880151878117808355838752600184019350836001556004601c8801a050505050505050505050565b6106e4611270565b6106ec61128f565b6106f4611270565b6106fc6112ae565b6002548087118715171561071657600185526001601f8601fd5b506402000000038601547f23b872dd00000000000000000000000000000000000000000000000000000000845233600485015230602485015274010000000000000000000000000000000000000000810467ffffffffffffffff16860260448501819052600160a060020a0382166020856064886000855af18015156107a257600288526001601f8901fd5b85518015156107b757600389526001601f8a01fd5b50506403000000008a6401000000003302010264030000000360c060020a0101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff60018404160167ffffffffffffffff81111561082d5760048c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a0152600060608aa1505050505050505050505050505050565b610880611270565b6108886112ae565b62278d00420183118361a8c042011117156108a957600182526001601f8301fd5b6001548085106108bf57600283526001601f8401fd5b50640300000000846401000000003302010264030000000360c060020a01018360018201553382528460208301526000604083a13480156109c45767ffffffffffffffff6402540be4008204166402540be400810282039150825467ffffffffffffffff8267ffffffffffffffff6801000000000000000084041601168267ffffffffffffffff60018404160167ffffffffffffffff81111561096857600388526001601f8901fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff19841617865584156109a9576403000000033364010000000002018054860190555b338752896020880152600060408801526000606088a1505050505b505050505050565b640300000003336401000000000201805434019055565b60008060006109f06112ae565b640300000000856401000000008802010264030000000360c060020a010180546001820154835267ffffffffffffffff7801000000000000000000000000000000000000000000000000820416602084015267ffffffffffffffff7001000000000000000000000000000000008204166040840152606083f35b600080600080610a7861128f565b640300000000876401000000008a02010264030000000360c060020a01018660030281018054600182015467ffffffffffffffff7801000000000000000000000000000000000000000000000000830416855267ffffffffffffffff700100000000000000000000000000000000830416602086015267ffffffffffffffff68010000000000000000820416604086015267ffffffffffffffff60018204166060860152608085f35b6000610b2b611270565b600154808252602082f35b6060600080610b436112cd565b60028502600301805460608352600c6060840152806080840152600160a060020a036001820416602084015260018201549050806040840152608c83f35b610b89611270565b610b91611270565b600054338114610ba757600182526001601f8301fd5b60016002540163ffffffff811115610bc557600283526001601f8401fd5b865160048114610bdb57600384526001601f8501fd5b861515610bee57600484526001601f8501fd5b60208801518674010000000000000000000000000000000000000000890217811780846402000000030155836002558387526004601c8801a050505050505050505050565b6000610c3d611270565b64010000000084026403000000030183810154808352602083f35b610c60611270565b640100000000330264030000000301805480841115610c7b57005b838103825560008360008587896000f18015156109c457600080fd5b6060600080610ca46112cd565b846402000000030154606082526004606083015280608083015267ffffffffffffffff740100000000000000000000000000000000000000008204166020830152600160a060020a0360018204166040830152608482f35b6000610d06611270565b600254808252602082f35b60005490565b610d1f611270565b610d2761128f565b610d2f611270565b60025480861186151715610d4957600184526001601f8501fd5b507f23b872dd00000000000000000000000000000000000000000000000000000000825233600483015230602483015260448201849052640200000003850154600160a060020a0381166020836064866000855af1801515610db157600286526001601f8701fd5b8351801515610dc657600387526001601f8801fd5b50505050505033640100000000029290920164030000000301805491909101905550565b610df26112ae565b610dfa611270565b34801515610e0457005b600154808510610e1a57600183526001601f8401fd5b5067ffffffffffffffff6402540be4008204166402540be400810282039150640300000000856401000000003302010264030000000360c060020a0101805467ffffffffffffffff8367ffffffffffffffff6801000000000000000084041601168367ffffffffffffffff60018404160167ffffffffffffffff811115610ea757600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784558515610ee8576403000000033364010000000002018054870190555b338852886020890152600060408901526000606089a1505050505050505050565b600080610f146112ec565b640300000000856401000000008802010264030000000360c060020a01018460030281015467ffffffffffffffff68010000000000000000820416835267ffffffffffffffff60018204166020840152604083f35b6000806000806000610f7961128f565b640300000000886401000000008b02010264030000000360c060020a01018760030281016001810154600282015467ffffffffffffffff700100000000000000000000000000000000820416855267ffffffffffffffff7801000000000000000000000000000000000000000000000000830416602086015267ffffffffffffffff700100000000000000000000000000000000830416604086015267ffffffffffffffff68010000000000000000820416606086015267ffffffffffffffff6001820416608086015260a085f35b6110506112ae565b611058611270565b611060611270565b64010000000033026403000000030186640200000003015480158815171561108f57600383526001601f8401a0005b8782018054878110156110a957600485526001601f8601a0005b7fa9059cbb000000000000000000000000000000000000000000000000000000008752886004880152876024880152600160a060020a03600184041660208760448a6000855af180151561110457600587526001601f8801a0005b875180151561111a57600688526001601f8901a0005b8a84038555600088526001601f8901a050505050505050505050505050565b611141611270565b611149611270565b61115161128f565b640300000000866401000000003302010264030000000360c060020a0101640100000000330264030000000301868101805488640200000003015467ffffffffffffffff74010000000000000000000000000000000000000000820416808a02838111156111c55760018a526001601f8b01fd5b8084039350505050886003028401805467ffffffffffffffff8a67ffffffffffffffff6801000000000000000084041601168a67ffffffffffffffff60018404160167ffffffffffffffff8111156112235760028b526001601f8c01fd5b84865580680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528d60208a01528c60408a0152600060608aa15050505050505050505050505050565b6020604051908101604052806001906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b6060604051908101604052806003906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b604080518082018252906002908290803883395091929150505600a165627a7a723058203cff29414b68e93f85454a8ec08bb50bc4948ff3b77d2d56e4834da71a1b97b20029";
    public static Function set_creator(String new_creator) {
        return new Function(
            "set_creator",
            Collections.singletonList(
                new org.web3j.abi.datatypes.Address(new_creator)
            ),
            Collections.emptyList()
        );
    }
    public static Function add_exchange(String name, String addr) {
        return new Function(
            "add_exchange",
            Arrays.asList(
                new org.web3j.abi.datatypes.Utf8String(name)
                , new org.web3j.abi.datatypes.Address(addr)
            ),
            Collections.emptyList()
        );
    }
    public static Function deposit_asset_to_session(int exchange_id, int asset_id, long quantity) {
        return new Function(
            "deposit_asset_to_session",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
                , new org.web3j.abi.datatypes.generated.Uint64(quantity)
            ),
            Collections.emptyList()
        );
    }
    public static Function update_session(int exchange_id, long expire_time) {
        return new Function(
            "update_session",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint64(expire_time)
            ),
            Collections.emptyList()
        );
    }
    public static Function deposit_eth() {
        return new Function(
            "deposit_eth",
            Collections.emptyList(),
            Collections.emptyList()
        );
    }
    public static Function get_session(String user, int exchange_id) {
        return new Function(
            "get_session",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
            ),
            Arrays.asList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetSessionReturnValue query_get_session(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetSessionReturnValue returnValue = new GetSessionReturnValue();
        returnValue.expire_time = (BigInteger) values.get(0).getValue();
        returnValue.fee_limit = (BigInteger) values.get(1).getValue();
        returnValue.fee_used = (BigInteger) values.get(2).getValue();
        return returnValue;
    }
    public static Function get_session_position(String user, int exchange_id, int asset_id) {
        return new Function(
            "get_session_position",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
            ),
            Arrays.asList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetSessionPositionReturnValue query_get_session_position(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetSessionPositionReturnValue returnValue = new GetSessionPositionReturnValue();
        returnValue.ether_qty = (BigInteger) values.get(0).getValue();
        returnValue.asset_qty = (BigInteger) values.get(1).getValue();
        returnValue.ether_shift = (BigInteger) values.get(2).getValue();
        returnValue.asset_shift = (BigInteger) values.get(3).getValue();
        return returnValue;
    }
    public static Function get_exchange_count() {
        return new Function(
            "get_exchange_count",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetExchangeCountReturnValue query_get_exchange_count(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetExchangeCountReturnValue returnValue = new GetExchangeCountReturnValue();
        returnValue.count = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function get_exchange(int id) {
        return new Function(
            "get_exchange",
            Collections.singletonList(
                new org.web3j.abi.datatypes.generated.Uint32(id)
            ),
            Arrays.asList(
                new TypeReference<org.web3j.abi.datatypes.Utf8String>() {}
                , new TypeReference<org.web3j.abi.datatypes.Address>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetExchangeReturnValue query_get_exchange(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetExchangeReturnValue returnValue = new GetExchangeReturnValue();
        returnValue.name = (String) values.get(0).getValue();
        returnValue.addr = (String) values.get(1).getValue();
        returnValue.fee_balance = (BigInteger) values.get(2).getValue();
        return returnValue;
    }
    public static Function add_asset(String symbol, long unit_scale, String contract_address) {
        return new Function(
            "add_asset",
            Arrays.asList(
                new org.web3j.abi.datatypes.Utf8String(symbol)
                , new org.web3j.abi.datatypes.generated.Uint64(unit_scale)
                , new org.web3j.abi.datatypes.Address(contract_address)
            ),
            Collections.emptyList()
        );
    }
    public static Function get_balance(String user, int asset_id) {
        return new Function(
            "get_balance",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetBalanceReturnValue query_get_balance(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetBalanceReturnValue returnValue = new GetBalanceReturnValue();
        returnValue.return_balance = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function withdraw_eth(String destination, BigInteger amount) {
        return new Function(
            "withdraw_eth",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(destination)
                , new org.web3j.abi.datatypes.generated.Uint256(amount)
            ),
            Collections.emptyList()
        );
    }
    public static Function get_asset(int asset_id) {
        return new Function(
            "get_asset",
            Collections.singletonList(
                new org.web3j.abi.datatypes.generated.Uint32(asset_id)
            ),
            Arrays.asList(
                new TypeReference<org.web3j.abi.datatypes.Utf8String>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.Address>() {}
            )
        );
    }
    public static GetAssetReturnValue query_get_asset(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetAssetReturnValue returnValue = new GetAssetReturnValue();
        returnValue.symbol = (String) values.get(0).getValue();
        returnValue.unit_scale = ((BigInteger) values.get(1).getValue()).longValue();;
        returnValue.contract_address = (String) values.get(2).getValue();
        return returnValue;
    }
    public static Function get_asset_count() {
        return new Function(
            "get_asset_count",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetAssetCountReturnValue query_get_asset_count(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetAssetCountReturnValue returnValue = new GetAssetCountReturnValue();
        returnValue.count = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function get_creator() {
        return new Function(
            "get_creator",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.Address>() {}
            )
        );
    }
    public static GetCreatorReturnValue query_get_creator(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetCreatorReturnValue returnValue = new GetCreatorReturnValue();
        returnValue.dcn_creator = (String) values.get(0).getValue();
        return returnValue;
    }
    public static Function deposit_asset(int asset_id, BigInteger amount) {
        return new Function(
            "deposit_asset",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(asset_id)
                , new org.web3j.abi.datatypes.generated.Uint256(amount)
            ),
            Collections.emptyList()
        );
    }
    public static Function deposit_eth_to_session(int exchange_id) {
        return new Function(
            "deposit_eth_to_session",
            Collections.singletonList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
            ),
            Collections.emptyList()
        );
    }
    public static Function get_session_balance(String user, int exchange_id, int asset_id) {
        return new Function(
            "get_session_balance",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
            ),
            Arrays.asList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetSessionBalanceReturnValue query_get_session_balance(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetSessionBalanceReturnValue returnValue = new GetSessionBalanceReturnValue();
        returnValue.total_deposit = (BigInteger) values.get(0).getValue();
        returnValue.asset_balance = (BigInteger) values.get(1).getValue();
        return returnValue;
    }
    public static Function get_session_limit(String user, int exchange_id, int asset_id) {
        return new Function(
            "get_session_limit",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
            ),
            Arrays.asList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static GetSessionLimitReturnValue query_get_session_limit(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetSessionLimitReturnValue returnValue = new GetSessionLimitReturnValue();
        returnValue.version = (BigInteger) values.get(0).getValue();
        returnValue.min_ether = (BigInteger) values.get(1).getValue();
        returnValue.min_asset = (BigInteger) values.get(2).getValue();
        returnValue.long_max_price = (BigInteger) values.get(3).getValue();
        returnValue.short_min_price = (BigInteger) values.get(4).getValue();
        return returnValue;
    }
    public static Function withdraw_asset(int asset_id, String destination, BigInteger amount) {
        return new Function(
            "withdraw_asset",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(asset_id)
                , new org.web3j.abi.datatypes.Address(destination)
                , new org.web3j.abi.datatypes.generated.Uint256(amount)
            ),
            Collections.emptyList()
        );
    }
    public static Function transfer_to_session(int exchange_id, int asset_id, long quantity) {
        return new Function(
            "transfer_to_session",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
                , new org.web3j.abi.datatypes.generated.Uint64(quantity)
            ),
            Collections.emptyList()
        );
    }
    public static class GetSessionReturnValue {
        public BigInteger expire_time;
        public BigInteger fee_limit;
        public BigInteger fee_used;
    }
    public static class GetSessionPositionReturnValue {
        public BigInteger ether_qty;
        public BigInteger asset_qty;
        public BigInteger ether_shift;
        public BigInteger asset_shift;
    }
    public static class GetExchangeCountReturnValue {
        public BigInteger count;
    }
    public static class GetExchangeReturnValue {
        public String name;
        public String addr;
        public BigInteger fee_balance;
    }
    public static class GetBalanceReturnValue {
        public BigInteger return_balance;
    }
    public static class GetAssetReturnValue {
        public String symbol;
        public long unit_scale;
        public String contract_address;
    }
    public static class GetAssetCountReturnValue {
        public BigInteger count;
    }
    public static class GetCreatorReturnValue {
        public String dcn_creator;
    }
    public static class GetSessionBalanceReturnValue {
        public BigInteger total_deposit;
        public BigInteger asset_balance;
    }
    public static class GetSessionLimitReturnValue {
        public BigInteger version;
        public BigInteger min_ether;
        public BigInteger min_asset;
        public BigInteger long_max_price;
        public BigInteger short_min_price;
    }
}
