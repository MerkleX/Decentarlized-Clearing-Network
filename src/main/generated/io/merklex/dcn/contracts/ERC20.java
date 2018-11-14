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
public class ERC20 {
    public static final String BINARY = "608060405234801561001057600080fd5b506040516107843803806107848339810160409081528151602080840151838501516060860151336000908152808552959095208490556002849055908501805193959094919391019161006991600391860190610096565b506004805460ff191660ff8416179055805161008c906005906020840190610096565b5050505050610131565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100d757805160ff1916838001178555610104565b82800160010185558215610104579182015b828111156101045782518255916020019190600101906100e9565b50610110929150610114565b5090565b61012e91905b80821115610110576000815560010161011a565b90565b610644806101406000396000f3006080604052600436106100ae5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100b3578063095ea7b31461013d57806318160ddd1461017557806323b872dd1461019c57806327e235e3146101c6578063313ce567146101e75780635c6581651461021257806370a082311461023957806395d89b411461025a578063a9059cbb1461026f578063dd62ed3e14610293575b600080fd5b3480156100bf57600080fd5b506100c86102ba565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101025781810151838201526020016100ea565b50505050905090810190601f16801561012f5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561014957600080fd5b50610161600160a060020a0360043516602435610348565b604080519115158252519081900360200190f35b34801561018157600080fd5b5061018a6103ae565b60408051918252519081900360200190f35b3480156101a857600080fd5b50610161600160a060020a03600435811690602435166044356103b4565b3480156101d257600080fd5b5061018a600160a060020a03600435166104b7565b3480156101f357600080fd5b506101fc6104c9565b6040805160ff9092168252519081900360200190f35b34801561021e57600080fd5b5061018a600160a060020a03600435811690602435166104d2565b34801561024557600080fd5b5061018a600160a060020a03600435166104ef565b34801561026657600080fd5b506100c861050a565b34801561027b57600080fd5b50610161600160a060020a0360043516602435610565565b34801561029f57600080fd5b5061018a600160a060020a03600435811690602435166105ed565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156103405780601f1061031557610100808354040283529160200191610340565b820191906000526020600020905b81548152906001019060200180831161032357829003601f168201915b505050505081565b336000818152600160209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b60025481565b600160a060020a03831660008181526001602090815260408083203384528252808320549383529082905281205490919083118015906103f45750828110155b15156103ff57600080fd5b600160a060020a038085166000908152602081905260408082208054870190559187168152208054849003905560001981101561046157600160a060020a03851660009081526001602090815260408083203384529091529020805484900390555b83600160a060020a031685600160a060020a03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef856040518082815260200191505060405180910390a3506001949350505050565b60006020819052908152604090205481565b60045460ff1681565b600160209081526000928352604080842090915290825290205481565b600160a060020a031660009081526020819052604090205490565b6005805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156103405780601f1061031557610100808354040283529160200191610340565b3360009081526020819052604081205482111561058157600080fd5b3360008181526020818152604080832080548790039055600160a060020a03871680845292819020805487019055805186815290519293927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a350600192915050565b600160a060020a039182166000908152600160209081526040808320939094168252919091522054905600a165627a7a72305820f11671b0ee47268a021604c6a092778307b60f8dc1a3d8de960328b3d902677c0029";
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
