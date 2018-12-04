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
public class ERC20 {
    public static final String BINARY = "608060405234801561001057600080fd5b506040516108ad3803806108ad8339810180604052608081101561003357600080fd5b81516020830180519193928301929164010000000081111561005457600080fd5b8201602081018481111561006757600080fd5b815164010000000081118282018710171561008157600080fd5b505060208201516040909201805191949293916401000000008111156100a657600080fd5b820160208101848111156100b957600080fd5b81516401000000008111828201871017156100d357600080fd5b50503360009081526020818152604090912088905560028890558651919450610103935060039250860190610130565b506004805460ff191660ff84161790558051610126906005906020840190610130565b50505050506101cb565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061017157805160ff191683800117855561019e565b8280016001018555821561019e579182015b8281111561019e578251825591602001919060010190610183565b506101aa9291506101ae565b5090565b6101c891905b808211156101aa57600081556001016101b4565b90565b6106d3806101da6000396000f3fe6080604052600436106100ae5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100b3578063095ea7b31461013d57806318160ddd1461018a57806323b872dd146101b157806327e235e3146101f4578063313ce567146102275780635c6581651461025257806370a082311461028d57806395d89b41146102c0578063a9059cbb146102d5578063dd62ed3e1461030e575b600080fd5b3480156100bf57600080fd5b506100c8610349565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101025781810151838201526020016100ea565b50505050905090810190601f16801561012f5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561014957600080fd5b506101766004803603604081101561016057600080fd5b50600160a060020a0381351690602001356103d7565b604080519115158252519081900360200190f35b34801561019657600080fd5b5061019f61043d565b60408051918252519081900360200190f35b3480156101bd57600080fd5b50610176600480360360608110156101d457600080fd5b50600160a060020a03813581169160208101359091169060400135610443565b34801561020057600080fd5b5061019f6004803603602081101561021757600080fd5b5035600160a060020a0316610546565b34801561023357600080fd5b5061023c610558565b6040805160ff9092168252519081900360200190f35b34801561025e57600080fd5b5061019f6004803603604081101561027557600080fd5b50600160a060020a0381358116916020013516610561565b34801561029957600080fd5b5061019f600480360360208110156102b057600080fd5b5035600160a060020a031661057e565b3480156102cc57600080fd5b506100c8610599565b3480156102e157600080fd5b50610176600480360360408110156102f857600080fd5b50600160a060020a0381351690602001356105f4565b34801561031a57600080fd5b5061019f6004803603604081101561033157600080fd5b50600160a060020a038135811691602001351661067c565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156103cf5780601f106103a4576101008083540402835291602001916103cf565b820191906000526020600020905b8154815290600101906020018083116103b257829003601f168201915b505050505081565b336000818152600160209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b60025481565b600160a060020a03831660008181526001602090815260408083203384528252808320549383529082905281205490919083118015906104835750828110155b151561048e57600080fd5b600160a060020a03808516600090815260208190526040808220805487019055918716815220805484900390556000198110156104f057600160a060020a03851660009081526001602090815260408083203384529091529020805484900390555b83600160a060020a031685600160a060020a03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef856040518082815260200191505060405180910390a3506001949350505050565b60006020819052908152604090205481565b60045460ff1681565b600160209081526000928352604080842090915290825290205481565b600160a060020a031660009081526020819052604090205490565b6005805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156103cf5780601f106103a4576101008083540402835291602001916103cf565b3360009081526020819052604081205482111561061057600080fd5b3360008181526020818152604080832080548790039055600160a060020a03871680845292819020805487019055805186815290519293927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a350600192915050565b600160a060020a0391821660009081526001602090815260408083209390941682529190915220549056fea165627a7a72305820afa4e87eb9fd6f4af19a3b6a206a71efc4b7502a1d1d521be39f31722f178f630029";
    public static Function name() {
        return new Function(
            "name",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.Utf8String>() {}
            )
        );
    }
    public static NameReturnValue query_name(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        NameReturnValue returnValue = new NameReturnValue();
        returnValue.value = (String) values.get(0).getValue();
        return returnValue;
    }
    public static Function approve(String _spender, BigInteger _value) {
        return new Function(
            "approve",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(_spender)
                , new org.web3j.abi.datatypes.generated.Uint256(_value)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.Bool>() {}
            )
        );
    }
    public static Function totalSupply() {
        return new Function(
            "totalSupply",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static TotalsupplyReturnValue query_totalSupply(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        TotalsupplyReturnValue returnValue = new TotalsupplyReturnValue();
        returnValue.value = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function transferFrom(String _from, String _to, BigInteger _value) {
        return new Function(
            "transferFrom",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(_from)
                , new org.web3j.abi.datatypes.Address(_to)
                , new org.web3j.abi.datatypes.generated.Uint256(_value)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.Bool>() {}
            )
        );
    }
    public static Function balances(String arg0) {
        return new Function(
            "balances",
            Collections.singletonList(
                new org.web3j.abi.datatypes.Address(arg0)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static BalancesReturnValue query_balances(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        BalancesReturnValue returnValue = new BalancesReturnValue();
        returnValue.value = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function decimals() {
        return new Function(
            "decimals",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint8>() {}
            )
        );
    }
    public static DecimalsReturnValue query_decimals(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        DecimalsReturnValue returnValue = new DecimalsReturnValue();
        returnValue.value = ((BigInteger) values.get(0).getValue()).intValue();
        return returnValue;
    }
    public static Function allowed(String arg0, String arg1) {
        return new Function(
            "allowed",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(arg0)
                , new org.web3j.abi.datatypes.Address(arg1)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static AllowedReturnValue query_allowed(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        AllowedReturnValue returnValue = new AllowedReturnValue();
        returnValue.value = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function balanceOf(String _owner) {
        return new Function(
            "balanceOf",
            Collections.singletonList(
                new org.web3j.abi.datatypes.Address(_owner)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static BalanceofReturnValue query_balanceOf(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        BalanceofReturnValue returnValue = new BalanceofReturnValue();
        returnValue.balance = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static Function symbol() {
        return new Function(
            "symbol",
            Collections.emptyList(),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.Utf8String>() {}
            )
        );
    }
    public static SymbolReturnValue query_symbol(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        SymbolReturnValue returnValue = new SymbolReturnValue();
        returnValue.value = (String) values.get(0).getValue();
        return returnValue;
    }
    public static Function transfer(String _to, BigInteger _value) {
        return new Function(
            "transfer",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(_to)
                , new org.web3j.abi.datatypes.generated.Uint256(_value)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.Bool>() {}
            )
        );
    }
    public static Function allowance(String _owner, String _spender) {
        return new Function(
            "allowance",
            Arrays.asList(
                new org.web3j.abi.datatypes.Address(_owner)
                , new org.web3j.abi.datatypes.Address(_spender)
            ),
            Collections.singletonList(
                new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
            )
        );
    }
    public static AllowanceReturnValue query_allowance(String contractAddress, Web3j web3j, Function function) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction("", contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        String value = ethCall.getValue();
        List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        AllowanceReturnValue returnValue = new AllowanceReturnValue();
        returnValue.remaining = (BigInteger) values.get(0).getValue();
        return returnValue;
    }
    public static String DeployData(BigInteger _initialAmount, String _tokenName, int _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
            Arrays.asList(
                new org.web3j.abi.datatypes.generated.Uint256(_initialAmount)
                , new org.web3j.abi.datatypes.Utf8String(_tokenName)
                , new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits)
                , new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)
            )
        );
        return BINARY + encodedConstructor;
    }
    public static class Transfer {
        public String _from;
        public String _to;
        public BigInteger _value;
    }
    public static final Event Transfer_EVENT = new Event("Transfer",
        Arrays.asList(
            new TypeReference<org.web3j.abi.datatypes.Address>() {}
            , new TypeReference<org.web3j.abi.datatypes.Address>() {}
            , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
        )
    );
    public static final String Transfer_EVENT_HASH = EventEncoder.encode(Transfer_EVENT);
    public static Transfer ExtractTransfer(Log log) {
        List<String> topics = log.getTopics();
        if (topics.size() == 0 || !Transfer_EVENT_HASH.equals(topics.get(0))) {
            return null;
        }
        EventValues values = Contract.staticExtractEventParameters(Transfer_EVENT, log);
        Transfer event = new Transfer();
        event._from = (String) values.getNonIndexedValues().get(0).getValue();
        event._to = (String) values.getNonIndexedValues().get(1).getValue();
        event._value = (BigInteger) values.getNonIndexedValues().get(2).getValue();
        return event;
    }
    public static class Approval {
        public String _owner;
        public String _spender;
        public BigInteger _value;
    }
    public static final Event Approval_EVENT = new Event("Approval",
        Arrays.asList(
            new TypeReference<org.web3j.abi.datatypes.Address>() {}
            , new TypeReference<org.web3j.abi.datatypes.Address>() {}
            , new TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}
        )
    );
    public static final String Approval_EVENT_HASH = EventEncoder.encode(Approval_EVENT);
    public static Approval ExtractApproval(Log log) {
        List<String> topics = log.getTopics();
        if (topics.size() == 0 || !Approval_EVENT_HASH.equals(topics.get(0))) {
            return null;
        }
        EventValues values = Contract.staticExtractEventParameters(Approval_EVENT, log);
        Approval event = new Approval();
        event._owner = (String) values.getNonIndexedValues().get(0).getValue();
        event._spender = (String) values.getNonIndexedValues().get(1).getValue();
        event._value = (BigInteger) values.getNonIndexedValues().get(2).getValue();
        return event;
    }
    public static class NameReturnValue {
        public String value;
    }
    public static class TotalsupplyReturnValue {
        public BigInteger value;
    }
    public static class BalancesReturnValue {
        public BigInteger value;
    }
    public static class DecimalsReturnValue {
        public int value;
    }
    public static class AllowedReturnValue {
        public BigInteger value;
    }
    public static class BalanceofReturnValue {
        public BigInteger balance;
    }
    public static class SymbolReturnValue {
        public String value;
    }
    public static class AllowanceReturnValue {
        public BigInteger remaining;
    }
}
