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
    public static final String BINARY = "608060405234801561001057600080fd5b5033600055611eb6806100246000396000f3fe6080604052600436106100e75760e060020a60003504630faf090481146100ec57806311a86b151461012157806314f604b8146101e85780631d6015671461023357806337f265e614610273578063431ec601146102e7578063541694cf146103155780637c7347361461040c578063831b55d6146104d757806383daf06d14610528578063a68c68b4146105db578063a88d1902146106b7578063ace1ed07146106cc578063b71a6dd6146106fd578063be6ae33114610733578063e4556549146107e7578063e7172f1714610855578063f894d39814610908578063fb6b38571461094d575b600080fd5b3480156100f857600080fd5b5061011f6004803603602081101561010f57600080fd5b5035600160a060020a0316610998565b005b34801561012d57600080fd5b5061011f6004803603606081101561014457600080fd5b81019060208101813564010000000081111561015f57600080fd5b82018360208201111561017157600080fd5b8035906020019184600183028401116401000000008311171561019357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505050813563ffffffff1692505060200135600160a060020a03166109ad565b3480156101f457600080fd5b5061011f6004803603606081101561020b57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16610a3e565b34801561023f57600080fd5b5061011f6004803603604081101561025657600080fd5b50803563ffffffff16906020013567ffffffffffffffff16610be4565b34801561027f57600080fd5b506102b26004803603604081101561029657600080fd5b508035600160a060020a0316906020013563ffffffff16610cc8565b6040805167ffffffffffffffff9586168152938516602085015291841683830152909216606082015290519081900360800190f35b3480156102f357600080fd5b506102fc610d7e565b6040805163ffffffff9092168252519081900360200190f35b34801561032157600080fd5b506103456004803603602081101561033857600080fd5b503563ffffffff16610d93565b60405180806020018567ffffffffffffffff1667ffffffffffffffff16815260200184600160a060020a0316600160a060020a031681526020018367ffffffffffffffff1667ffffffffffffffff168152602001828103825286818151815260200191508051906020019080838360005b838110156103ce5781810151838201526020016103b6565b50505050905090810190601f1680156103fb5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b34801561041857600080fd5b5061011f6004803603606081101561042f57600080fd5b81019060208101813564010000000081111561044a57600080fd5b82018360208201111561045c57600080fd5b8035906020019184600183028401116401000000008311171561047e57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505050813567ffffffffffffffff1692505060200135600160a060020a0316610def565b3480156104e357600080fd5b50610516600480360360408110156104fa57600080fd5b508035600160a060020a0316906020013563ffffffff16610e8e565b60408051918252519081900360200190f35b34801561053457600080fd5b5061011f6004803603602081101561054b57600080fd5b81019060208101813564010000000081111561056657600080fd5b82018360208201111561057857600080fd5b8035906020019184600183028401116401000000008311171561059a57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610eb3945050505050565b3480156105e757600080fd5b5061060b600480360360208110156105fe57600080fd5b503563ffffffff16611390565b60405180806020018467ffffffffffffffff1667ffffffffffffffff16815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360005b8381101561067a578181015183820152602001610662565b50505050905090810190601f1680156106a75780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b3480156106c357600080fd5b506102fc6113e1565b3480156106d857600080fd5b506106e16113f6565b60408051600160a060020a039092168252519081900360200190f35b34801561070957600080fd5b5061011f6004803603604081101561072057600080fd5b5063ffffffff81351690602001356113fc565b34801561073f57600080fd5b5061077a6004803603606081101561075657600080fd5b50600160a060020a038135169063ffffffff602082013581169160400135166114d8565b6040805160079a8b0b8b0b8152988a0b8a0b60208a015296890b890b8888015294880b880b606088015267ffffffffffffffff938416608088015291870b870b60a0870152860b90950b60c085015293841660e08401529092166101008201529051908190036101200190f35b3480156107f357600080fd5b5061082e6004803603606081101561080a57600080fd5b50600160a060020a038135169063ffffffff602082013581169160400135166115ea565b6040805167ffffffffffffffff938416815291909216602082015281519081900390910190f35b34801561086157600080fd5b5061011f6004803603602081101561087857600080fd5b81019060208101813564010000000081111561089357600080fd5b8201836020820111156108a557600080fd5b803590602001918460018302840111640100000000831117156108c757600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061164c945050505050565b34801561091457600080fd5b5061011f6004803603606081101561092b57600080fd5b5063ffffffff81351690600160a060020a036020820135169060400135611b85565b34801561095957600080fd5b5061011f6004803603606081101561097057600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16611c56565b6000543381146109a757600080fd5b50600055565b6109b5611d94565b6000543381146109cb57600182526001601f8301fd5b8451600881146109e157600283526001601f8401fd5b6002548086106109f757600384526001601f8501fd5b6001546401000000008110610a1257600485526001601f8601fd5b6020979097015160a060020a969096029094179094176003600287020155505050600191820190915550565b610a46611d94565b610a4e611db3565b610a56611d94565b610a5e611dd2565b600254808710610a7457600185526001601f8601fd5b5085640200000003015467ffffffffffffffff60a060020a8204168602600160a060020a0382167f23b872dd0000000000000000000000000000000000000000000000000000000086523360048701523060248701528160448701526020856064886000855af1801515610aee57600288526001601f8901fd5b8551801515610b0357600389526001601f8a01fd5b5050896403000000000233680300000000000000000264030000000360c060020a010101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff83160167ffffffffffffffff811115610b7a5760048c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b610bec611d94565b610bf4611dd2565b62278d004201831161a8c0420184101715610c1557600182526001601f8301fd5b600154808510610c2b57600283526001601f8401fd5b508360020260030163ffffffff60a060020a82540416856403000000000233680300000000000000000264030000000360c060020a01010181600302810167ffffffffffffffff60c060020a600183015404168760c060020a60018301021760018301553386528860208701527f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604087a1505050505050505050565b600080600080610cd6611db3565b8560020260030163ffffffff60a060020a82540416876403000000000289680300000000000000000264030000000360c060020a0101018160030281018154600183015467ffffffffffffffff60c060020a820416875277ffffffffffffffffffffffffffffffffffffffffffffffff8116602088015267ffffffffffffffff60c060020a830416604088015267ffffffffffffffff608060020a8304166060880152608087f35b6000610d88611d94565b600154808252602082f35b60606000806000610da2611df1565b85600202600301805460808352600860808401528060a084015263ffffffff60a060020a8204166020840152600160a060020a03811660408401526001820154905080606084015260a883f35b610df7611d94565b600054338114610e0d57600182526001601f8301fd5b6002546401000000008110610e2857600283526001601f8401fd5b855160048114610e3e57600384526001601f8501fd5b851515610e5157600484526001601f8501fd5b841515610e6457600584526001601f8501fd5b506020959095015160a060020a949094029092179092176402000000038401555050600101600255565b6000610e98611d94565b83640100000000026403000000030183810180548352602083f35b610ebb611d94565b610ec3611e10565b82516000906020850160958214610ee057600185526001601f8601fd5b8051601482019150600160a060020a036c0100000000000000000000000082041693507f74be7520fc933d8061b6cf113d28a772f7a40539ab5e0e8276dd066dd71a7d6985528151905060208201915060008063ffffffff60e060020a84041680602089015263ffffffff60c060020a8504168060408a015267ffffffffffffffff608060020a86041692508260608a0152816403000000000288680300000000000000000264030000000360c060020a01010181600302810194508260020260030154600160a060020a0381168033141515610fc35760028d526001601f8e01fd5b506001600360a060020a830463ffffffff16028301015460c060020a810467ffffffffffffffff16868110610ffe5760038e526001601f8f01fd5b50505050505067ffffffffffffffff6801000000000000000084041680608089015267ffffffffffffffff84168060a08a015280680100000000000000008302608060020a85021717600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a810291506780000000000000008116156110915767ffffffffffffffff19175b8060c08901525067ffffffffffffffff608060020a840416608060020a8102821791506780000000000000008116156110d05767ffffffffffffffff19175b8060e08901525067ffffffffffffffff68010000000000000000840416680100000000000000008102821791506780000000000000008116156111195767ffffffffffffffff19175b8061010089015267ffffffffffffffff8416808317925067800000000000000081161561114c5767ffffffffffffffff19175b806101208a0152826001850155600184015467ffffffffffffffff680100000000000000008204166780000000000000008116156111905767ffffffffffffffff19175b9092039167ffffffffffffffff81166780000000000000008216156111bb5767ffffffffffffffff19175b80830392505050835467ffffffffffffffff60c060020a820416830167ffffffffffffffff608060020a8304168301677fffffffffffffff8113677fffffffffffffff19821217677fffffffffffffff8313677fffffffffffffff19841217171561122c5760048d526001601f8e01fd5b6fffffffffffffffffffffffffffffffff9290921667ffffffffffffffff91821660c060020a0292909116608060020a029190911717909355505061014086207f190100000000000000000000000000000000000000000000000000000000000087527f8bdc799ab1e4f88b464481578308e5bde325b7ed088fe2b99495c7924d58c7f96002880152602287015250506042842080855281516020808701829052838101516040808901829052948501517f0100000000000000000000000000000000000000000000000000000000000000900460ff166060808a0182905286516000808252818601808a5297909752808801929092528101939093526080830152925191935060019260a0808301939192601f198301929081900390910190855afa158015611360573d6000803e3d6000fd5b5050604051601f190151600160a060020a031691505081811461138957600584526001601f8501fd5b5050505050565b606060008061139d611e30565b846402000000030154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0381166040830152608482f35b60006113eb611d94565b600254808252602082f35b60005490565b611404611d94565b61140c611db3565b611414611d94565b83151561141d57005b600254808610158615171561143857600184526001601f8501fd5b507f23b872dd000000000000000000000000000000000000000000000000000000008252336004830152306024830152836044830152846402000000030154600160a060020a0381166020836064866000855af180151561149f57600286526001601f8701fd5b83518015156114b457600387526001601f8801fd5b50505050505064010000000033029290920164030000000301805491909101905550565b60008060008060008060008060006114ee611e4f565b8b640300000000028d680300000000000000000264030000000360c060020a0101018b600302810180546001820154600283015467ffffffffffffffff60c060020a840416865267ffffffffffffffff608060020a840416602087015267ffffffffffffffff68010000000000000000830416604087015267ffffffffffffffff8216606087015267ffffffffffffffff608060020a820416608087015267ffffffffffffffff60c060020a83041660a087015267ffffffffffffffff608060020a83041660c087015267ffffffffffffffff6801000000000000000082041660e087015267ffffffffffffffff811661010087015261012086f35b6000806115f5611e6f565b846403000000000286680300000000000000000264030000000360c060020a010101846003028101805467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff81166020850152604084f35b611654611e30565b60c0811461166957600061016052600161017ffd5b600182018251810160e052805160048201915063ffffffff60e060020a82041660015480821015156116a257600161016081905261017ffd5b60036002830201805433600160a060020a038216146116c857600261016052600161017ffd5b60a060020a900463ffffffff1660c052600101546101005250610120525b60058260e05103101515611b7f578151905060048201915060ff7b01000000000000000000000000000000000000000000000000000000820416602c810280840160e05181111561173e57600361016052600161017ffd5b61014052505060e060020a810463ffffffff166000805b61014051851015611b6a57845193506014850194506101205164030000000002600160a060020a036c01000000000000000000000000860416680300000000000000000264030000000360c060020a0101018551945060188601955067ffffffffffffffff60c060020a86041667ffffffffffffffff608060020a87041667ffffffffffffffff680100000000000000008804166780000000000000008316156118085767ffffffffffffffff19831792505b6780000000000000008216156118275767ffffffffffffffff19821791505b8286019550818501945060c0516003028401876003028501600182015477ffffffffffffffffffffffffffffffffffffffffffffffff81164281111561187457600561016052600161017ffd5b5050815467ffffffffffffffff80821687018590039081111561189e57600461016052600161017ffd5b67ffffffffffffffff608060020a8304811686019060c060020a840416808211156118d057600561016052600161017ffd5b82608060020a83021777ffffffffffffffff0000000000000000ffffffffffffffff198516178655505050506000808a6003028801805467ffffffffffffffff8116888101905067ffffffffffffffff81111561193457600661016052600161017ffd5b67ffffffffffffffff60c060020a830416945067ffffffffffffffff608060020a83041693506780000000000000008516156119795767ffffffffffffffff19851794505b6780000000000000008416156119985767ffffffffffffffff19841793505b89850194508884019350677fffffffffffffff8413677fffffffffffffff19851217677fffffffffffffff8613677fffffffffffffff1987121717156119e557600761016052600161017ffd5b80608060020a850260c060020a870217176fffffffffffffffff00000000000000008316178355505050600183015467ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a830416678000000000000000821615611a555767ffffffffffffffff19821791505b678000000000000000811615611a715767ffffffffffffffff19175b8084128286121715611a8a57600861016052600161017ffd5b50505060028301546002600083120260008412018060038114611abc5760018114611ac95760028114611b1957611b5a565b600961016052600161017ffd5b831515611add57600a61016052600161017ffd5b836305f5e10086600003020467ffffffffffffffff68010000000000000000850416811115611b1357600b61016052600161017ffd5b50611b5a565b841515611b2d57600c61016052600161017ffd5b836000036305f5e10086020467ffffffffffffffff8416811015611b5857600d61016052600161017ffd5b505b5050505050505050505050611755565b80821715611b7757600080fd5b5050506116e6565b50505050565b611b8d611d94565b611b95611dd2565b611b9d611d94565b831515611ba657005b336401000000000264030000000301868101805486811015611bce57600186526001601f8701fd5b7fa9059cbb000000000000000000000000000000000000000000000000000000008552876004860152866024860152886402000000030154600160a060020a0381166020866044896000855af1801515611c2e57600289526001601f8a01fd5b8651801515611c435760038a526001601f8b01fd5b5050505095909503909455505050505050565b611c5e611d94565b611c66611db3565b336401000000000264030000000301848101805486640200000003015467ffffffffffffffff60a060020a82041680880283811115611cab57600188526001601f8901fd5b8084039350505050876403000000000233680300000000000000000264030000000360c060020a01010180600302600001805467ffffffffffffffff8967ffffffffffffffff6801000000000000000084041601168967ffffffffffffffff83160167ffffffffffffffff811115611d295760028a526001601f8b01fd5b85875580680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528c60208a01528b60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa150505050505050505050505050565b6020604051908101604052806001906020820280388339509192915050565b6080604051908101604052806004906020820280388339509192915050565b6060604051908101604052806003906020820280388339509192915050565b60c0604051908101604052806006906020820280388339509192915050565b61014060405190810160405280600a906020820280388339509192915050565b60a0604051908101604052806005906020820280388339509192915050565b610100604051908101604052806008906020820280388339509192915050565b6040805180820182529060029082908038833950919291505056fea165627a7a72305820f38e03ed453e2e6510343fc8e720d93b16729300e4eabe5f80bb59f065935c850029";
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
