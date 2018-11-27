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
    public static final String BINARY = "608060405234801561001057600080fd5b50336000557f4554482000000002540be400000000000000000000000000000000000000000064020000000355611ea98061004c6000396000f3fe6080604052600436106101195763ffffffff60e060020a6000350416630faf0904811461011e57806311a86b151461015357806314f604b81461021a5780631d60156714610265578063274b3df41461029857806337f265e6146102a0578063400ebbcf14610314578063431ec60114610391578063541694cf146103bf5780637c734736146104b6578063831b55d61461058157806383daf06d146105d25780639f9d8b4314610685578063a68c68b4146106be578063a88d19021461079a578063ace1ed07146107af578063b71a6dd6146107e0578063c3ba7a4814610816578063e455654914610839578063e7172f17146108a7578063f12cafd01461095a578063f894d398146109e4578063fb6b385714610a29575b600080fd5b34801561012a57600080fd5b506101516004803603602081101561014157600080fd5b5035600160a060020a0316610a74565b005b34801561015f57600080fd5b506101516004803603606081101561017657600080fd5b81019060208101813564010000000081111561019157600080fd5b8201836020820111156101a357600080fd5b803590602001918460018302840111640100000000831117156101c557600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505050813563ffffffff1692505060200135600160a060020a0316610a89565b34801561022657600080fd5b506101516004803603606081101561023d57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16610b1b565b6101516004803603604081101561027b57600080fd5b50803563ffffffff16906020013567ffffffffffffffff16610cbb565b610151610d9a565b3480156102ac57600080fd5b506102df600480360360408110156102c357600080fd5b508035600160a060020a0316906020013563ffffffff16610db1565b6040805167ffffffffffffffff9586168152938516602085015291841683830152909216606082015290519081900360800190f35b34801561032057600080fd5b5061035b6004803603606081101561033757600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516610e3b565b60408051600795860b860b815293850b850b602085015291840b840b83830152830b90920b606082015290519081900360800190f35b34801561039d57600080fd5b506103a6610ecb565b6040805163ffffffff9092168252519081900360200190f35b3480156103cb57600080fd5b506103ef600480360360208110156103e257600080fd5b503563ffffffff16610ee0565b60405180806020018567ffffffffffffffff1667ffffffffffffffff16815260200184600160a060020a0316600160a060020a031681526020018367ffffffffffffffff1667ffffffffffffffff168152602001828103825286818151815260200191508051906020019080838360005b83811015610478578181015183820152602001610460565b50505050905090810190601f1680156104a55780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b3480156104c257600080fd5b50610151600480360360608110156104d957600080fd5b8101906020810181356401000000008111156104f457600080fd5b82018360208201111561050657600080fd5b8035906020019184600183028401116401000000008311171561052857600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505050813567ffffffffffffffff1692505060200135600160a060020a0316610f3f565b34801561058d57600080fd5b506105c0600480360360408110156105a457600080fd5b508035600160a060020a0316906020013563ffffffff16610fcb565b60408051918252519081900360200190f35b3480156105de57600080fd5b50610151600480360360208110156105f557600080fd5b81019060208101813564010000000081111561061057600080fd5b82018360208201111561062257600080fd5b8035906020019184600183028401116401000000008311171561064457600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610ff0945050505050565b34801561069157600080fd5b50610151600480360360408110156106a857600080fd5b50600160a060020a038135169060200135611448565b3480156106ca57600080fd5b506106ee600480360360208110156106e157600080fd5b503563ffffffff1661148a565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360005b8381101561075d578181015183820152602001610745565b50505050905090810190601f16801561078a5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156107a657600080fd5b506103a66114de565b3480156107bb57600080fd5b506107c46114f3565b60408051600160a060020a039092168252519081900360200190f35b3480156107ec57600080fd5b506101516004803603604081101561080357600080fd5b5063ffffffff81351690602001356114f9565b6101516004803603602081101561082c57600080fd5b503563ffffffff166115cc565b34801561084557600080fd5b506108806004803603606081101561085c57600080fd5b50600160a060020a038135169063ffffffff602082013581169160400135166116e9565b6040805167ffffffffffffffff938416815291909216602082015281519081900390910190f35b3480156108b357600080fd5b50610151600480360360208110156108ca57600080fd5b8101906020810181356401000000008111156108e557600080fd5b8201836020820111156108f757600080fd5b8035906020019184600183028401116401000000008311171561091957600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550611744945050505050565b34801561096657600080fd5b506109a16004803603606081101561097d57600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516611aed565b6040805167ffffffffffffffff9687168152600795860b860b602082015293850b90940b8385015290841660608301529092166080830152519081900360a00190f35b3480156109f057600080fd5b5061015160048036036060811015610a0757600080fd5b5063ffffffff81351690600160a060020a036020820135169060400135611b98565b348015610a3557600080fd5b5061015160048036036060811015610a4c57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16611c6c565b600054338114610a8357600080fd5b50600055565b610a91611da7565b600054338114610aa757600182526001601f8301fd5b845160088114610abd57600283526001601f8401fd5b60025480861115610ad457600384526001601f8501fd5b60015463ffffffff811115610aef57600485526001601f8601fd5b6020979097015160a060020a969096029094179094176003600287020155505050600191820190915550565b610b23611da7565b610b2b611dc6565b610b33611da7565b610b3b611de5565b60025480871187151715610b5557600185526001601f8601fd5b506402000000038601547f23b872dd00000000000000000000000000000000000000000000000000000000845233600485015230602485015260a060020a810467ffffffffffffffff16860260448501819052600160a060020a0382166020856064886000855af1801515610bd057600288526001601f8901fd5b8551801515610be557600389526001601f8a01fd5b50506403000000008a6401000000003302010264030000000360c060020a0101896003028101805467ffffffffffffffff8b67ffffffffffffffff604060020a84041601168b67ffffffffffffffff60018404160167ffffffffffffffff811115610c565760048c526001601f8d01fd5b80604060020a8302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b610cc3611da7565b610ccb611de5565b62278d00420183118361a8c04201111715610cec57600182526001601f8301fd5b600154808510610d0257600283526001601f8401fd5b5078010000000000000000000000000000000000000003000000043364010000000081028601640300000000029182018054604060020a9081900467ffffffffffffffff16600101028617905582526020820185905264030000000360c060020a01017f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604083a150610d94846115cc565b50505050565b640300000003336401000000000201805434019055565b600080600080610dbf611dc6565b640300000000866401000000008902010264030000000360c060020a01018054600182015467ffffffffffffffff604060020a820416845267ffffffffffffffff6001820416602085015267ffffffffffffffff60c060020a830416604085015267ffffffffffffffff608060020a8304166060850152608084f35b600080600080610e49611dc6565b640300000000876401000000008a02010264030000000360c060020a01018660030281018054600182015467ffffffffffffffff60c060020a830416855267ffffffffffffffff608060020a830416602086015267ffffffffffffffff604060020a820416604086015267ffffffffffffffff60018204166060860152608085f35b6000610ed5611da7565b600154808252602082f35b60606000806000610eef611e04565b60028602600301805460808352600860808401528060a084015263ffffffff60a060020a8204166020840152600160a060020a03600182041660408401526001820154905080606084015260a883f35b610f47611da7565b600054338114610f5d57600182526001601f8301fd5b60016002540163ffffffff811115610f7b57600283526001601f8401fd5b855160048114610f9157600384526001601f8501fd5b851515610fa457600484526001601f8501fd5b506020959095015160a060020a949094029092179092176402000000038401555050600255565b6000610fd5611da7565b64010000000084026403000000030183810154808352602083f35b610ff8611da7565b611000611e23565b611008611dc6565b8351600090602086016095821461102557600186526001601f8701fd5b8051601482019150600160a060020a036c0100000000000000000000000082041693507f74be7520fc933d8061b6cf113d28a772f7a40539ab5e0e8276dd066dd71a7d69865281519050602082019150600063ffffffff60e060020a83041680602089015263ffffffff60c060020a8404168060608a01526002820260030154600160a060020a03600182041680331415156110c75760028c526001601f8d01fd5b505060038102640300000000836401000000008a02010264030000000360c060020a0101019250505067ffffffffffffffff608060020a830416806040890152600282015467ffffffffffffffff608060020a82041682811015156111325760038b526001601f8c01fd5b505067ffffffffffffffff604060020a8404168060808a015267ffffffffffffffff60018504168060a08b015280604060020a830217608060020a840217600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a810291506780000000000000008116156111ba5767ffffffffffffffff19175b8060c08a01525067ffffffffffffffff608060020a840416608060020a8102821791506780000000000000008116156111f95767ffffffffffffffff19175b8060e08a01525067ffffffffffffffff604060020a840416604060020a8102821791506780000000000000008116156112385767ffffffffffffffff19175b610100890181905267ffffffffffffffff84169182179167800000000000000085161561126b5767ffffffffffffffff19175b806101208b0152600184015467ffffffffffffffff604060020a8204166780000000000000008116156112a45767ffffffffffffffff19175b9092039167ffffffffffffffff81166780000000000000008216156112cf5767ffffffffffffffff19175b85549203608060020a80840467ffffffffffffffff908116929092010260c060020a808504929092169490940102929092176fffffffffffffffffffffffffffffffff9091161783555060019182015561014087207f190100000000000000000000000000000000000000000000000000000000000088527f8bdc799ab1e4f88b464481578308e5bde325b7ed088fe2b99495c7924d58c7f9600289015260228801526042872080885283516020808a01829052858101516040808c018290529687015160ff7f0100000000000000000000000000000000000000000000000000000000000000909104166060808d0182905288516000808252818601808c5297909752808a019290925281019390935260808301529451919550919360a080840194509092601f198301929081900390910190855afa158015611417573d6000803e3d6000fd5b5050604051601f190151600160a060020a031691505081811461144057600485526001601f8601fd5b505050505050565b611450611da7565b64010000000033026403000000030180548084111561146e57600080fd5b838103825560008360008587896000f180151561144057600080fd5b6060600080611497611e43565b846402000000030154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0360018204166040830152608482f35b60006114e8611da7565b600254808252602082f35b60005490565b611501611da7565b611509611dc6565b611511611da7565b6002548086118615171561152b57600184526001601f8501fd5b507f23b872dd00000000000000000000000000000000000000000000000000000000825233600483015230602483015260448201849052640200000003850154600160a060020a0381166020836064866000855af180151561159357600286526001601f8701fd5b83518015156115a857600387526001601f8801fd5b50505050505033640100000000029290920164030000000301805491909101905550565b6115d4611de5565b6115dc611da7565b348015156115e657005b67ffffffffffffffff6402540be4008204166402540be400810282039150640300000000856401000000003302010264030000000360c060020a0101805467ffffffffffffffff8367ffffffffffffffff604060020a84041601168367ffffffffffffffff60018404160167ffffffffffffffff81111561166d57600287526001601f8801fd5b80604060020a8302176fffffffffffffffffffffffffffffffff19841617845585156116a9576403000000033364010000000002018054870190555b338852886020890152600060408901527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606089a1505050505050505050565b6000806116f4611e62565b640300000000856401000000008802010264030000000360c060020a01018460030281015467ffffffffffffffff604060020a820416835267ffffffffffffffff60018204166020840152604083f35b6001810181518101815163ffffffff60e060020a8204166004840193505b602884840310611ae6578351915060ff7b0100000000000000000000000000000000000000000000000000000083041663ffffffff60e060020a84041660286101608302018601858111156117b657600080fd5b6000805b82891015611acf578851965064030000000086640100000000600160a060020a036c010000000000000000000000008b041602010264030000000360c060020a010160148a019950895167ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a83041667ffffffffffffffff604060020a8404166780000000000000008316156118565767ffffffffffffffff19831792505b6780000000000000008216156118755767ffffffffffffffff19821791505b8454968301969582019567ffffffffffffffff604060020a86048116818316860103908111156118a457600080fd5b67ffffffffffffffff1991909116178555600389028501805467ffffffffffffffff8082168501908111156118d857600080fd5b67ffffffffffffffff60c060020a8304166780000000000000008116156119055767ffffffffffffffff19175b67ffffffffffffffff608060020a8404166780000000000000008116156119325767ffffffffffffffff19175b8782019150868101905082604060020a67ffffffffffffffff604060020a8704160217608060020a82021760c060020a8302179350838555677fffffffffffffff8113677fffffffffffffff8313171561198b57600080fd5b600185015467ffffffffffffffff60c060020a8204166780000000000000008116156119bd5767ffffffffffffffff19175b67ffffffffffffffff608060020a8304166780000000000000008116156119ea5767ffffffffffffffff19175b80841282861217156119fb57600080fd5b505050677fffffffffffffff198112677fffffffffffffff1983121715611a2157600080fd5b600285015460026000831202600084120180600381146101195760018114611a505760028114611a8b57611abd565b831515611a5c57600080fd5b836305f5e10086600003020467ffffffffffffffff604060020a850416811115611a8557600080fd5b50611abd565b841515611a9757600080fd5b60008490036305f5e10086020467ffffffffffffffff8416811015611abb57600080fd5b505b505050505050505050505050506117ba565b80821715611adc57600080fd5b5050505050611762565b5050505050565b6000806000806000611afd611dc6565b640300000000886401000000008b02010264030000000360c060020a01016003880281016001810154600282015467ffffffffffffffff608060020a820416855267ffffffffffffffff60c060020a830416602086015267ffffffffffffffff608060020a830416604086015267ffffffffffffffff604060020a820416606086015267ffffffffffffffff6001820416608086015260a085f35b611ba0611de5565b611ba8611da7565b611bb0611da7565b831515611bb957005b640100000000330264030000000301868101805486811015611be157600284526001601f8501fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008652876004870152866024870152886402000000030154600160a060020a03600182041660208760448a6000855af1801515611c4457600387526001601f8801fd5b8751801515611c5957600488526001601f8901fd5b5050505095909503909455505050505050565b611c74611da7565b611c7c611da7565b611c84611dc6565b640300000000866401000000003302010264030000000360c060020a0101640100000000330264030000000301868101805488640200000003015467ffffffffffffffff60a060020a820416808a0283811115611ce75760018a526001601f8b01fd5b8084039350505050886003028401805467ffffffffffffffff8a67ffffffffffffffff604060020a84041601168a67ffffffffffffffff60018404160167ffffffffffffffff811115611d405760028b526001601f8c01fd5b84865580604060020a8302176fffffffffffffffffffffffffffffffff1984161784553389528d60208a01528c60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa15050505050505050505050505050565b6020604051908101604052806001906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b6060604051908101604052806003906020820280388339509192915050565b60c0604051908101604052806006906020820280388339509192915050565b61014060405190810160405280600a906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b6040805180820182529060029082908038833950919291505056fea165627a7a72305820098a25661a250f6a209e43721c8d31636ec9468d8b64b1547044b40cb072f4da0029";
    public static Function set_creator(String new_creator) {
        return new Function(
            "set_creator",
            Collections.singletonList(
                new org.web3j.abi.datatypes.Address(new_creator)
            ),
            Collections.emptyList()
        );
    }
    public static Function add_exchange(String name, int quote_asset_id, String addr) {
        return new Function(
            "add_exchange",
            Arrays.asList(
                new org.web3j.abi.datatypes.Utf8String(name)
                , new org.web3j.abi.datatypes.generated.Uint32(quote_asset_id)
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
        returnValue.quote_qty = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.base_qty = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.quote_shift = ((BigInteger) values.get(2).getValue()).longValue();
        returnValue.base_shift = ((BigInteger) values.get(3).getValue()).longValue();
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
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
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
        returnValue.quote_asset_id = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.addr = (String) values.get(2).getValue();
        returnValue.fee_balance = ((BigInteger) values.get(3).getValue()).longValue();
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
        returnValue.min_quote = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.min_base = ((BigInteger) values.get(2).getValue()).longValue();
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
        public long quote_qty;
        public long base_qty;
        public long quote_shift;
        public long base_shift;
    }
    public static class GetExchangeCountReturnValue {
        public int count;
    }
    public static class GetExchangeReturnValue {
        public String name;
        public long quote_asset_id;
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
        public long min_quote;
        public long min_base;
        public long long_max_price;
        public long short_min_price;
    }
}
