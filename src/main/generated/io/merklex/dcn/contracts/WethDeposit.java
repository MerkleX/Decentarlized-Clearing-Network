package io.merklex.dcn.contracts;

import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameter;
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
public class WethDeposit {
    public static final String BINARY = "608060405234801561001057600080fd5b506100196100b2565b7f095ea7b30000000000000000000000000000000000000000000000000000000081527384f6451efe944ba67bedb8e0cf996fa1feb4031d60048201527fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff6024820152600080604483600073c02aaa39b223fe8d0a0e5c4f27ead9083c756cc25af1806100ab5760016020526001603ffd5b50506100d4565b6040518060600160405280600390602082028038833980820191505090505090565b6101ce806100e36000396000f3fe60806040526004361061001e5760003560e01c806354cd400914610023575b600080fd5b6100696004803603604081101561003957600080fd5b81019080803567ffffffffffffffff169060200190929190803563ffffffff16906020019092919050505061006b565b005b61007361015e565b61007b610180565b6402540be4003406156100935760016020526001603ffd5b7fd0e30db00000000000000000000000000000000000000000000000000000000082526000816004843473c02aaa39b223fe8d0a0e5c4f27ead9083c756cc25af1806100e45760026020526001603ffd5b507f054060bb000000000000000000000000000000000000000000000000000000008252836004830152826024830152600060448301526402540be4003404606483015260008160848460007384f6451efe944ba67bedb8e0cf996fa1feb4031d5af1806101575760036020526001603ffd5b5050505050565b6040518060a00160405280600590602082028038833980820191505090505090565b604051806020016040528060019060208202803883398082019150509050509056fea165627a7a72305820023bf8ce1e8d0d106b33e49e8a8c9f0103e8d00262a7b08e0eaf0ce22cdcfb700029";
    public static Function deposit(long user_id, int exchange_id) {
        return new Function(
            "deposit",
            Arrays.asList(
                new UnsignedNumberType(64, user_id)
                , new UnsignedNumberType(32, exchange_id)
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
}
