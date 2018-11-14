package io.merklex.web3;

import org.web3j.abi.datatypes.Function;
import org.web3j.protocol.Web3j;

import java.io.IOException;

public class QueryHelper {
    public final String contractAddress;
    public final Web3j web3j;

    public QueryHelper(String contractAddress, Web3j web3j) {
        this.contractAddress = contractAddress;
        this.web3j = web3j;
    }

    public <T> T query(QueryFn<T> query, Function fn) throws IOException {
        return query.query(contractAddress, web3j, fn);
    }

    public interface QueryFn<T> {
        T query(String contractAddress, Web3j web3j, Function function) throws IOException;
    }
}
