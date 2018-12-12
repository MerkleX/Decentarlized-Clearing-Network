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
    public static final String BINARY = "608060405234801561001057600080fd5b503360005561210b806100246000396000f3fe6080604052600436106100f25760e060020a60003504630faf090481146100f757806311a86b151461012c57806314f604b8146101f257806337f265e61461023d578063431ec601146102b3578063541694cf146102e15780637c734736146103b2578063831b55d61461047c57806383daf06d146104cd5780638e98cee11461057f578063a68c68b4146105c7578063a88d19021461068f578063ace1ed07146106a4578063b71a6dd6146106d5578063be6ae3311461070b578063e4556549146107c1578063e7172f171461082d578063f894d398146108df578063fb6b385714610924578063fc429e6a1461096f575b600080fd5b34801561010357600080fd5b5061012a6004803603602081101561011a57600080fd5b5035600160a060020a03166109ba565b005b34801561013857600080fd5b5061012a6004803603606081101561014f57600080fd5b81019060208101813564010000000081111561016a57600080fd5b82018360208201111561017c57600080fd5b8035906020019184600183028401116401000000008311171561019e57600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813563ffffffff1692505060200135600160a060020a03166109cf565b3480156101fe57600080fd5b5061012a6004803603606081101561021557600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16610a62565b34801561024957600080fd5b5061027c6004803603604081101561026057600080fd5b508035600160a060020a0316906020013563ffffffff16610c1b565b60405167ffffffffffffffff9485168152928416602084015290831660408084019190915292166060820152608001905180910390f35b3480156102bf57600080fd5b506102c8610cd1565b60405163ffffffff909116815260200160405180910390f35b3480156102ed57600080fd5b506103116004803603602081101561030457600080fd5b503563ffffffff16610ce6565b60405167ffffffffffffffff8085166020830152600160a060020a03841660408301528216606082015260808082528190810186818151815260200191508051906020019080838360005b8381101561037457808201518382015260200161035c565b50505050905090810190601f1680156103a15780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b3480156103be57600080fd5b5061012a600480360360608110156103d557600080fd5b8101906020810181356401000000008111156103f057600080fd5b82018360208201111561040257600080fd5b8035906020019184600183028401116401000000008311171561042457600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813567ffffffffffffffff1692505060200135600160a060020a0316610d42565b34801561048857600080fd5b506104bb6004803603604081101561049f57600080fd5b508035600160a060020a0316906020013563ffffffff16610de1565b60405190815260200160405180910390f35b3480156104d957600080fd5b5061012a600480360360208110156104f057600080fd5b81019060208101813564010000000081111561050b57600080fd5b82018360208201111561051d57600080fd5b8035906020019184600183028401116401000000008311171561053f57600080fd5b91908080601f0160208091040260200160405190810160405281815292919060208401838380828437600092019190915250929550610e06945050505050565b34801561058b57600080fd5b5061012a600480360360608110156105a257600080fd5b5063ffffffff8135169067ffffffffffffffff60208201358116916040013516611305565b3480156105d357600080fd5b506105f7600480360360208110156105ea57600080fd5b503563ffffffff16611441565b60405167ffffffffffffffff83166020820152600160a060020a038216604082015260608082528190810185818151815260200191508051906020019080838360005b8381101561065257808201518382015260200161063a565b50505050905090810190601f16801561067f5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561069b57600080fd5b506102c8611492565b3480156106b057600080fd5b506106b96114a7565b604051600160a060020a03909116815260200160405180910390f35b3480156106e157600080fd5b5061012a600480360360408110156106f857600080fd5b5063ffffffff81351690602001356114ad565b34801561071757600080fd5b506107526004803603606081101561072e57600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516611584565b6040516007998a0b8a0b815297890b890b602089015295880b880b60408089019190915294880b880b606088015267ffffffffffffffff938416608088015291870b870b60a0870152860b90950b60c085015293841660e0840152921661010082015261012001905180910390f35b3480156107cd57600080fd5b50610808600480360360608110156107e457600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516611696565b60405167ffffffffffffffff9283168152911660208201526040908101905180910390f35b34801561083957600080fd5b5061012a6004803603602081101561085057600080fd5b81019060208101813564010000000081111561086b57600080fd5b82018360208201111561087d57600080fd5b8035906020019184600183028401116401000000008311171561089f57600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295506116f8945050505050565b3480156108eb57600080fd5b5061012a6004803603606081101561090257600080fd5b5063ffffffff81351690600160a060020a036020820135169060400135611c97565b34801561093057600080fd5b5061012a6004803603606081101561094757600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16611d68565b34801561097b57600080fd5b5061012a6004803603606081101561099257600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16611ea8565b6000543381146109c957600080fd5b50600055565b6109d7611ff9565b6000543381146109ed57600182526001601f8301fd5b845160088114610a0357600283526001601f8401fd5b600254808610610a1957600384526001601f8501fd5b6001546401000000008110610a3457600485526001601f8601fd5b8060020260030160208901518760a060020a8a02178117808355600184016001555050505050505050505050565b610a6a611ff9565b610a72612015565b610a7a611ff9565b610a82612031565b600254808710610a9857600185526001601f8601fd5b50841515610aac57600284526001601f8501fd5b85640200000003015467ffffffffffffffff60a060020a8204168602600160a060020a0382167f23b872dd0000000000000000000000000000000000000000000000000000000086523360048701523060248701528160448701526020856064886000855af1801515610b2557600388526001601f8901fd5b8551801515610b3a57600489526001601f8a01fd5b5050896403000000000233680300000000000000000264030000000360c060020a010101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff83160167ffffffffffffffff811115610bb15760058c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b600080600080610c29612015565b8560020260030163ffffffff60a060020a82540416876403000000000289680300000000000000000264030000000360c060020a0101018160030281018154600183015467ffffffffffffffff60c060020a820416875277ffffffffffffffffffffffffffffffffffffffffffffffff8116602088015267ffffffffffffffff60c060020a830416604088015267ffffffffffffffff608060020a8304166060880152608087f35b6000610cdb611ff9565b600154808252602082f35b60606000806000610cf561204d565b85600202600301805460808352600860808401528060a084015263ffffffff60a060020a8204166020840152600160a060020a03811660408401526001820154905080606084015260a883f35b610d4a611ff9565b600054338114610d6057600182526001601f8301fd5b6002546401000000008110610d7b57600283526001601f8401fd5b855160048114610d9157600384526001601f8501fd5b851515610da457600484526001601f8501fd5b841515610db757600584526001601f8501fd5b602087015160a060020a969096029094179490941764020000000385015550505060010160025550565b6000610deb611ff9565b83640100000000026403000000030183810180548352602083f35b610e0e611ff9565b610e16612069565b600083516020850160958214610e3257600185526001601f8601fd5b8051601482019150600160a060020a036c0100000000000000000000000082041693507f54f117d451d3a82d44e7d7965ecb9427e0092827103aac90b3ffffcc9ed9189585528151905060208201915060008063ffffffff60e060020a84041680602089015263ffffffff60c060020a8504168060408a015267ffffffffffffffff608060020a86041692508260608a0152816403000000000288680300000000000000000264030000000360c060020a01010181600302810194508260020260030154600160a060020a0381168033141515610f155760028d526001601f8e01fd5b50506002850154608060020a900467ffffffffffffffff16808511610f405760038c526001601f8d01fd5b5050505067ffffffffffffffff6801000000000000000084041680608089015267ffffffffffffffff84168060a08a015280680100000000000000008302608060020a85021717600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a81029150678000000000000000811615610fd15767ffffffffffffffff19175b8060c08901525067ffffffffffffffff608060020a840416608060020a8102821791506780000000000000008116156110105767ffffffffffffffff19175b8060e08901525067ffffffffffffffff68010000000000000000840416680100000000000000008102821791506780000000000000008116156110595767ffffffffffffffff19175b8061010089015267ffffffffffffffff8416808317925067800000000000000081161561108c5767ffffffffffffffff19175b806101208a0152826001850155600184015467ffffffffffffffff680100000000000000008204166780000000000000008116156110d05767ffffffffffffffff19175b9092039167ffffffffffffffff81166780000000000000008216156110fb5767ffffffffffffffff19175b80830392505050835467ffffffffffffffff60c060020a820416830167ffffffffffffffff608060020a8304168301677fffffffffffffff8113677fffffffffffffff19821217677fffffffffffffff8313677fffffffffffffff19841217171561116c5760048d526001601f8e01fd5b608060020a67ffffffffffffffff82160260c060020a67ffffffffffffffff841602176fffffffffffffffffffffffffffffffff84161787556101408c207f19010000000000000000000000000000000000000000000000000000000000008d527f044c7dbaf083d34033441e730195278b6b4e3ea03f9bb3fb208a721e7c15b72c60028e015260228d0181905260428d209050808d528951985060208a0199508860208e01528951985060208a0199508860408e015289517f0100000000000000000000000000000000000000000000000000000000000000900460ff1660608e01525060009950600198508b975089965061126b95505050505050565b602002015160608501516020860151604087015160405160008152602001604052604051808581526020018460ff1660ff1681526020018381526020018281526020019450505050506020604051602081039080840390855afa1580156112d6573d6000803e3d6000fd5b50505060206040510351600160a060020a031690508181146112fe57600584526001601f8501fd5b5050505050565b61130d611ff9565b611315612031565b62278d004201841161a8c042018510171561133657600182526001601f8301fd5b60015480861061134c57600283526001601f8401fd5b508460020260030163ffffffff60a060020a82540416866403000000000233680300000000000000000264030000000360c060020a010101816003028101805467ffffffffffffffff60c060020a820416808910156113b157600388526001601f8901fd5b808911156113df5760c060020a890277ffffffffffffffffffffffffffffffffffffffffffffffff83161783555b67ffffffffffffffff60c060020a600185015404168a60c060020a60018301021760018501553388528b60208901527f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604089a1505050505050505050505050565b606060008061144e612087565b846402000000030154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0381166040830152608482f35b600061149c611ff9565b600254808252602082f35b60005490565b6114b5611ff9565b6114bd612015565b6114c5611ff9565b8315156114ce57005b6002548086106114e457600184526001601f8501fd5b507f23b872dd000000000000000000000000000000000000000000000000000000008252336004830152306024830152836044830152846402000000030154600160a060020a0381166020836064866000855af180151561154b57600286526001601f8701fd5b835180151561156057600387526001601f8801fd5b50505050505064010000000033029290920164030000000301805491909101905550565b600080600080600080600080600061159a6120a3565b8b640300000000028d680300000000000000000264030000000360c060020a0101018b600302810180546001820154600283015467ffffffffffffffff60c060020a840416865267ffffffffffffffff608060020a840416602087015267ffffffffffffffff68010000000000000000830416604087015267ffffffffffffffff8216606087015267ffffffffffffffff608060020a820416608087015267ffffffffffffffff60c060020a83041660a087015267ffffffffffffffff608060020a83041660c087015267ffffffffffffffff6801000000000000000082041660e087015267ffffffffffffffff811661010087015261012086f35b6000806116a16120c1565b846403000000000286680300000000000000000264030000000360c060020a010101846003028101805467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff81166020850152604084f35b611700612087565b60208201825180820160a0590352600481101561172957600060205903526001601f6020590301fd5b815160048301925063ffffffff60e060020a820416600154808210151561175c57600160205903526001601f6020590301fd5b60036002830201805433600160a060020a0382161461178757600260205903526001601f6020590301fd5b63ffffffff60a060020a82041660c059035260018201546080590352836060590352505050505b60058360a059035103106112fe578251905060058301925060ff7b01000000000000000000000000000000000000000000000000000000820416602c810280850160a059035181111561180d57600360205903526001601f6020590301fd5b80604059035250505063ffffffff60e060020a8204166000805b6040590351861015611c755785519350601486019550606059035164030000000002600160a060020a036c01000000000000000000000000860416680300000000000000000264030000000360c060020a0101018651945060188701965067ffffffffffffffff60c060020a86041667ffffffffffffffff608060020a87041667ffffffffffffffff680100000000000000008804166780000000000000008316156118dc5767ffffffffffffffff19831792505b6780000000000000008216156118fb5767ffffffffffffffff19821791505b948201949381019360c05903516003028401876003028501600182015477ffffffffffffffffffffffffffffffffffffffffffffffff81168042111561194d57600460205903526001601f6020590301fd5b5050815467ffffffffffffffff80821687018590039081111561197c57600560205903526001601f6020590301fd5b67ffffffffffffffff608060020a8304811686019060c060020a840416808211156119b357600660205903526001601f6020590301fd5b82608060020a83021777ffffffffffffffff0000000000000000ffffffffffffffff198516178655505050506000808a6003028801805467ffffffffffffffff8116888101905067ffffffffffffffff811115611a1c57600760205903526001601f6020590301fd5b67ffffffffffffffff60c060020a830416945067ffffffffffffffff608060020a8304169350678000000000000000851615611a615767ffffffffffffffff19851794505b678000000000000000841615611a805767ffffffffffffffff19841793505b89850194508884019350677fffffffffffffff8413677fffffffffffffff19851217677fffffffffffffff8613677fffffffffffffff198712171715611ad257600860205903526001601f6020590301fd5b80608060020a850260c060020a870217176fffffffffffffffff00000000000000008316178355505050600183015467ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a830416678000000000000000821615611b425767ffffffffffffffff19821791505b678000000000000000811615611b5e5767ffffffffffffffff19175b8084128286121715611b7c57600960205903526001601f6020590301fd5b50505060028301546002600083120260008412018060038114611bae5760018114611bc05760028114611c1a57611c65565b600a60205903526001601f6020590301fd5b831515611bd957600b60205903526001601f6020590301fd5b836305f5e10086600003020467ffffffffffffffff68010000000000000000850416811115611c1457600c60205903526001601f6020590301fd5b50611c65565b841515611c3357600d60205903526001601f6020590301fd5b836000036305f5e10086020467ffffffffffffffff8416811015611c6357600e60205903526001601f6020590301fd5b505b5050505050505050505050611827565b80821715611c8f57600f60205903526001601f6020590301fd5b5050506117ae565b611c9f611ff9565b611ca7612031565b611caf611ff9565b831515611cb857005b336401000000000264030000000301868101805486811015611ce057600186526001601f8701fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008552876004860152866024860152886402000000030154600160a060020a0381166020866044896000855af1801515611d4057600289526001601f8a01fd5b8651801515611d555760038a526001601f8b01fd5b5050505095909503909455505050505050565b611d70611ff9565b611d78612015565b336401000000000264030000000301848101805486640200000003015467ffffffffffffffff60a060020a82041680880283811115611dbd57600188526001601f8901fd5b8084039350838555505050505050846403000000000233680300000000000000000264030000000360c060020a01010184600302810180548567ffffffffffffffff68010000000000000000830416018667ffffffffffffffff83160167ffffffffffffffff8216915067ffffffffffffffff811115611e4357600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553386528960208701528860408701527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606087a150505050505050505050565b611eb0611ff9565b611eb8612015565b846002026003015463ffffffff60a060020a820416866403000000000233680300000000000000000264030000000360c060020a01010186600302810177ffffffffffffffffffffffffffffffffffffffffffffffff60018201541680421015611f2857600187526001601f8801fd5b5050866003028101805467ffffffffffffffff811680891115611f5157600288526001601f8901fd5b88810390508067ffffffffffffffff198316178355505050336401000000000264030000000301878101805489640200000003015467ffffffffffffffff60a060020a820416808b02808401935080841015611fb35760038b526001601f8c01fd5b50505090555033845260208401889052604084018790527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606085a15050505050505050565b6020604051908101604052600181602080388339509192915050565b6080604051908101604052600481608080388339509192915050565b6060604051908101604052600381606080388339509192915050565b60c060405190810160405260068160c080388339509192915050565b610140604051908101604052600a8161014080388339509192915050565b60a060405190810160405260058160a080388339509192915050565b61010060405190810160405260088161010080388339509192915050565b6040805190810160405280600290602082028038833950919291505056fea165627a7a723058203fed9ef763ded33ad9c316d15eb5099a6cf58e872e171172d410c7e612cc81030029";
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
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
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
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
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
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
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
