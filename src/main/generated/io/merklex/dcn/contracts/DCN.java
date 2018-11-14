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
import org.web3j.utils.Numeric;

@javax.annotation.Generated(value="merklex-code-gen")
public class DCN {
    public static final String BINARY = "608060405234801561001057600080fd5b50336000557f4554482000000002540be40000000000000000000000000000000000000000006402000000035561142a8061004c6000396000f3006080604052600436106101275763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630faf0904811461012c578063136a9bf71461014f57806314f604b8146101b35780631d601567146101e4578063274b3df41461020257806337f265e61461020a578063400ebbcf14610269578063431ec601146102cf578063541694cf146102fd5780637c734736146103c7578063831b55d6146104385780639f9d8b4314610474578063a68c68b414610498578063a88d190214610524578063ace1ed0714610539578063b71a6dd61461056a578063c3ba7a481461058b578063e45565491461059c578063e7172f17146105f3578063f12cafd01461064c578063f894d398146106bf578063fb6b3857146106ec575b600080fd5b34801561013857600080fd5b5061014d600160a060020a036004351661071d565b005b34801561015b57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261014d94369492936024939284019190819084018382808284375094975050509235600160a060020a0316935061073292505050565b3480156101bf57600080fd5b5061014d63ffffffff6004358116906024351667ffffffffffffffff604435166107a0565b61014d63ffffffff6004351667ffffffffffffffff6024351661095b565b61014d610a3f565b34801561021657600080fd5b50610234600160a060020a036004351663ffffffff60243516610a56565b6040805167ffffffffffffffff9586168152938516602085015291841683830152909216606082015290519081900360800190f35b34801561027557600080fd5b50610299600160a060020a036004351663ffffffff60243581169060443516610b07565b60408051600795860b860b815293850b850b602085015291840b840b83830152830b90920b606082015290519081900360800190f35b3480156102db57600080fd5b506102e4610bbe565b6040805163ffffffff9092168252519081900360200190f35b34801561030957600080fd5b5061031b63ffffffff60043516610bd3565b604051808060200184600160a060020a0316600160a060020a031681526020018367ffffffffffffffff1667ffffffffffffffff168152602001828103825285818151815260200191508051906020019080838360005b8381101561038a578181015183820152602001610372565b50505050905090810190601f1680156103b75780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156103d357600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261014d9436949293602493928401919081908401838280828437509497505050833567ffffffffffffffff16945050505060200135600160a060020a0316610c1e565b34801561044457600080fd5b50610462600160a060020a036004351663ffffffff60243516610cbb565b60408051918252519081900360200190f35b34801561048057600080fd5b5061014d600160a060020a0360043516602435610ce0565b3480156104a457600080fd5b506104b663ffffffff60043516610d2a565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360008381101561038a578181015183820152602001610372565b34801561053057600080fd5b506102e4610d8f565b34801561054557600080fd5b5061054e610da4565b60408051600160a060020a039092168252519081900360200190f35b34801561057657600080fd5b5061014d63ffffffff60043516602435610daa565b61014d63ffffffff60043516610e7d565b3480156105a857600080fd5b506105cc600160a060020a036004351663ffffffff60243581169060443516610fa4565b6040805167ffffffffffffffff938416815291909216602082015281519081900390910190f35b3480156105ff57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261014d9436949293602493928401919081908401838280828437509497506110049650505050505050565b34801561065857600080fd5b5061067c600160a060020a036004351663ffffffff6024358116906044351661105e565b6040805167ffffffffffffffff9687168152600795860b860b602082015293850b90940b8385015290841660608301529092166080830152519081900360a00190f35b3480156106cb57600080fd5b5061014d63ffffffff60043516600160a060020a036024351660443561113d565b3480156106f857600080fd5b5061014d63ffffffff6004358116906024351667ffffffffffffffff60443516611211565b60005433811461072c57600080fd5b50600055565b61073a611367565b60005433811461075057600182526001601f8301fd5b8351600c811461076657600283526001601f8401fd5b60015463ffffffff81111561078157600384526001601f8501fd5b6020959095015193909317600360028602015550505060019081019055565b6107a8611367565b6107b0611386565b6107b8611367565b6107c06113a5565b600254808711871517156107da57600185526001601f8601fd5b506402000000038601547f23b872dd00000000000000000000000000000000000000000000000000000000845233600485015230602485015274010000000000000000000000000000000000000000810467ffffffffffffffff16860260448501819052600160a060020a0382166020856064886000855af180151561086657600288526001601f8901fd5b855180151561087b57600389526001601f8a01fd5b50506403000000008a6401000000003302010264030000000360c060020a0101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff60018404160167ffffffffffffffff8111156108f15760048c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b610963611367565b61096b6113a5565b62278d00420183118361a8c0420111171561098c57600182526001601f8301fd5b6001548085106109a257600283526001601f8401fd5b5078010000000000000000000000000000000000000003000000043364010000000081028601640300000000029182018054680100000000000000009081900467ffffffffffffffff16600101028617905582526020820185905264030000000360c060020a01017f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604083a150610a3984610e7d565b50505050565b640300000003336401000000000201805434019055565b600080600080610a64611386565b640300000000866401000000008902010264030000000360c060020a01018054600182015467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff6001820416602085015267ffffffffffffffff7801000000000000000000000000000000000000000000000000830416604085015267ffffffffffffffff7001000000000000000000000000000000008304166060850152608084f35b600080600080610b15611386565b640300000000876401000000008a02010264030000000360c060020a01018660030281018054600182015467ffffffffffffffff7801000000000000000000000000000000000000000000000000830416855267ffffffffffffffff700100000000000000000000000000000000830416602086015267ffffffffffffffff68010000000000000000820416604086015267ffffffffffffffff60018204166060860152608085f35b6000610bc8611367565b600154808252602082f35b6060600080610be06113c4565b60028502600301805460608352600c6060840152806080840152600160a060020a036001820416602084015260018201549050806040840152608c83f35b610c26611367565b600054338114610c3c57600182526001601f8301fd5b60016002540163ffffffff811115610c5a57600283526001601f8401fd5b855160048114610c7057600384526001601f8501fd5b851515610c8357600484526001601f8501fd5b506020959095015174010000000000000000000000000000000000000000949094029092179092176402000000038401555050600255565b6000610cc5611367565b64010000000084026403000000030183810154808352602083f35b610ce8611367565b640100000000330264030000000301805480841115610d0657600080fd5b838103825560008360008587896000f1801515610d2257600080fd5b505050505050565b6060600080610d376113c4565b846402000000030154606082526004606083015280608083015267ffffffffffffffff740100000000000000000000000000000000000000008204166020830152600160a060020a0360018204166040830152608482f35b6000610d99611367565b600254808252602082f35b60005490565b610db2611367565b610dba611386565b610dc2611367565b60025480861186151715610ddc57600184526001601f8501fd5b507f23b872dd00000000000000000000000000000000000000000000000000000000825233600483015230602483015260448201849052640200000003850154600160a060020a0381166020836064866000855af1801515610e4457600286526001601f8701fd5b8351801515610e5957600387526001601f8801fd5b50505050505033640100000000029290920164030000000301805491909101905550565b610e856113a5565b610e8d611367565b34801515610e9757005b67ffffffffffffffff6402540be4008204166402540be400810282039150640300000000856401000000003302010264030000000360c060020a0101805467ffffffffffffffff8367ffffffffffffffff6801000000000000000084041601168367ffffffffffffffff60018404160167ffffffffffffffff811115610f2357600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784558515610f64576403000000033364010000000002018054870190555b338852886020890152600060408901527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606089a1505050505050505050565b600080610faf6113e3565b640300000000856401000000008802010264030000000360c060020a01018460030281015467ffffffffffffffff68010000000000000000820416835267ffffffffffffffff60018204166020840152604083f35b600181510160015b60288183031061105957805460ff7b01000000000000000000000000000000000000000000000000000000820416602861016082020184818501111561105157600080fd5b50505061100c565b505050565b600080600080600061106e611386565b640300000000886401000000008b02010264030000000360c060020a01018760030281016001810154600282015467ffffffffffffffff700100000000000000000000000000000000820416855267ffffffffffffffff7801000000000000000000000000000000000000000000000000830416602086015267ffffffffffffffff700100000000000000000000000000000000830416604086015267ffffffffffffffff68010000000000000000820416606086015267ffffffffffffffff6001820416608086015260a085f35b6111456113a5565b61114d611367565b611155611367565b83151561115e57005b64010000000033026403000000030186810180548681101561118657600284526001601f8501fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008652876004870152866024870152886402000000030154600160a060020a03600182041660208760448a6000855af18015156111e957600387526001601f8801fd5b87518015156111fe57600488526001601f8901fd5b5050505095909503909455505050505050565b611219611367565b611221611367565b611229611386565b640300000000866401000000003302010264030000000360c060020a0101640100000000330264030000000301868101805488640200000003015467ffffffffffffffff74010000000000000000000000000000000000000000820416808a028381111561129d5760018a526001601f8b01fd5b8084039350505050886003028401805467ffffffffffffffff8a67ffffffffffffffff6801000000000000000084041601168a67ffffffffffffffff60018404160167ffffffffffffffff8111156112fb5760028b526001601f8c01fd5b84865580680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528d60208a01528c60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa15050505050505050505050505050565b6020604051908101604052806001906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b6060604051908101604052806003906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b604080518082018252906002908290803883395091929150505600a165627a7a7230582000eb10723870ce878be7f3b2311259c4833a97a4d8c545a2c3dd94947d718a400029";
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
        returnValue.version = ((BigInteger) values.get(0).getValue()).longValue();
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
    public static Function apply_settlement_groups(String data) {
        return new Function(
            "apply_settlement_groups",
            Collections.singletonList(
                new org.web3j.abi.datatypes.DynamicBytes(Numeric.hexStringToByteArray(data))
            ),
            Collections.emptyList()
        );
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
        public long version;
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
