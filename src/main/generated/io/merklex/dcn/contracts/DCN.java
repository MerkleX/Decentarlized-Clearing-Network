package io.merklex.dcn.contracts;

import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@javax.annotation.Generated(value="merklex-code-gen")
public class DCN {
    public static final String BINARY = "608060405234801561001057600080fd5b50336000557f4554482000000002540be4000000000000000000000000000000000000000000640200000003556114778061004c6000396000f30060806040526004361061011c5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630faf09048114610121578063136a9bf71461014457806314f604b8146101a85780631d601567146101d9578063274b3df4146101f757806337f265e6146101ff578063400ebbcf1461025e578063431ec601146102c4578063541694cf146102f25780637c734736146103bc578063831b55d61461042d5780639f9d8b4314610469578063a68c68b41461048d578063a88d190214610519578063ace1ed071461052e578063b71a6dd61461055f578063c3ba7a4814610580578063e455654914610591578063f12cafd0146105e8578063f894d3981461065b578063fb6b385714610688575b600080fd5b34801561012d57600080fd5b50610142600160a060020a03600435166106b9565b005b34801561015057600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261014294369492936024939284019190819084018382808284375094975050509235600160a060020a031693506106cd92505050565b3480156101b457600080fd5b5061014263ffffffff6004358116906024351667ffffffffffffffff60443516610756565b61014263ffffffff6004351667ffffffffffffffff602435166108ff565b610142610ab9565b34801561020b57600080fd5b50610229600160a060020a036004351663ffffffff60243516610ad0565b6040805167ffffffffffffffff9586168152938516602085015291841683830152909216606082015290519081900360800190f35b34801561026a57600080fd5b5061028e600160a060020a036004351663ffffffff60243581169060443516610b81565b60408051600795860b860b815293850b850b602085015291840b840b83830152830b90920b606082015290519081900360800190f35b3480156102d057600080fd5b506102d9610c38565b6040805163ffffffff9092168252519081900360200190f35b3480156102fe57600080fd5b5061031063ffffffff60043516610c4d565b604051808060200184600160a060020a0316600160a060020a031681526020018367ffffffffffffffff1667ffffffffffffffff168152602001828103825285818151815260200191508051906020019080838360005b8381101561037f578181015183820152602001610367565b50505050905090810190601f1680156103ac5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156103c857600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526101429436949293602493928401919081908401838280828437509497505050833567ffffffffffffffff16945050505060200135600160a060020a0316610c98565b34801561043957600080fd5b50610457600160a060020a036004351663ffffffff60243516610d4a565b60408051918252519081900360200190f35b34801561047557600080fd5b50610142600160a060020a0360043516602435610d6f565b34801561049957600080fd5b506104ab63ffffffff60043516610db1565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360008381101561037f578181015183820152602001610367565b34801561052557600080fd5b506102d9610e16565b34801561053a57600080fd5b50610543610e2b565b60408051600160a060020a039092168252519081900360200190f35b34801561056b57600080fd5b5061014263ffffffff60043516602435610e31565b61014263ffffffff60043516610f04565b34801561059d57600080fd5b506105c1600160a060020a036004351663ffffffff60243581169060443516611030565b6040805167ffffffffffffffff938416815291909216602082015281519081900390910190f35b3480156105f457600080fd5b50610618600160a060020a036004351663ffffffff60243581169060443516611090565b6040805167ffffffffffffffff9687168152600795860b860b602082015293850b90940b8385015290841660608301529092166080830152519081900360a00190f35b34801561066757600080fd5b5061014263ffffffff60043516600160a060020a036024351660443561116f565b34801561069457600080fd5b5061014263ffffffff6004358116906024351667ffffffffffffffff60443516611250565b600054338114156106c957816000555b5050565b6106d5611394565b6106dd611394565b6000543381146106f357600183526001601f8401fd5b8451600c811461070957600284526001601f8501fd5b60015463ffffffff81111561072457600385526001601f8601fd5b600281026003016020880151878117808355838752600184019350836001556004601c8801a050505050505050505050565b61075e611394565b6107666113b3565b61076e611394565b6107766113d2565b6002548087118715171561079057600185526001601f8601fd5b506402000000038601547f23b872dd00000000000000000000000000000000000000000000000000000000845233600485015230602485015274010000000000000000000000000000000000000000810467ffffffffffffffff16860260448501819052600160a060020a0382166020856064886000855af180151561081c57600288526001601f8901fd5b855180151561083157600389526001601f8a01fd5b50506403000000008a6401000000003302010264030000000360c060020a0101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff60018404160167ffffffffffffffff8111156108a75760048c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a015260008051602061142c83398151915260608aa1505050505050505050505050505050565b610907611394565b61090f6113d2565b62278d00420183118361a8c0420111171561093057600182526001601f8301fd5b60015480851061094657600283526001601f8401fd5b5078010000000000000000000000000000000000000003000000043364010000000081028601640300000000029182018054680100000000000000009081900467ffffffffffffffff16600101028617905582526020820185905264030000000360c060020a01017f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604083a1348015610ab15767ffffffffffffffff6402540be4008204166402540be400810282039150825467ffffffffffffffff8267ffffffffffffffff6801000000000000000084041601168267ffffffffffffffff60018404160167ffffffffffffffff811115610a4857600388526001601f8901fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161786558415610a89576403000000033364010000000002018054860190555b3387528960208801526000604088015260008051602061142c833981519152606088a1505050505b505050505050565b640300000003336401000000000201805434019055565b600080600080610ade6113b3565b640300000000866401000000008902010264030000000360c060020a01018054600182015467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff6001820416602085015267ffffffffffffffff7801000000000000000000000000000000000000000000000000830416604085015267ffffffffffffffff7001000000000000000000000000000000008304166060850152608084f35b600080600080610b8f6113b3565b640300000000876401000000008a02010264030000000360c060020a01018660030281018054600182015467ffffffffffffffff7801000000000000000000000000000000000000000000000000830416855267ffffffffffffffff700100000000000000000000000000000000830416602086015267ffffffffffffffff68010000000000000000820416604086015267ffffffffffffffff60018204166060860152608085f35b6000610c42611394565b600154808252602082f35b6060600080610c5a6113f1565b60028502600301805460608352600c6060840152806080840152600160a060020a036001820416602084015260018201549050806040840152608c83f35b610ca0611394565b610ca8611394565b600054338114610cbe57600182526001601f8301fd5b60016002540163ffffffff811115610cdc57600283526001601f8401fd5b865160048114610cf257600384526001601f8501fd5b861515610d0557600484526001601f8501fd5b60208801518674010000000000000000000000000000000000000000890217811780846402000000030155836002558387526004601c8801a050505050505050505050565b6000610d54611394565b64010000000084026403000000030183810154808352602083f35b610d77611394565b640100000000330264030000000301805480841115610d9557600080fd5b838103825560008360008587896000f1801515610ab157600080fd5b6060600080610dbe6113f1565b846402000000030154606082526004606083015280608083015267ffffffffffffffff740100000000000000000000000000000000000000008204166020830152600160a060020a0360018204166040830152608482f35b6000610e20611394565b600254808252602082f35b60005490565b610e39611394565b610e416113b3565b610e49611394565b60025480861186151715610e6357600184526001601f8501fd5b507f23b872dd00000000000000000000000000000000000000000000000000000000825233600483015230602483015260448201849052640200000003850154600160a060020a0381166020836064866000855af1801515610ecb57600286526001601f8701fd5b8351801515610ee057600387526001601f8801fd5b50505050505033640100000000029290920164030000000301805491909101905550565b610f0c6113d2565b610f14611394565b34801515610f1e57005b600154808510610f3457600183526001601f8401fd5b5067ffffffffffffffff6402540be4008204166402540be400810282039150640300000000856401000000003302010264030000000360c060020a0101805467ffffffffffffffff8367ffffffffffffffff6801000000000000000084041601168367ffffffffffffffff60018404160167ffffffffffffffff811115610fc157600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784558515611002576403000000033364010000000002018054870190555b3388528860208901526000604089015260008051602061142c833981519152606089a1505050505050505050565b60008061103b611410565b640300000000856401000000008802010264030000000360c060020a01018460030281015467ffffffffffffffff68010000000000000000820416835267ffffffffffffffff60018204166020840152604083f35b60008060008060006110a06113b3565b640300000000886401000000008b02010264030000000360c060020a01018760030281016001810154600282015467ffffffffffffffff700100000000000000000000000000000000820416855267ffffffffffffffff7801000000000000000000000000000000000000000000000000830416602086015267ffffffffffffffff700100000000000000000000000000000000830416604086015267ffffffffffffffff68010000000000000000820416606086015267ffffffffffffffff6001820416608086015260a085f35b6111776113d2565b61117f611394565b611187611394565b6401000000003302640300000003018664020000000301548015881517156111b557600183526001601f8401fd5b8782018054878110156111ce57600285526001601f8601fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008752886004880152876024880152600160a060020a03600184041660208760448a6000855af180151561122857600387526001601f8801fd5b875180151561123d57600488526001601f8901fd5b5050509690960390955550505050505050565b611258611394565b611260611394565b6112686113b3565b640300000000866401000000003302010264030000000360c060020a0101640100000000330264030000000301868101805488640200000003015467ffffffffffffffff74010000000000000000000000000000000000000000820416808a02838111156112dc5760018a526001601f8b01fd5b8084039350505050886003028401805467ffffffffffffffff8a67ffffffffffffffff6801000000000000000084041601168a67ffffffffffffffff60018404160167ffffffffffffffff81111561133a5760028b526001601f8c01fd5b84865580680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528d60208a01528c60408a015260008051602061142c83398151915260608aa15050505050505050505050505050565b6020604051908101604052806001906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b6060604051908101604052806003906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b60408051808201825290600290829080388339509192915050560080e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500a165627a7a723058204556f5066b359d8a2b92ac417f5bad6a51316a361de0a7e6bc15b05061b6662c0029";
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
                new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
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
        returnValue.time_version = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.expire_time = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.fee_limit = ((BigInteger) values.get(2).getValue()).longValue();
        returnValue.fee_used = ((BigInteger) values.get(3).getValue()).longValue();
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
                new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
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
        returnValue.ether_qty = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.asset_qty = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.ether_shift = ((BigInteger) values.get(2).getValue()).longValue();
        returnValue.asset_shift = ((BigInteger) values.get(3).getValue()).longValue();
        return returnValue;
    }
    public static Function get_exchange_count() {
        return new Function(
            "get_exchange_count",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint32>() {}
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
        returnValue.count = ((BigInteger) values.get(0).getValue()).intValue();
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
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
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
        returnValue.fee_balance = ((BigInteger) values.get(2).getValue()).longValue();
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
        returnValue.unit_scale = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.contract_address = (String) values.get(2).getValue();
        return returnValue;
    }
    public static Function get_asset_count() {
        return new Function(
            "get_asset_count",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint32>() {}
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
        returnValue.count = ((BigInteger) values.get(0).getValue()).intValue();
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
                new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
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
        returnValue.total_deposit = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.asset_balance = ((BigInteger) values.get(1).getValue()).longValue();
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
                new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
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
        returnValue.version = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.min_ether = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.min_asset = ((BigInteger) values.get(2).getValue()).longValue();
        returnValue.long_max_price = ((BigInteger) values.get(3).getValue()).longValue();
        returnValue.short_min_price = ((BigInteger) values.get(4).getValue()).longValue();
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
    public static String DeployData() {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
            Collections.emptyList()
        );
        return BINARY + encodedConstructor;
    }
    public static class SessionUpdated {
        public String user;
        public long exchange_id;
    }
    public static final Event SessionUpdated_EVENT = new Event("SessionUpdated",
        Arrays.asList(
            new TypeReference<org.web3j.abi.datatypes.Address>() {}
            , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
        )
    );
    public static final String SessionUpdated_EVENT_HASH = EventEncoder.encode(SessionUpdated_EVENT);
    public static SessionUpdated ExtractSessionUpdated(Log log) {
        List<String> topics = log.getTopics();
        if (topics.size() == 0 || !SessionUpdated_EVENT_HASH.equals(topics.get(0))) {
            return null;
        }
        EventValues values = Contract.staticExtractEventParameters(SessionUpdated_EVENT, log);
        SessionUpdated event = new SessionUpdated();
        event.user = (String) values.getNonIndexedValues().get(0).getValue();
        event.exchange_id = ((BigInteger) values.getNonIndexedValues().get(1).getValue()).longValue();
        return event;
    }
    public static class PositionUpdated {
        public String user;
        public long exchange_id;
        public int asset_id;
    }
    public static final Event PositionUpdated_EVENT = new Event("PositionUpdated",
        Arrays.asList(
            new TypeReference<org.web3j.abi.datatypes.Address>() {}
            , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
            , new TypeReference<org.web3j.abi.datatypes.generated.Uint32>() {}
        )
    );
    public static final String PositionUpdated_EVENT_HASH = EventEncoder.encode(PositionUpdated_EVENT);
    public static PositionUpdated ExtractPositionUpdated(Log log) {
        List<String> topics = log.getTopics();
        if (topics.size() == 0 || !PositionUpdated_EVENT_HASH.equals(topics.get(0))) {
            return null;
        }
        EventValues values = Contract.staticExtractEventParameters(PositionUpdated_EVENT, log);
        PositionUpdated event = new PositionUpdated();
        event.user = (String) values.getNonIndexedValues().get(0).getValue();
        event.exchange_id = ((BigInteger) values.getNonIndexedValues().get(1).getValue()).longValue();
        event.asset_id = ((BigInteger) values.getNonIndexedValues().get(2).getValue()).intValue();
        return event;
    }
    public static class GetSessionReturnValue {
        public long time_version;
        public long expire_time;
        public long fee_limit;
        public long fee_used;
    }
    public static class GetSessionPositionReturnValue {
        public long ether_qty;
        public long asset_qty;
        public long ether_shift;
        public long asset_shift;
    }
    public static class GetExchangeCountReturnValue {
        public int count;
    }
    public static class GetExchangeReturnValue {
        public String name;
        public String addr;
        public long fee_balance;
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
        public int count;
    }
    public static class GetCreatorReturnValue {
        public String dcn_creator;
    }
    public static class GetSessionBalanceReturnValue {
        public long total_deposit;
        public long asset_balance;
    }
    public static class GetSessionLimitReturnValue {
        public long version;
        public long min_ether;
        public long min_asset;
        public long long_max_price;
        public long short_min_price;
    }
}
