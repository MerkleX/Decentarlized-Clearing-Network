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
    public static final String BINARY = "608060405234801561001057600080fd5b50336000557f4554482000000002540be400000000000000000000000000000000000000000064020000000355611b878061004c6000396000f3006080604052600436106101195763ffffffff60e060020a6000350416630faf0904811461011e578063136a9bf71461014157806314f604b8146101a55780631d601567146101d6578063274b3df4146101f457806337f265e6146101fc578063400ebbcf1461025b578063431ec601146102c1578063541694cf146102ef5780637c734736146103b9578063831b55d61461042a57806383daf06d146104665780639f9d8b43146104bf578063a68c68b4146104e3578063a88d19021461056f578063ace1ed0714610584578063b71a6dd6146105b5578063c3ba7a48146105d6578063e4556549146105e7578063e7172f171461063e578063f12cafd014610697578063f894d3981461070a578063fb6b385714610737575b600080fd5b34801561012a57600080fd5b5061013f600160a060020a0360043516610768565b005b34801561014d57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261013f94369492936024939284019190819084018382808284375094975050509235600160a060020a0316935061077d92505050565b3480156101b157600080fd5b5061013f63ffffffff6004358116906024351667ffffffffffffffff604435166107eb565b61013f63ffffffff6004351667ffffffffffffffff6024351661099c565b61013f610a7b565b34801561020857600080fd5b50610226600160a060020a036004351663ffffffff60243516610a92565b6040805167ffffffffffffffff9586168152938516602085015291841683830152909216606082015290519081900360800190f35b34801561026757600080fd5b5061028b600160a060020a036004351663ffffffff60243581169060443516610b1c565b60408051600795860b860b815293850b850b602085015291840b840b83830152830b90920b606082015290519081900360800190f35b3480156102cd57600080fd5b506102d6610bac565b6040805163ffffffff9092168252519081900360200190f35b3480156102fb57600080fd5b5061030d63ffffffff60043516610bc1565b604051808060200184600160a060020a0316600160a060020a031681526020018367ffffffffffffffff1667ffffffffffffffff168152602001828103825285818151815260200191508051906020019080838360005b8381101561037c578181015183820152602001610364565b50505050905090810190601f1680156103a95780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156103c557600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261013f9436949293602493928401919081908401838280828437509497505050833567ffffffffffffffff16945050505060200135600160a060020a0316610c0c565b34801561043657600080fd5b50610454600160a060020a036004351663ffffffff60243516610ca9565b60408051918252519081900360200190f35b34801561047257600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261013f943694929360249392840191908190840183828082843750949750610cce9650505050505050565b3480156104cb57600080fd5b5061013f600160a060020a0360043516602435611123565b3480156104ef57600080fd5b5061050163ffffffff60043516611165565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360008381101561037c578181015183820152602001610364565b34801561057b57600080fd5b506102d66111ca565b34801561059057600080fd5b506105996111df565b60408051600160a060020a039092168252519081900360200190f35b3480156105c157600080fd5b5061013f63ffffffff600435166024356111e5565b61013f63ffffffff600435166112b8565b3480156105f357600080fd5b50610617600160a060020a036004351663ffffffff602435811690604435166113d5565b6040805167ffffffffffffffff938416815291909216602082015281519081900390910190f35b34801561064a57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261013f9436949293602493928401919081908401838280828437509497506114309650505050505050565b3480156106a357600080fd5b506106c7600160a060020a036004351663ffffffff602435811690604435166117d9565b6040805167ffffffffffffffff9687168152600795860b860b602082015293850b90940b8385015290841660608301529092166080830152519081900360a00190f35b34801561071657600080fd5b5061013f63ffffffff60043516600160a060020a0360243516604435611884565b34801561074357600080fd5b5061013f63ffffffff6004358116906024351667ffffffffffffffff60443516611958565b60005433811461077757600080fd5b50600055565b610785611aa4565b60005433811461079b57600182526001601f8301fd5b8351600c81146107b157600283526001601f8401fd5b60015463ffffffff8111156107cc57600384526001601f8501fd5b6020959095015193909317600360028602015550505060019081019055565b6107f3611aa4565b6107fb611ac3565b610803611aa4565b61080b611ae2565b6002548087118715171561082557600185526001601f8601fd5b506402000000038601547f23b872dd00000000000000000000000000000000000000000000000000000000845233600485015230602485015274010000000000000000000000000000000000000000810467ffffffffffffffff16860260448501819052600160a060020a0382166020856064886000855af18015156108b157600288526001601f8901fd5b85518015156108c657600389526001601f8a01fd5b50506403000000008a6401000000003302010264030000000360c060020a0101896003028101805467ffffffffffffffff8b67ffffffffffffffff604060020a84041601168b67ffffffffffffffff60018404160167ffffffffffffffff8111156109375760048c526001601f8d01fd5b80604060020a8302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b6109a4611aa4565b6109ac611ae2565b62278d00420183118361a8c042011117156109cd57600182526001601f8301fd5b6001548085106109e357600283526001601f8401fd5b5078010000000000000000000000000000000000000003000000043364010000000081028601640300000000029182018054604060020a9081900467ffffffffffffffff16600101028617905582526020820185905264030000000360c060020a01017f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604083a150610a75846112b8565b50505050565b640300000003336401000000000201805434019055565b600080600080610aa0611ac3565b640300000000866401000000008902010264030000000360c060020a01018054600182015467ffffffffffffffff604060020a820416845267ffffffffffffffff6001820416602085015267ffffffffffffffff60c060020a830416604085015267ffffffffffffffff608060020a8304166060850152608084f35b600080600080610b2a611ac3565b640300000000876401000000008a02010264030000000360c060020a01018660030281018054600182015467ffffffffffffffff60c060020a830416855267ffffffffffffffff608060020a830416602086015267ffffffffffffffff604060020a820416604086015267ffffffffffffffff60018204166060860152608085f35b6000610bb6611aa4565b600154808252602082f35b6060600080610bce611b01565b60028502600301805460608352600c6060840152806080840152600160a060020a036001820416602084015260018201549050806040840152608c83f35b610c14611aa4565b600054338114610c2a57600182526001601f8301fd5b60016002540163ffffffff811115610c4857600283526001601f8401fd5b855160048114610c5e57600384526001601f8501fd5b851515610c7157600484526001601f8501fd5b506020959095015174010000000000000000000000000000000000000000949094029092179092176402000000038401555050600255565b6000610cb3611aa4565b64010000000084026403000000030183810154808352602083f35b610cd6611aa4565b610cde611b20565b610ce6611ac3565b600080855160208701609582141515610d0557600187526001601f8801fd5b8051601482019150600160a060020a036c0100000000000000000000000082041694507f74be7520fc933d8061b6cf113d28a772f7a40539ab5e0e8276dd066dd71a7d69875281519050602082019150600063ffffffff60e060020a8304168060208a015263ffffffff60c060020a8404168060608b01526002820260030154600160a060020a0360018204168033141515610da75760028d526001601f8e01fd5b505060038102640300000000836401000000008b02010264030000000360c060020a0101019250505067ffffffffffffffff608060020a8304168060408a0152600282015467ffffffffffffffff608060020a8204168281101515610e125760038c526001601f8d01fd5b505067ffffffffffffffff604060020a8404168060808b015267ffffffffffffffff60018504168060a08c015280604060020a830217608060020a840217600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a81029150678000000000000000811615610e9a5767ffffffffffffffff19175b8060c08b01525067ffffffffffffffff608060020a840416608060020a810282179150678000000000000000811615610ed95767ffffffffffffffff19175b8060e08b01525067ffffffffffffffff604060020a840416604060020a810282179150678000000000000000811615610f185767ffffffffffffffff19175b6101008a0181905267ffffffffffffffff841691821791678000000000000000851615610f4b5767ffffffffffffffff19175b806101208c0152600184015467ffffffffffffffff604060020a820416678000000000000000811615610f845767ffffffffffffffff19175b9092039167ffffffffffffffff8116678000000000000000821615610faf5767ffffffffffffffff19175b85549203608060020a80840467ffffffffffffffff908116929092010260c060020a808504929092169490940102929092176fffffffffffffffffffffffffffffffff9091161783555060019182015561014088207f190100000000000000000000000000000000000000000000000000000000000089527f8bdc799ab1e4f88b464481578308e5bde325b7ed088fe2b99495c7924d58c7f960028a015260228901526042882080895283516020808b01829052858101516040808d018290529687015160ff7f0100000000000000000000000000000000000000000000000000000000000000909104166060808e0182905288516000808252818601808c5297909752808a01929092528101939093526080830152945192955060a0808201959450601f19840193918290030191865af11580156110f2573d6000803e3d6000fd5b5050604051601f190151600160a060020a031691505081811461111b57600485526001601f8601fd5b505050505050565b61112b611aa4565b64010000000033026403000000030180548084111561114957600080fd5b838103825560008360008587896000f180151561111b57600080fd5b6060600080611172611b01565b846402000000030154606082526004606083015280608083015267ffffffffffffffff740100000000000000000000000000000000000000008204166020830152600160a060020a0360018204166040830152608482f35b60006111d4611aa4565b600254808252602082f35b60005490565b6111ed611aa4565b6111f5611ac3565b6111fd611aa4565b6002548086118615171561121757600184526001601f8501fd5b507f23b872dd00000000000000000000000000000000000000000000000000000000825233600483015230602483015260448201849052640200000003850154600160a060020a0381166020836064866000855af180151561127f57600286526001601f8701fd5b835180151561129457600387526001601f8801fd5b50505050505033640100000000029290920164030000000301805491909101905550565b6112c0611ae2565b6112c8611aa4565b348015156112d257005b67ffffffffffffffff6402540be4008204166402540be400810282039150640300000000856401000000003302010264030000000360c060020a0101805467ffffffffffffffff8367ffffffffffffffff604060020a84041601168367ffffffffffffffff60018404160167ffffffffffffffff81111561135957600287526001601f8801fd5b80604060020a8302176fffffffffffffffffffffffffffffffff1984161784558515611395576403000000033364010000000002018054870190555b338852886020890152600060408901527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606089a1505050505050505050565b6000806113e0611b40565b640300000000856401000000008802010264030000000360c060020a01018460030281015467ffffffffffffffff604060020a820416835267ffffffffffffffff60018204166020840152604083f35b6001810181518101815163ffffffff60e060020a8204166004840193505b6028848403106117d2578351915060ff7b0100000000000000000000000000000000000000000000000000000083041663ffffffff60e060020a84041660286101608302018601858111156114a257600080fd5b6000805b828910156117bb578851965064030000000086640100000000600160a060020a036c010000000000000000000000008b041602010264030000000360c060020a010160148a019950895167ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a83041667ffffffffffffffff604060020a8404166780000000000000008316156115425767ffffffffffffffff19831792505b6780000000000000008216156115615767ffffffffffffffff19821791505b8454968301969582019567ffffffffffffffff604060020a860481168183168601039081111561159057600080fd5b67ffffffffffffffff1991909116178555600389028501805467ffffffffffffffff8082168501908111156115c457600080fd5b67ffffffffffffffff60c060020a8304166780000000000000008116156115f15767ffffffffffffffff19175b67ffffffffffffffff608060020a84041667800000000000000081161561161e5767ffffffffffffffff19175b8782019150868101905082604060020a67ffffffffffffffff604060020a8704160217608060020a82021760c060020a8302179350838555677fffffffffffffff8113677fffffffffffffff8313171561167757600080fd5b600185015467ffffffffffffffff60c060020a8204166780000000000000008116156116a95767ffffffffffffffff19175b67ffffffffffffffff608060020a8304166780000000000000008116156116d65767ffffffffffffffff19175b80841282861217156116e757600080fd5b505050677fffffffffffffff198112677fffffffffffffff198312171561170d57600080fd5b60028501546002600083120260008412018060038114610119576001811461173c5760028114611777576117a9565b83151561174857600080fd5b836305f5e10086600003020467ffffffffffffffff604060020a85041681111561177157600080fd5b506117a9565b84151561178357600080fd5b60008490036305f5e10086020467ffffffffffffffff84168110156117a757600080fd5b505b505050505050505050505050506114a6565b808217156117c857600080fd5b505050505061144e565b5050505050565b60008060008060006117e9611ac3565b640300000000886401000000008b02010264030000000360c060020a01016003880281016001810154600282015467ffffffffffffffff608060020a820416855267ffffffffffffffff60c060020a830416602086015267ffffffffffffffff608060020a830416604086015267ffffffffffffffff604060020a820416606086015267ffffffffffffffff6001820416608086015260a085f35b61188c611ae2565b611894611aa4565b61189c611aa4565b8315156118a557005b6401000000003302640300000003018681018054868110156118cd57600284526001601f8501fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008652876004870152866024870152886402000000030154600160a060020a03600182041660208760448a6000855af180151561193057600387526001601f8801fd5b875180151561194557600488526001601f8901fd5b5050505095909503909455505050505050565b611960611aa4565b611968611aa4565b611970611ac3565b640300000000866401000000003302010264030000000360c060020a0101640100000000330264030000000301868101805488640200000003015467ffffffffffffffff74010000000000000000000000000000000000000000820416808a02838111156119e45760018a526001601f8b01fd5b8084039350505050886003028401805467ffffffffffffffff8a67ffffffffffffffff604060020a84041601168a67ffffffffffffffff60018404160167ffffffffffffffff811115611a3d5760028b526001601f8c01fd5b84865580604060020a8302176fffffffffffffffffffffffffffffffff1984161784553389528d60208a01528c60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa15050505050505050505050505050565b6020604051908101604052806001906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b6060604051908101604052806003906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b61014060405190810160405280600a906020820280388339509192915050565b604080518082018252906002908290803883395091929150505600a165627a7a72305820a7b196dc095296c181ed6257f419ffd6eb172954721ba31a389e7548ae2caff40029";
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
    public static Function set_limit(String data) {
        return new Function(
            "set_limit",
            Collections.singletonList(
                new org.web3j.abi.datatypes.DynamicBytes(Numeric.hexStringToByteArray(data))
            ),
            Collections.emptyList()
        );
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
