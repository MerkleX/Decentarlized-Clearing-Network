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
    public static final String BINARY = "608060405234801561001057600080fd5b50336000556127c5806100246000396000f3fe608060405234801561001057600080fd5b50600436106101c85760003560e060020a90048063a88d1902116100fd578063c9de7c081161009b578063e7172f1711610075578063e7172f17146109b9578063f894d39814610a5e578063fb6b385714610a96578063fc429e6a14610ad4576101c8565b8063c9de7c081461092f578063cf58894514610952578063e45565491461095a576101c8565b8063b71a6dd6116100d7578063b71a6dd6146107ce578063be6ae331146107f7578063c03284ba146108a0578063c2d56899146108ed576101c8565b8063a88d19021461079a578063ace1ed07146107a2578063adb6c75b146107c6576101c8565b80637c7347361161016a57806383daf06d1161014457806383daf06d146105b25780638e98cee1146106575780639e51d96714610692578063a68c68b4146106df576101c8565b80637c7347361461047f578063828b51e11461053c578063831b55d61461056e576101c8565b806336384331116101a657806336384331146102ec57806337f265e61461031e578063431ec60114610387578063541694cf146103a8576101c8565b80630faf0904146101cd57806311a86b15146101f557806314f604b8146102ae575b600080fd5b6101f3600480360360208110156101e357600080fd5b5035600160a060020a0316610b12565b005b6101f36004803603606081101561020b57600080fd5b81019060208101813564010000000081111561022657600080fd5b82018360208201111561023857600080fd5b8035906020019184600183028401116401000000008311171561025a57600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813563ffffffff1692505060200135600160a060020a0316610b27565b6101f3600480360360608110156102c457600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16610bc0565b6101f36004803603604081101561030257600080fd5b50803563ffffffff169060200135600160a060020a0316610d79565b6103506004803603604081101561033457600080fd5b508035600160a060020a0316906020013563ffffffff16610db5565b60405167ffffffffffffffff9485168152928416602084015290831660408084019190915292166060820152608001905180910390f35b61038f610e6b565b60405163ffffffff909116815260200160405180910390f35b6103cb600480360360208110156103be57600080fd5b503563ffffffff16610e80565b60405167ffffffffffffffff8087166020830152600160a060020a03808716604084015290851660608301528381166080830152821660a082015260c08082528190810188818151815260200191508051906020019080838360005b8381101561043f578082015183820152602001610427565b50505050905090810190601f16801561046c5780820380516001836020036101000a031916815260200191505b5097505050505050505060405180910390f35b6101f36004803603606081101561049557600080fd5b8101906020810181356401000000008111156104b057600080fd5b8201836020820111156104c257600080fd5b803590602001918460018302840111640100000000831117156104e457600080fd5b91908080601f01602080910402602001604051908101604052818152929190602084018383808284376000920191909152509295505050813567ffffffffffffffff1692505060200135600160a060020a0316610ef4565b6101f36004803603604081101561055257600080fd5b50803563ffffffff169060200135600160a060020a0316610f93565b6105a06004803603604081101561058457600080fd5b508035600160a060020a0316906020013563ffffffff16610fb5565b60405190815260200160405180910390f35b6101f3600480360360208110156105c857600080fd5b8101906020810181356401000000008111156105e357600080fd5b8201836020820111156105f557600080fd5b8035906020019184600183028401116401000000008311171561061757600080fd5b91908080601f0160208091040260200160405190810160405281815292919060208401838380828437600092019190915250929550610fda945050505050565b6101f36004803603606081101561066d57600080fd5b5063ffffffff8135169067ffffffffffffffff60208201358116916040013516611513565b6101f3600480360360808110156106a857600080fd5b50803563ffffffff908116916020810135909116906040810135600160a060020a0316906060013567ffffffffffffffff1661164f565b610702600480360360208110156106f557600080fd5b503563ffffffff1661178e565b60405167ffffffffffffffff83166020820152600160a060020a038216604082015260608082528190810185818151815260200191508051906020019080838360005b8381101561075d578082015183820152602001610745565b50505050905090810190601f16801561078a5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b61038f6117df565b6107aa6117f4565b604051600160a060020a03909116815260200160405180910390f35b6101f36117fa565b6101f3600480360360408110156107e457600080fd5b5063ffffffff8135169060200135611822565b6108316004803603606081101561080d57600080fd5b50600160a060020a038135169063ffffffff602082013581169160400135166118f9565b6040516007998a0b8a0b815297890b890b602089015295880b880b60408089019190915294880b880b606088015267ffffffffffffffff938416608088015291870b870b60a0870152860b90950b60c085015293841660e0840152921661010082015261012001905180910390f35b6101f3600480360360808110156108b657600080fd5b50803563ffffffff908116916020810135909116906040810135600160a060020a0316906060013567ffffffffffffffff16611aba565b6101f36004803603606081101561090357600080fd5b50803563ffffffff16906020810135600160a060020a0316906040013567ffffffffffffffff16611b88565b6101f36004803603602081101561094557600080fd5b503563ffffffff16611c8f565b6101f3611cb4565b6109946004803603606081101561097057600080fd5b50600160a060020a038135169063ffffffff60208201358116916040013516611cfe565b60405167ffffffffffffffff9283168152911660208201526040908101905180910390f35b6101f3600480360360208110156109cf57600080fd5b8101906020810181356401000000008111156109ea57600080fd5b8201836020820111156109fc57600080fd5b80359060200191846001830284011164010000000083111715610a1e57600080fd5b91908080601f0160208091040260200160405190810160405281815292919060208401838380828437600092019190915250929550611d60945050505050565b6101f360048036036060811015610a7457600080fd5b5063ffffffff81351690600160a060020a036020820135169060400135612350565b6101f360048036036060811015610aac57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16612422565b6101f360048036036060811015610aea57600080fd5b50803563ffffffff908116916020810135909116906040013567ffffffffffffffff16612562565b600054338114610b2157600080fd5b50600055565b610b2f6126b3565b600054338114610b4557600182526001601f8301fd5b845160088114610b5b57600283526001601f8401fd5b600254808610610b7157600384526001601f8501fd5b6001546401000000008110610b8c57600485526001601f8601fd5b8060040260040160208901518760a060020a8a02178117808355886002840155600184016001555050505050505050505050565b610bc86126b3565b610bd06126cf565b610bd86126b3565b610be06126eb565b600254808710610bf657600185526001601f8601fd5b50841515610c0a57600284526001601f8501fd5b85640400000004015467ffffffffffffffff60a060020a8204168602600160a060020a0382167f23b872dd0000000000000000000000000000000000000000000000000000000086523360048701523060248701528160448701526020856064886000855af1801515610c8357600388526001601f8901fd5b8551801515610c9857600489526001601f8a01fd5b5050896403000000000233680300000000000000000264050000000460c060020a010101896003028101805467ffffffffffffffff8b67ffffffffffffffff6801000000000000000084041601168b67ffffffffffffffff83160167ffffffffffffffff811115610d0f5760058c526001601f8d01fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553389528e60208a01528d60408a01527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf50060608aa1505050505050505050505050505050565b8160040260040160028101543381141515610d9357600080fd5b50805473ffffffffffffffffffffffffffffffffffffffff1916909117905550565b600080600080610dc36126cf565b8560040260040163ffffffff60a060020a82540416876403000000000289680300000000000000000264050000000460c060020a0101018160030281018054600182015467ffffffffffffffff60c060020a820416875277ffffffffffffffffffffffffffffffffffffffffffffffff8116602088015267ffffffffffffffff60c060020a830416604088015267ffffffffffffffff608060020a8304166060880152608087f35b6000610e756126b3565b600154808252602082f35b60606000806000806000610e92612707565b6004808902908101805460c080855260089085015260e0840181905260a060020a810463ffffffff166020850152600160a060020a03166040840152600582015460608401526006820154608084015260079091015460a0830181905260e883f35b610efc6126b3565b600054338114610f1257600182526001601f8301fd5b6002546401000000008110610f2d57600283526001601f8401fd5b855160048114610f4357600384526001601f8501fd5b851515610f5657600484526001601f8501fd5b841515610f6957600584526001601f8501fd5b602087015160a060020a969096029094179490941764040000000485015550505060010160025550565b8160040260040160028101543381141515610fad57600080fd5b506003015550565b6000610fbf6126b3565b83640100000000026405000000040183810180548352602083f35b610fe26126b3565b610fea612725565b600080600085516020870191508082019250609581061561101157600186526001601f8701fd5b505b8181141561101d57005b8051601482019150600160a060020a036c0100000000000000000000000082041693507fe0bfc2789e007df269c9fec46d3ddd4acf88fdf0f76af154da933aab7fb2f2b985528151905060208201915060008063ffffffff60e060020a84041680602089015263ffffffff60c060020a8504168060408a015267ffffffffffffffff608060020a86041692508260608a0152816403000000000288680300000000000000000264050000000460c060020a01010181600302810194508260040260040154600160a060020a03811680331415156111005760028d526001601f8e01fd5b63ffffffff60a060020a830416848114156111215760068e526001601f8f01fd5b5050506002850154608060020a900467ffffffffffffffff1680851161114d5760038c526001601f8d01fd5b5050505067ffffffffffffffff6801000000000000000084041680608089015267ffffffffffffffff84168060a08a015280680100000000000000008302608060020a85021717600285015550505082519150602083019250600067ffffffffffffffff60c060020a84041660c060020a810291506780000000000000008116156111de5767ffffffffffffffff19175b8060c08901525067ffffffffffffffff608060020a840416608060020a81028217915067800000000000000081161561121d5767ffffffffffffffff19175b8060e08901525067ffffffffffffffff68010000000000000000840416680100000000000000008102821791506780000000000000008116156112665767ffffffffffffffff19175b8061010089015267ffffffffffffffff841680831792506780000000000000008116156112995767ffffffffffffffff19175b806101208a0152826001850155600184015467ffffffffffffffff680100000000000000008204166780000000000000008116156112dd5767ffffffffffffffff19175b9092039167ffffffffffffffff81166780000000000000008216156113085767ffffffffffffffff19175b80830392505050835467ffffffffffffffff60c060020a820416830167ffffffffffffffff608060020a8304168301677fffffffffffffff8113677fffffffffffffff19821217677fffffffffffffff8313677fffffffffffffff1984121717156113795760048d526001601f8e01fd5b608060020a67ffffffffffffffff82160260c060020a67ffffffffffffffff841602176fffffffffffffffffffffffffffffffff84161787556101408c207f19010000000000000000000000000000000000000000000000000000000000008d527fe3d3073cc59e3a3126c17585a7e516a048e61a9a1c82144af982d1c194b1871060028e015260228d0181905260428d209050808d528951985060208a0199508860208e01528951985060208a0199508860408e015289517f0100000000000000000000000000000000000000000000000000000000000000900460ff1660608e015250505060019687019660009650945089935085925061147a915050565b602002015160608701516020880151604089015160405160008152602001604052604051808581526020018460ff1660ff1681526020018381526020018281526020019450505050506020604051602081039080840390855afa1580156114e5573d6000803e3d6000fd5b50505060206040510351600160a060020a0316905083811461150d57600586526001601f8701fd5b50611013565b61151b6126b3565b6115236126eb565b62278d004201841161a8c042018510171561154457600182526001601f8301fd5b60015480861061155a57600283526001601f8401fd5b508460040260040163ffffffff60a060020a82540416866403000000000233680300000000000000000264050000000460c060020a010101816003028101805467ffffffffffffffff60c060020a820416808910156115bf57600388526001601f8901fd5b808911156115ed5760c060020a890277ffffffffffffffffffffffffffffffffffffffffffffffff83161783555b67ffffffffffffffff60c060020a600185015404168a60c060020a60018301021760018501553388528b60208901527f1fceb0227bbc8d151c84f6f90cac5b115842ef0ed5dd5b6ee6bf6eca2dae91f7604089a1505050505050505050505050565b6116576126b3565b61165f6126eb565b6116676126b3565b83151561167057005b6004808802015433600160a060020a0382161461169357600184526001601f8501fd5b876403000000000286680300000000000000000264050000000460c060020a010101876003028101805467ffffffffffffffff8116808911156116dc57600288526001601f8901fd5b88810390508067ffffffffffffffff1983161783558a640400000004015467ffffffffffffffff60a060020a820416600160a060020a038216818c027fa9059cbb000000000000000000000000000000000000000000000000000000008b528d60048c01528060248c015260208a60448d6000865af18015156117655760038d526001601f8e01fd5b8a5180151561177a5760048e526001601f8f01fd5b505050505050505050505050505050505050565b606060008061179b612743565b846404000000040154606082526004606083015280608083015267ffffffffffffffff60a060020a8204166020830152600160a060020a0381166040830152608482f35b60006117e96126b3565b600254808252602082f35b60005490565b6118026126b3565b60005433811461181857600182526001601f8301fd5b6000196003555050565b61182a6126b3565b6118326126cf565b61183a6126b3565b83151561184357005b60025480861061185957600184526001601f8501fd5b507f23b872dd000000000000000000000000000000000000000000000000000000008252336004830152306024830152836044830152846404000000040154600160a060020a0381166020836064866000855af18015156118c057600286526001601f8701fd5b83518015156118d557600387526001601f8801fd5b50505050505064010000000033029290920164050000000401805491909101905550565b600080600080600080600080600061190f612707565b8b640300000000028d680300000000000000000264050000000460c060020a0101018b600302810180546001820154600283015467ffffffffffffffff60c060020a8404166780000000000000008116156119705767ffffffffffffffff19175b8652608060020a830467ffffffffffffffff81169067800000000000000016156119a05767ffffffffffffffff19175b602087015268010000000000000000820467ffffffffffffffff81169067800000000000000016156119d85767ffffffffffffffff19175b604087015267ffffffffffffffff8216678000000000000000831615611a045767ffffffffffffffff19175b80606088015267ffffffffffffffff608060020a830416608088015267ffffffffffffffff60c060020a8404169050678000000000000000811615611a4f5767ffffffffffffffff19175b60a0870152608060020a820467ffffffffffffffff8116906780000000000000001615611a825767ffffffffffffffff19175b8060c088015267ffffffffffffffff6801000000000000000083041660e088015267ffffffffffffffff821661010088015261012087f35b611ac26126b3565b811515611acb57005b6004808602015433600160a060020a03821614611aee57600182526001601f8301fd5b856403000000000284680300000000000000000264050000000460c060020a010101856003028101805467ffffffffffffffff811680871115611b3757600286526001601f8701fd5b67ffffffffffffffff19919091169086900317905550505064040000000483015464010000000090920290920164050000000401805460a060020a90920467ffffffffffffffff1690920201905550565b611b906126b3565b611b986126eb565b611ba06126b3565b6004808702018054600160a060020a03811660a060020a820463ffffffff16338214611bd257600187526001601f8801fd5b80640400000004015467ffffffffffffffff60a060020a820416600160a060020a0382166001870154808c1115611c0f5760028b526001601f8c01fd5b8b81036001890155828c027fa9059cbb000000000000000000000000000000000000000000000000000000008b528d60048c01528060248c015260208a60448d6000875af1801515611c675760038d526001601f8e01fd5b8a51801515611c7c5760048e526001601f8f01fd5b5050505050505050505050505050505050565b8060040260040160038101543381141515611ca957600080fd5b336002830155505050565b611cbc6126b3565b600054338114611cd257600182526001601f8301fd5b60035442811115611cf45762069780420181811115611ced57005b6003819055005b6000600355505050565b600080611d0961275f565b846403000000000286680300000000000000000264050000000460c060020a010101846003028101805467ffffffffffffffff68010000000000000000820416845267ffffffffffffffff81166020850152604084f35b611d6861277d565b60035415611d8257606460205903526001601f6020590301fd5b60208201825180820160a05903526004811015611dab57600060205903526001601f6020590301fd5b815160048301925063ffffffff60e060020a8204166001548082101515611dde57600160205903526001601f6020590301fd5b600480830201805433600160a060020a03821614611e0857600260205903526001601f6020590301fd5b63ffffffff60a060020a82041660c059035260018201546080590352836060590352505050505b60058360a05903510310612338578251905060058301925060ff7b01000000000000000000000000000000000000000000000000000000820416602c810280850160a0590351811115611e8e57600360205903526001601f6020590301fd5b80604059035250505063ffffffff60e060020a8204166000805b60405903518610156123165785519350601486019550606059035164030000000002600160a060020a036c01000000000000000000000000860416680300000000000000000264050000000460c060020a0101018651945060188701965067ffffffffffffffff60c060020a86041667ffffffffffffffff608060020a87041667ffffffffffffffff68010000000000000000880416678000000000000000831615611f5d5767ffffffffffffffff19831792505b678000000000000000821615611f7c5767ffffffffffffffff19821791505b948201949381019360c05903516003028401876003028501600182015477ffffffffffffffffffffffffffffffffffffffffffffffff811680421115611fce57600460205903526001601f6020590301fd5b5050815467ffffffffffffffff808216870185900390811115611ffd57600560205903526001601f6020590301fd5b608060020a820467ffffffffffffffff1685016080590386815101815267ffffffffffffffff60c060020a8504168083111561204557600660205903526001601f6020590301fd5b83608060020a84021777ffffffffffffffff0000000000000000ffffffffffffffff19861617875550505050506000808a6003028801805467ffffffffffffffff8116888101905067ffffffffffffffff8111156120af57600760205903526001601f6020590301fd5b67ffffffffffffffff60c060020a830416945067ffffffffffffffff608060020a83041693506780000000000000008516156120f45767ffffffffffffffff19851794505b6780000000000000008416156121135767ffffffffffffffff19841793505b89850194508884019350677fffffffffffffff8413677fffffffffffffff19851217677fffffffffffffff8613677fffffffffffffff19871217171561216557600860205903526001601f6020590301fd5b80608060020a850260c060020a870217176fffffffffffffffff00000000000000008316178355505050600183015467ffffffffffffffff60c060020a82041667ffffffffffffffff608060020a8304166780000000000000008216156121d55767ffffffffffffffff19821791505b6780000000000000008116156121f15767ffffffffffffffff19175b808412828612171561220f57600960205903526001601f6020590301fd5b50505060028301546002600183120260018412018060038114612241576001811461226157600281146122bb57612306565b838517151561225c57600a60205903526001601f6020590301fd5b612306565b83151561227a57600b60205903526001601f6020590301fd5b836305f5e10086600003020467ffffffffffffffff680100000000000000008504168111156122b557600c60205903526001601f6020590301fd5b50612306565b8415156122d457600d60205903526001601f6020590301fd5b836000036305f5e10086020467ffffffffffffffff841681101561230457600e60205903526001601f6020590301fd5b505b5050505050505050505050611ea8565b8082171561233057600f60205903526001601f6020590301fd5b505050611e2f565b60805903516060590351600402600501555050505050565b6123586126b3565b6123606126eb565b6123686126b3565b83151561237157005b33640100000000026405000000040186810180548681101561239957600186526001601f8701fd5b8681038255886404000000040154600160a060020a0381167fa9059cbb0000000000000000000000000000000000000000000000000000000087528960048801528860248801526020866044896000855af18015156123fe57600289526001601f8a01fd5b86518015156124135760038a526001601f8b01fd5b50505050505050505050505050565b61242a6126b3565b6124326126cf565b336401000000000264050000000401848101805486640400000004015467ffffffffffffffff60a060020a8204168088028381111561247757600188526001601f8901fd5b8084039350838555505050505050846403000000000233680300000000000000000264050000000460c060020a01010184600302810180548567ffffffffffffffff68010000000000000000830416018667ffffffffffffffff83160167ffffffffffffffff8216915067ffffffffffffffff8111156124fd57600287526001601f8801fd5b80680100000000000000008302176fffffffffffffffffffffffffffffffff1984161784553386528960208701528860408701527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606087a150505050505050505050565b61256a6126b3565b6125726126cf565b846004026004015463ffffffff60a060020a820416866403000000000233680300000000000000000264050000000460c060020a01010181600302810177ffffffffffffffffffffffffffffffffffffffffffffffff600182015416804210156125e257600187526001601f8801fd5b5050866003028101805467ffffffffffffffff81168089111561260b57600288526001601f8901fd5b88810390508067ffffffffffffffff198316178355505050336401000000000264050000000401878101805489640400000004015467ffffffffffffffff60a060020a820416808b0280840193508084101561266d5760038b526001601f8c01fd5b50505090555033845260208401889052604084018790527f80e69f6146713abffddddec8ef3901e1cd3fd9e079375d62e04e2719f1adf500606085a15050505050505050565b6020604051908101604052600181602080388339509192915050565b6080604051908101604052600481608080388339509192915050565b6060604051908101604052600381606080388339509192915050565b61010060405190810160405260088161010080388339509192915050565b610140604051908101604052600a8161014080388339509192915050565b60a060405190810160405260058160a080388339509192915050565b60408051908101604052806002906020820280388339509192915050565b60c060405190810160405260068160c08038833950919291505056fea165627a7a72305820afab44b0fc19862ffbc945dca7145da493971d383ec9526c68a4d41edfac15600029";
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
    public static Function exchange_update_owner(int exchange_id, String new_owner) {
        return new Function(
            "exchange_update_owner",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.Address(new_owner)
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
                , new TypeReference<org.web3j.abi.datatypes.Address>() {}
                , new TypeReference<org.web3j.abi.datatypes.Address>() {}
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
        returnValue.owner_backup = (String) values.get(4).getValue();
        returnValue.owner_backup_proposed = (String) values.get(5).getValue();
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
    public static Function exchange_propose_backup(int exchange_id, String backup) {
        return new Function(
            "exchange_propose_backup",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.Address(backup)
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
    public static Function exchange_withdraw_fees(int exchange_id, String destination, long quantity) {
        return new Function(
            "exchange_withdraw_fees",
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
                , new org.web3j.abi.datatypes.Address(destination)
                , new org.web3j.abi.datatypes.generated.Uint64(quantity)
            ),
            Collections.emptyList()
        );
    }
    public static Function exchange_set_backup(int exchange_id) {
        return new Function(
            "exchange_set_backup",
            Collections.singletonList(
                new org.web3j.abi.datatypes.generated.Uint32(exchange_id)
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
        public String owner_backup;
        public String owner_backup_proposed;
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
