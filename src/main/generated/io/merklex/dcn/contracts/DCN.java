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
    public static final String BINARY = "608060405234801561001057600080fd5b503360005561206f806100246000396000f3fe608060405234801561001057600080fd5b50600436106101305760003560e060020a90048063a68c68b4116100b1578063e455654911610075578063e45565491461073c578063e7172f171461079b578063f894d39814610840578063fb6b385714610878578063fc429e6a146108b657610130565b8063a68c68b414610583578063a88d19021461063e578063ace1ed0714610646578063b71a6dd61461066a578063be6ae3311461069357610130565b8063541694cf116100f8578063541694cf146102de5780637c734736146103a2578063831b55d61461045f57806383daf06d146104a35780638e98cee11461054857610130565b80630faf09041461013557806311a86b151461015d57806314f604b81461021657806337f265e614610254578063431ec601146102bd575b600080fd5b61015b6004803603602081101561014b57600080fd5b5035600160a060020a03166108f4565b005b61015b6004803603606081101561017357600080fd5b81019060208101813564010000000081111561018e57600080fd5b8201836020820111156101a057600080fd5b803590602001918460018302840111640100000000831117156101c257600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813563ffffffff1692505060200135600160a060020a0316610909565b61015b6004803603606081101561022c57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff1661099c565b6102866004803603604081101561026a57600080fd5b508035600160a060020a0316906020013563ffffffff16610b55565b60405167ffffffffffffffff9485168152928416602084015290831660408084019190915292166060820152608001905180910390f35b6102c5610c0b565b60405163ffffffff909116815260200160405180910390f35b610301600480360360208110156102f457600080fd5b503563ffffffff16610c20565b60405167ffffffffffffffff8085166020830152600160a060020a03841660408301528216606082015260808082528190810186818151815260200191508051906020019080838360005b8381101561036457808201518382015260200161034c565b50505050905090810190601f1680156103915780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b61015b600480360360608110156103b857600080fd5b8101906020810181356401000000008111156103d357600080fd5b8201836020820111156103e557600080fd5b8035906020019184600183028401116401000000008311171561040757600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813567ffffffffffffffff1692505060200135600160a060020a0316610c7c565b6104916004803603604081101561047557600080fd5b508035600160a060020a0316906020013563ffffffff16610d1b565b60405190815260200160405180910390f35b61015b600480360360208110156104b957600080fd5b8101906020810181356401000000008111156104d457600080fd5b8201836020820111156104e657600080fd5b8035906020019184600183028401116401000000008311171561050857600080fd5b91908080601f0160208091040260200160405190810160405281815292919060208401838380828437600092019190915250929550610d40945050505050565b61015b6004803603606081101561055e57600080fd5b5063ffffffff8135169067ffffffffffffffff6020820135811691604001351661123f565b6105a66004803603602081101561059957600080fd5b503563ffffffff1661137b565b60405167ffffffffffffffff83166020820152600160a060020a038216604082015260608082528190810185818151815260200191508051906020019080838360005b838110156106015780820151838201526020016105e9565b50505050905090810190601f16801561062e5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b6102c56113cc565b61064e6113e1565b604051600160a060020a03909116815260200160405180910390f35b61015b6004803603604081101561068057600080fd5b5063ffffffff81351690602001356113e7565b6106cd600480360360608110156106a957600080fd5b50600160a060020a038135169063ffffffff602082013581169160400135166114be565b6040516007998a0b8a0b815297890b890b602089015295880b880b60408089019190915294880b880b606088015267ffffffffffffffff938416608088015291870b870b60a0870152860b90950b60c085015293841660e0840152921661010082015261012001905180910390f35b6107766004803603606081101561075257600080fd5b50600160a060020a038135169063ffffffff602082013581169160400135166115d0565b60405167ffffffffffffffff9283168152911660208201526040908101905180910390f35b61015b600480360360208110156107b157600080fd5b8101906020810181356401000000008111156107cc57600080fd5b8201836020820111156107de57600080fd5b8035906020019184600183028401116401000000008311171561080057600080fd5b91908080601f0160208091040260200160405190810160405281815292919060208401838380828437600092019190915250929550611632945050505050565b61015b6004803603606081101561085657600080fd5b5063ffffffff81351690600160a060020a036020820135169060400135611bfb565b61015b6004803603606081101561088e57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16611ccc565b61015b600480360360608110156108cc57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16611e0c565b60005433811461090357600080fd5b50600055565b610911611f5d565b60005433811461092757600182526001601f8301fd5b84516008811461093d57600283526001601f8401fd5b60025480861061095357600384526001601f8501fd5b600154640100000000811061096e57600485526001601f8601fd5b8060020260030160208901518760a060020a8a02178117808355600184016001555050505050505050505050565b6109a4611f5d565b6109ac611f79565b6109b4611f5d565b6109bc611f95565b6002548087106109d257600185526001601f8601fd5b508415156109e657600284526001601f8501fd5b85640200000003015467ffffffffffffffff60a060020a8204168602600160a060020a0382167f23b872dd0000000000000000000000000000000000000000000000000000000086523360048701523060248701528160448701526020856064886000855af1801515610a5f57600388526001601f8901fd5b8551801515610a7457600489526001601f8a01fd5b5050896403000000000233680300000000000000000264030000000360c060020a010101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff83160167ffffffffffffffff811115610aeb5760058c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b600080600080610b63611f79565b8560020260030163ffffffff60a060020a82540416876403000000000289680300000000000000000264030000000360c060020a0101018160030281018154600183015467ffffffffffffffff60c060020a820416875277ffffffffffffffffffffffffffffffffffffffffffffffff8116602088015267ffffffffffffffff60c060020a830416604088015267ffffffffffffffff608060020a8304166060880152608087f35b6000610c15611f5d565b600154808252602082f35b60606000806000610c2f611fb1565b85600202600301805460808352600860808401528060a084015263ffffffff60a060020a8204166020840152600160a060020a03811660408401526001820154905080606084015260a883f35b610c84611f5d565b600054338114610c9a57600182526001601f8301fd5b6002546401000000008110610cb557600283526001601f8401fd5b855160048114610ccb57600384526001601f8501fd5b851515610cde57600484526001601f8501fd5b841515610cf157600584526001601f8501fd5b602087015160a060020a969096029094179490941764020000000385015550505060010160025550565b6000610d25611f5d565b83640100000000026403000000030183810180548352602083f35b610d48611f5d565b610d50611fcd565b600083516020850160958214610d6c57600185526001601f8601fd5b8051601482019150600160a060020a036c0100000000000000000000000082041693507f54f117d451d3a82d44e7d7965ecb9427e0092827103aac90b3ffffcc9ed9189585528151905060208201915060008063ffffffff60e060020a84041680602089015263ffffffff60c060020a8504168060408a015267ffffffffffffffff608060020a86041692508260608a0152816403000000000288680300000000000000000264030000000360c060020a01010181600302810194508260020260030154600160a060020a0381168033141515610e4f5760028d526001601f8e01fd5b50506002850154608060020a900467ffffffffffffffff16808511610e7a5760038c526001601f8d01fd5b5050505067ffffffffffffffff6801000000000000000084041680608089015267ffffffffffffffff84168060a08a015280680100000000000000008302608060020a85021717600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a81029150678000000000000000811615610f0b5767ffffffffffffffff19175b8060c08901525067ffffffffffffffff608060020a840416608060020a810282179150678000000000000000811615610f4a5767ffffffffffffffff19175b8060e08901525067ffffffffffffffff6801000000000000000084041668010000000000000000810282179150678000000000000000811615610f935767ffffffffffffffff19175b8061010089015267ffffffffffffffff84168083179250678000000000000000811615610fc65767ffffffffffffffff19175b806101208a0152826001850155600184015467ffffffffffffffff6801000000000000000082041667800000000000000081161561100a5767ffffffffffffffff19175b9092039167ffffffffffffffff81166780000000000000008216156110355767ffffffffffffffff19175b80830392505050835467ffffffffffffffff60c060020a820416830167ffffffffffffffff608060020a8304168301677fffffffffffffff8113677fffffffffffffff19821217677fffffffffffffff8313677fffffffffffffff1984121717156110a65760048d526001601f8e01fd5b608060020a67ffffffffffffffff82160260c060020a67ffffffffffffffff841602176fffffffffffffffffffffffffffffffff84161787556101408c207f19010000000000000000000000000000000000000000000000000000000000008d527f044c7dbaf083d34033441e730195278b6b4e3ea03f9bb3fb208a721e7c15b72c60028e015260228d0181905260428d209050808d528951985060208a0199508860208e01528951985060208a0199508860408e015289517f0100000000000000000000000000000000000000000000000000000000000000900460ff1660608e01525060009950600198508b97508996506111a595505050505050565b602002015160608501516020860151604087015160405160008152602001604052604051808581526020018460ff1660ff1681526020018381526020018281526020019450505050506020604051602081039080840390855afa158015611210573d6000803e3d6000fd5b50505060206040510351600160a060020a0316905081811461123857600584526001601f8501fd5b5050505050565b611247611f5d565b61124f611f95565b62278d004201841161a8c042018510171561127057600182526001601f8301fd5b60015480861061128657600283526001601f8401fd5b508460020260030163ffffffff60a060020a82540416866403000000000233680300000000000000000264030000000360c060020a010101816003028101805467ffffffffffffffff60c060020a820416808910156112eb57600388526001601f8901fd5b808911156113195760c060020a890277ffffffffffffffffffffffffffffffffffffffffffffffff83161783555b67ffffffffffffffff60c060020a600185015404168a60c060020a60018301021760018501553388528b60208901527f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604089a1505050505050505050505050565b6060600080611388611feb565b846402000000030154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0381166040830152608482f35b60006113d6611f5d565b600254808252602082f35b60005490565b6113ef611f5d565b6113f7611f79565b6113ff611f5d565b83151561140857005b60025480861061141e57600184526001601f8501fd5b507f23b872dd000000000000000000000000000000000000000000000000000000008252336004830152306024830152836044830152846402000000030154600160a060020a0381166020836064866000855af180151561148557600286526001601f8701fd5b835180151561149a57600387526001601f8801fd5b50505050505064010000000033029290920164030000000301805491909101905550565b60008060008060008060008060006114d4612007565b8b640300000000028d680300000000000000000264030000000360c060020a0101018b600302810180546001820154600283015467ffffffffffffffff60c060020a840416865267ffffffffffffffff608060020a840416602087015267ffffffffffffffff68010000000000000000830416604087015267ffffffffffffffff8216606087015267ffffffffffffffff608060020a820416608087015267ffffffffffffffff60c060020a83041660a087015267ffffffffffffffff608060020a83041660c087015267ffffffffffffffff6801000000000000000082041660e087015267ffffffffffffffff811661010087015261012086f35b6000806115db612025565b846403000000000286680300000000000000000264030000000360c060020a010101846003028101805467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff81166020850152604084f35b61163a611feb565b60208201825180820160a0590352600481101561166357600060205903526001601f6020590301fd5b815160048301925063ffffffff60e060020a820416600154808210151561169657600160205903526001601f6020590301fd5b60036002830201805433600160a060020a038216146116c157600260205903526001601f6020590301fd5b63ffffffff60a060020a82041660c059035260018201546080590352836060590352505050505b60058360a05903510310611be3578251905060058301925060ff7b01000000000000000000000000000000000000000000000000000000820416602c810280850160a059035181111561174757600360205903526001601f6020590301fd5b80604059035250505063ffffffff60e060020a8204166000805b6040590351861015611bc15785519350601486019550606059035164030000000002600160a060020a036c01000000000000000000000000860416680300000000000000000264030000000360c060020a0101018651945060188701965067ffffffffffffffff60c060020a86041667ffffffffffffffff608060020a87041667ffffffffffffffff680100000000000000008804166780000000000000008316156118165767ffffffffffffffff19831792505b6780000000000000008216156118355767ffffffffffffffff19821791505b948201949381019360c05903516003028401876003028501600182015477ffffffffffffffffffffffffffffffffffffffffffffffff81168042111561188757600460205903526001601f6020590301fd5b5050815467ffffffffffffffff8082168701859003908111156118b657600560205903526001601f6020590301fd5b608060020a820467ffffffffffffffff1685016080590386815101815267ffffffffffffffff60c060020a850416808311156118fe57600660205903526001601f6020590301fd5b83608060020a84021777ffffffffffffffff0000000000000000ffffffffffffffff19861617875550505050506000808a6003028801805467ffffffffffffffff8116888101905067ffffffffffffffff81111561196857600760205903526001601f6020590301fd5b67ffffffffffffffff60c060020a830416945067ffffffffffffffff608060020a83041693506780000000000000008516156119ad5767ffffffffffffffff19851794505b6780000000000000008416156119cc5767ffffffffffffffff19841793505b89850194508884019350677fffffffffffffff8413677fffffffffffffff19851217677fffffffffffffff8613677fffffffffffffff198712171715611a1e57600860205903526001601f6020590301fd5b80608060020a850260c060020a870217176fffffffffffffffff00000000000000008316178355505050600183015467ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a830416678000000000000000821615611a8e5767ffffffffffffffff19821791505b678000000000000000811615611aaa5767ffffffffffffffff19175b8084128286121715611ac857600960205903526001601f6020590301fd5b50505060028301546002600083120260008412018060038114611afa5760018114611b0c5760028114611b6657611bb1565b600a60205903526001601f6020590301fd5b831515611b2557600b60205903526001601f6020590301fd5b836305f5e10086600003020467ffffffffffffffff68010000000000000000850416811115611b6057600c60205903526001601f6020590301fd5b50611bb1565b841515611b7f57600d60205903526001601f6020590301fd5b836000036305f5e10086020467ffffffffffffffff8416811015611baf57600e60205903526001601f6020590301fd5b505b5050505050505050505050611761565b80821715611bdb57600f60205903526001601f6020590301fd5b5050506116e8565b60805903516060590351600202600401555050505050565b611c03611f5d565b611c0b611f95565b611c13611f5d565b831515611c1c57005b336401000000000264030000000301868101805486811015611c4457600186526001601f8701fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008552876004860152866024860152886402000000030154600160a060020a0381166020866044896000855af1801515611ca457600289526001601f8a01fd5b8651801515611cb95760038a526001601f8b01fd5b5050505095909503909455505050505050565b611cd4611f5d565b611cdc611f79565b336401000000000264030000000301848101805486640200000003015467ffffffffffffffff60a060020a82041680880283811115611d2157600188526001601f8901fd5b8084039350838555505050505050846403000000000233680300000000000000000264030000000360c060020a01010184600302810180548567ffffffffffffffff68010000000000000000830416018667ffffffffffffffff83160167ffffffffffffffff8216915067ffffffffffffffff811115611da757600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553386528960208701528860408701527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606087a150505050505050505050565b611e14611f5d565b611e1c611f79565b846002026003015463ffffffff60a060020a820416866403000000000233680300000000000000000264030000000360c060020a01010186600302810177ffffffffffffffffffffffffffffffffffffffffffffffff60018201541680421015611e8c57600187526001601f8801fd5b5050866003028101805467ffffffffffffffff811680891115611eb557600288526001601f8901fd5b88810390508067ffffffffffffffff198316178355505050336401000000000264030000000301878101805489640200000003015467ffffffffffffffff60a060020a820416808b02808401935080841015611f175760038b526001601f8c01fd5b50505090555033845260208401889052604084018790527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606085a15050505050505050565b6020604051908101604052600181602080388339509192915050565b6080604051908101604052600481608080388339509192915050565b6060604051908101604052600381606080388339509192915050565b60c060405190810160405260068160c080388339509192915050565b610140604051908101604052600a8161014080388339509192915050565b60a060405190810160405260058160a080388339509192915050565b61010060405190810160405260088161010080388339509192915050565b6040805190810160405280600290602082028038833950919291505056fea165627a7a72305820d4aff6763e8fe6379c127a9623182da2395cbfe1a833277b867d20d37df4a8e40029";
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetSessionReturnValue returnValue = new GetSessionReturnValue();
        returnValue.version = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.unlock_at = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.fee_limit = ((BigInteger) values.get(2).getValue()).longValue();
        returnValue.fee_used = ((BigInteger) values.get(3).getValue()).longValue();
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetExchangeCountReturnValue returnValue = new GetExchangeCountReturnValue();
        returnValue.count = ((BigInteger) values.get(0).getValue()).intValue();
        return returnValue;
    }
    public static Function get_exchange(int exchange_id) {
        return new Function(
            "get_exchange",
            Collections.singletonList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
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
    public static Function update_session(int exchange_id, long unlock_at, long fee_limit) {
        return new Function(
            "update_session",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint64(unlock_at)
                , new org.web3j.abi.datatypes.generated.Uint64(fee_limit)
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
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
    public static Function get_session_state(String user, int exchange_id, int asset_id) {
        return new Function(
            "get_session_state",
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
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Int64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
                , new TypeReference<org.web3j.abi.datatypes.generated.Uint64>() {}
            )
        );
    }
    public static GetSessionStateReturnValue query_get_session_state(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        GetSessionStateReturnValue returnValue = new GetSessionStateReturnValue();
        returnValue.quote_qty = ((BigInteger) values.get(0).getValue()).longValue();
        returnValue.base_qty = ((BigInteger) values.get(1).getValue()).longValue();
        returnValue.quote_shift = ((BigInteger) values.get(2).getValue()).longValue();
        returnValue.base_shift = ((BigInteger) values.get(3).getValue()).longValue();
        returnValue.version = ((BigInteger) values.get(4).getValue()).longValue();
        returnValue.min_quote = ((BigInteger) values.get(5).getValue()).longValue();
        returnValue.min_base = ((BigInteger) values.get(6).getValue()).longValue();
        returnValue.long_max_price = ((BigInteger) values.get(7).getValue()).longValue();
        returnValue.short_min_price = ((BigInteger) values.get(8).getValue()).longValue();
        return returnValue;
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
            Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, encodedFunction),
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
    public static Function transfer_from_session(int exchange_id, int asset_id, long quantity) {
        return new Function(
            "transfer_from_session",
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
        public long unlock_at;
        public long fee_limit;
        public long fee_used;
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
    public static class GetSessionStateReturnValue {
        public long quote_qty;
        public long base_qty;
        public long quote_shift;
        public long base_shift;
        public long version;
        public long min_quote;
        public long min_base;
        public long long_max_price;
        public long short_min_price;
    }
    public static class GetSessionBalanceReturnValue {
        public long total_deposit;
        public long asset_balance;
    }
}
