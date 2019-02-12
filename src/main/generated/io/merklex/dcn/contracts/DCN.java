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
    public static final String BINARY = "608060405234801561001057600080fd5b5033600055612428806100246000396000f3fe608060405234801561001057600080fd5b506004361061015c5760003560e060020a90048063a88d1902116100c7578063cf58894511610080578063cf5889451461080a578063e455654914610812578063e7172f1714610871578063f894d39814610916578063fb6b38571461094e578063fc429e6a1461098c5761015c565b8063a88d1902146106b7578063ace1ed07146106bf578063adb6c75b146106e3578063b71a6dd6146106eb578063be6ae33114610714578063c03284ba146107bd5761015c565b80637c734736116101195780637c734736146103ce578063831b55d61461048b57806383daf06d146104cf5780638e98cee1146105745780639e51d967146105af578063a68c68b4146105fc5761015c565b80630faf09041461016157806311a86b151461018957806314f604b81461024257806337f265e614610280578063431ec601146102e9578063541694cf1461030a575b600080fd5b6101876004803603602081101561017757600080fd5b5035600160a060020a03166109ca565b005b6101876004803603606081101561019f57600080fd5b8101906020810181356401000000008111156101ba57600080fd5b8201836020820111156101cc57600080fd5b803590602001918460018302840111640100000000831117156101ee57600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813563ffffffff1692505060200135600160a060020a03166109df565b6101876004803603606081101561025857600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16610a72565b6102b26004803603604081101561029657600080fd5b508035600160a060020a0316906020013563ffffffff16610c2b565b60405167ffffffffffffffff9485168152928416602084015290831660408084019190915292166060820152608001905180910390f35b6102f1610ce1565b60405163ffffffff909116815260200160405180910390f35b61032d6004803603602081101561032057600080fd5b503563ffffffff16610cf6565b60405167ffffffffffffffff8085166020830152600160a060020a03841660408301528216606082015260808082528190810186818151815260200191508051906020019080838360005b83811015610390578082015183820152602001610378565b50505050905090810190601f1680156103bd5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b610187600480360360608110156103e457600080fd5b8101906020810181356401000000008111156103ff57600080fd5b82018360208201111561041157600080fd5b8035906020019184600183028401116401000000008311171561043357600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813567ffffffffffffffff1692505060200135600160a060020a0316610d52565b6104bd600480360360408110156104a157600080fd5b508035600160a060020a0316906020013563ffffffff16610df1565b60405190815260200160405180910390f35b610187600480360360208110156104e557600080fd5b81019060208101813564010000000081111561050057600080fd5b82018360208201111561051257600080fd5b8035906020019184600183028401116401000000008311171561053457600080fd5b91908080601f0160208091040260200160405190810160405281815292919060208401838380828437600092019190915250929550610e16945050505050565b6101876004803603606081101561058a57600080fd5b5063ffffffff8135169067ffffffffffffffff6020820135811691604001351661134f565b610187600480360360808110156105c557600080fd5b50803563ffffffff908116916020810135909116906040810135600160a060020a0316906060013567ffffffffffffffff1661148b565b61061f6004803603602081101561061257600080fd5b503563ffffffff166115cb565b60405167ffffffffffffffff83166020820152600160a060020a038216604082015260608082528190810185818151815260200191508051906020019080838360005b8381101561067a578082015183820152602001610662565b50505050905090810190601f1680156106a75780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b6102f161161c565b6106c7611631565b604051600160a060020a03909116815260200160405180910390f35b610187611637565b6101876004803603604081101561070157600080fd5b5063ffffffff813516906020013561165f565b61074e6004803603606081101561072a57600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516611736565b6040516007998a0b8a0b815297890b890b602089015295880b880b60408089019190915294880b880b606088015267ffffffffffffffff938416608088015291870b870b60a0870152860b90950b60c085015293841660e0840152921661010082015261012001905180910390f35b610187600480360360808110156107d357600080fd5b50803563ffffffff908116916020810135909116906040810135600160a060020a0316906060013567ffffffffffffffff16611848565b610187611917565b61084c6004803603606081101561082857600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516611961565b60405167ffffffffffffffff9283168152911660208201526040908101905180910390f35b6101876004803603602081101561088757600080fd5b8101906020810181356401000000008111156108a257600080fd5b8201836020820111156108b457600080fd5b803590602001918460018302840111640100000000831117156108d657600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295506119c3945050505050565b6101876004803603606081101561092c57600080fd5b5063ffffffff81351690600160a060020a036020820135169060400135611fb4565b6101876004803603606081101561096457600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16612085565b610187600480360360608110156109a257600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff166121c5565b6000543381146109d957600080fd5b50600055565b6109e7612316565b6000543381146109fd57600182526001601f8301fd5b845160088114610a1357600283526001601f8401fd5b600254808610610a2957600384526001601f8501fd5b6001546401000000008110610a4457600485526001601f8601fd5b8060020260040160208901518760a060020a8a02178117808355600184016001555050505050505050505050565b610a7a612316565b610a82612332565b610a8a612316565b610a9261234e565b600254808710610aa857600185526001601f8601fd5b50841515610abc57600284526001601f8501fd5b85640200000004015467ffffffffffffffff60a060020a8204168602600160a060020a0382167f23b872dd0000000000000000000000000000000000000000000000000000000086523360048701523060248701528160448701526020856064886000855af1801515610b3557600388526001601f8901fd5b8551801515610b4a57600489526001601f8a01fd5b5050896403000000000233680300000000000000000264030000000460c060020a010101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff83160167ffffffffffffffff811115610bc15760058c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b600080600080610c39612332565b8560020260040163ffffffff60a060020a82540416876403000000000289680300000000000000000264030000000460c060020a0101018160030281018054600182015467ffffffffffffffff60c060020a820416875277ffffffffffffffffffffffffffffffffffffffffffffffff8116602088015267ffffffffffffffff60c060020a830416604088015267ffffffffffffffff608060020a8304166060880152608087f35b6000610ceb612316565b600154808252602082f35b60606000806000610d0561236a565b85600202600401805460808352600860808401528060a084015263ffffffff60a060020a8204166020840152600160a060020a03811660408401526001820154905080606084015260a883f35b610d5a612316565b600054338114610d7057600182526001601f8301fd5b6002546401000000008110610d8b57600283526001601f8401fd5b855160048114610da157600384526001601f8501fd5b851515610db457600484526001601f8501fd5b841515610dc757600584526001601f8501fd5b602087015160a060020a969096029094179490941764020000000485015550505060010160025550565b6000610dfb612316565b83640100000000026403000000040183810180548352602083f35b610e1e612316565b610e26612386565b6000806000855160208701915080820192506095810615610e4d57600186526001601f8701fd5b505b81811415610e5957005b8051601482019150600160a060020a036c0100000000000000000000000082041693507fe0bfc2789e007df269c9fec46d3ddd4acf88fdf0f76af154da933aab7fb2f2b985528151905060208201915060008063ffffffff60e060020a84041680602089015263ffffffff60c060020a8504168060408a015267ffffffffffffffff608060020a86041692508260608a0152816403000000000288680300000000000000000264030000000460c060020a01010181600302810194508260020260040154600160a060020a0381168033141515610f3c5760028d526001601f8e01fd5b63ffffffff60a060020a83041684811415610f5d5760068e526001601f8f01fd5b5050506002850154608060020a900467ffffffffffffffff16808511610f895760038c526001601f8d01fd5b5050505067ffffffffffffffff6801000000000000000084041680608089015267ffffffffffffffff84168060a08a015280680100000000000000008302608060020a85021717600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a8102915067800000000000000081161561101a5767ffffffffffffffff19175b8060c08901525067ffffffffffffffff608060020a840416608060020a8102821791506780000000000000008116156110595767ffffffffffffffff19175b8060e08901525067ffffffffffffffff68010000000000000000840416680100000000000000008102821791506780000000000000008116156110a25767ffffffffffffffff19175b8061010089015267ffffffffffffffff841680831792506780000000000000008116156110d55767ffffffffffffffff19175b806101208a0152826001850155600184015467ffffffffffffffff680100000000000000008204166780000000000000008116156111195767ffffffffffffffff19175b9092039167ffffffffffffffff81166780000000000000008216156111445767ffffffffffffffff19175b80830392505050835467ffffffffffffffff60c060020a820416830167ffffffffffffffff608060020a8304168301677fffffffffffffff8113677fffffffffffffff19821217677fffffffffffffff8313677fffffffffffffff1984121717156111b55760048d526001601f8e01fd5b608060020a67ffffffffffffffff82160260c060020a67ffffffffffffffff841602176fffffffffffffffffffffffffffffffff84161787556101408c207f19010000000000000000000000000000000000000000000000000000000000008d527fe3d3073cc59e3a3126c17585a7e516a048e61a9a1c82144af982d1c194b1871060028e015260228d0181905260428d209050808d528951985060208a0199508860208e01528951985060208a0199508860408e015289517f0100000000000000000000000000000000000000000000000000000000000000900460ff1660608e01525050506001968701966000965094508993508592506112b6915050565b602002015160608701516020880151604089015160405160008152602001604052604051808581526020018460ff1660ff1681526020018381526020018281526020019450505050506020604051602081039080840390855afa158015611321573d6000803e3d6000fd5b50505060206040510351600160a060020a0316905083811461134957600586526001601f8701fd5b50610e4f565b611357612316565b61135f61234e565b62278d004201841161a8c042018510171561138057600182526001601f8301fd5b60015480861061139657600283526001601f8401fd5b508460020260040163ffffffff60a060020a82540416866403000000000233680300000000000000000264030000000460c060020a010101816003028101805467ffffffffffffffff60c060020a820416808910156113fb57600388526001601f8901fd5b808911156114295760c060020a890277ffffffffffffffffffffffffffffffffffffffffffffffff83161783555b67ffffffffffffffff60c060020a600185015404168a60c060020a60018301021760018501553388528b60208901527f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604089a1505050505050505050505050565b611493612316565b61149b61234e565b6114a3612316565b8315156114ac57005b600460028802015433600160a060020a038216146114d057600184526001601f8501fd5b876403000000000286680300000000000000000264030000000460c060020a010101876003028101805467ffffffffffffffff81168089111561151957600288526001601f8901fd5b88810390508067ffffffffffffffff1983161783558a640200000004015467ffffffffffffffff60a060020a820416600160a060020a038216818c027fa9059cbb000000000000000000000000000000000000000000000000000000008b528d60048c01528060248c015260208a60448d6000865af18015156115a25760028d526001601f8e01fd5b8a518015156115b75760038e526001601f8f01fd5b505050505050505050505050505050505050565b60606000806115d86123a4565b846402000000040154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0381166040830152608482f35b6000611626612316565b600254808252602082f35b60005490565b61163f612316565b60005433811461165557600182526001601f8301fd5b6000196003555050565b611667612316565b61166f612332565b611677612316565b83151561168057005b60025480861061169657600184526001601f8501fd5b507f23b872dd000000000000000000000000000000000000000000000000000000008252336004830152306024830152836044830152846402000000040154600160a060020a0381166020836064866000855af18015156116fd57600286526001601f8701fd5b835180151561171257600387526001601f8801fd5b50505050505064010000000033029290920164030000000401805491909101905550565b600080600080600080600080600061174c6123c0565b8b640300000000028d680300000000000000000264030000000460c060020a0101018b600302810180546001820154600283015467ffffffffffffffff60c060020a840416865267ffffffffffffffff608060020a840416602087015267ffffffffffffffff68010000000000000000830416604087015267ffffffffffffffff8216606087015267ffffffffffffffff608060020a820416608087015267ffffffffffffffff60c060020a83041660a087015267ffffffffffffffff608060020a83041660c087015267ffffffffffffffff6801000000000000000082041660e087015267ffffffffffffffff811661010087015261012086f35b611850612316565b81151561185957005b600460028602015433600160a060020a0382161461187d57600182526001601f8301fd5b856403000000000284680300000000000000000264030000000460c060020a010101856003028101805467ffffffffffffffff8116808711156118c657600286526001601f8701fd5b67ffffffffffffffff19919091169086900317905550505064020000000483015464010000000090920290920164030000000401805460a060020a90920467ffffffffffffffff1690920201905550565b61191f612316565b60005433811461193557600182526001601f8301fd5b60035442811115611957576206978042018181111561195057005b6003819055005b6000600355505050565b60008061196c6123de565b846403000000000286680300000000000000000264030000000460c060020a010101846003028101805467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff81166020850152604084f35b6119cb61236a565b600354156119e557606460205903526001601f6020590301fd5b60208201825180820160a05903526004811015611a0e57600060205903526001601f6020590301fd5b815160048301925063ffffffff60e060020a8204166001548082101515611a4157600160205903526001601f6020590301fd5b60046002830201805433600160a060020a03821614611a6c57600260205903526001601f6020590301fd5b63ffffffff60a060020a82041660c059035260018201546080590352836060590352505050505b60058360a05903510310611f9c578251905060058301925060ff7b01000000000000000000000000000000000000000000000000000000820416602c810280850160a0590351811115611af257600360205903526001601f6020590301fd5b80604059035250505063ffffffff60e060020a8204166000805b6040590351861015611f7a5785519350601486019550606059035164030000000002600160a060020a036c01000000000000000000000000860416680300000000000000000264030000000460c060020a0101018651945060188701965067ffffffffffffffff60c060020a86041667ffffffffffffffff608060020a87041667ffffffffffffffff68010000000000000000880416678000000000000000831615611bc15767ffffffffffffffff19831792505b678000000000000000821615611be05767ffffffffffffffff19821791505b948201949381019360c05903516003028401876003028501600182015477ffffffffffffffffffffffffffffffffffffffffffffffff811680421115611c3257600460205903526001601f6020590301fd5b5050815467ffffffffffffffff808216870185900390811115611c6157600560205903526001601f6020590301fd5b608060020a820467ffffffffffffffff1685016080590386815101815267ffffffffffffffff60c060020a85041680831115611ca957600660205903526001601f6020590301fd5b83608060020a84021777ffffffffffffffff0000000000000000ffffffffffffffff19861617875550505050506000808a6003028801805467ffffffffffffffff8116888101905067ffffffffffffffff811115611d1357600760205903526001601f6020590301fd5b67ffffffffffffffff60c060020a830416945067ffffffffffffffff608060020a8304169350678000000000000000851615611d585767ffffffffffffffff19851794505b678000000000000000841615611d775767ffffffffffffffff19841793505b89850194508884019350677fffffffffffffff8413677fffffffffffffff19851217677fffffffffffffff8613677fffffffffffffff198712171715611dc957600860205903526001601f6020590301fd5b80608060020a850260c060020a870217176fffffffffffffffff00000000000000008316178355505050600183015467ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a830416678000000000000000821615611e395767ffffffffffffffff19821791505b678000000000000000811615611e555767ffffffffffffffff19175b8084128286121715611e7357600960205903526001601f6020590301fd5b50505060028301546002600183120260018412018060038114611ea55760018114611ec55760028114611f1f57611f6a565b8385171515611ec057600a60205903526001601f6020590301fd5b611f6a565b831515611ede57600b60205903526001601f6020590301fd5b836305f5e10086600003020467ffffffffffffffff68010000000000000000850416811115611f1957600c60205903526001601f6020590301fd5b50611f6a565b841515611f3857600d60205903526001601f6020590301fd5b836000036305f5e10086020467ffffffffffffffff8416811015611f6857600e60205903526001601f6020590301fd5b505b5050505050505050505050611b0c565b80821715611f9457600f60205903526001601f6020590301fd5b505050611a93565b60805903516060590351600202600501555050505050565b611fbc612316565b611fc461234e565b611fcc612316565b831515611fd557005b336401000000000264030000000401868101805486811015611ffd57600186526001601f8701fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008552876004860152866024860152886402000000040154600160a060020a0381166020866044896000855af180151561205d57600289526001601f8a01fd5b86518015156120725760038a526001601f8b01fd5b5050505095909503909455505050505050565b61208d612316565b612095612332565b336401000000000264030000000401848101805486640200000004015467ffffffffffffffff60a060020a820416808802838111156120da57600188526001601f8901fd5b8084039350838555505050505050846403000000000233680300000000000000000264030000000460c060020a01010184600302810180548567ffffffffffffffff68010000000000000000830416018667ffffffffffffffff83160167ffffffffffffffff8216915067ffffffffffffffff81111561216057600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553386528960208701528860408701527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606087a150505050505050505050565b6121cd612316565b6121d5612332565b846002026004015463ffffffff60a060020a820416866403000000000233680300000000000000000264030000000460c060020a01010181600302810177ffffffffffffffffffffffffffffffffffffffffffffffff6001820154168042101561224557600187526001601f8801fd5b5050866003028101805467ffffffffffffffff81168089111561226e57600288526001601f8901fd5b88810390508067ffffffffffffffff198316178355505050336401000000000264030000000401878101805489640200000004015467ffffffffffffffff60a060020a820416808b028084019350808410156122d05760038b526001601f8c01fd5b50505090555033845260208401889052604084018790527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606085a15050505050505050565b6020604051908101604052600181602080388339509192915050565b6080604051908101604052600481608080388339509192915050565b6060604051908101604052600381606080388339509192915050565b60c060405190810160405260068160c080388339509192915050565b610140604051908101604052600a8161014080388339509192915050565b60a060405190810160405260058160a080388339509192915050565b61010060405190810160405260088161010080388339509192915050565b6040805190810160405280600290602082028038833950919291505056fea165627a7a7230582009bc9ee5e9785cb60d424ad1403ae5500cd21c833db04ab34495c51202ead1290029";
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
    public static Function withdraw_from_session_to_account(int exchange_id, int asset_id, String user, long amount) {
        return new Function(
            "withdraw_from_session_to_account",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
                , new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint64(amount)
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
    public static Function security_lock() {
        return new Function(
            "security_lock",
            Collections.emptyList(),
            Collections.emptyList()
        );
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
    public static Function withdraw_from_session(int exchange_id, int asset_id, String user, long amount) {
        return new Function(
            "withdraw_from_session",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.generated.Uint32(asset_id)
                , new org.web3j.abi.datatypes.Address(user)
                , new org.web3j.abi.datatypes.generated.Uint64(amount)
            ),
            Collections.emptyList()
        );
    }
    public static Function security_unlock() {
        return new Function(
            "security_unlock",
            Collections.emptyList(),
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
