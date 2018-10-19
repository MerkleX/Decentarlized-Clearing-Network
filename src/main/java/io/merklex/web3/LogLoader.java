package io.merklex.web3;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LogLoader {
    public static <T> List<T> LoadEvents(Web3j web3j, long firstBlock, long lastBlock,
                                         String contractAddress, String logFilter, Function<Log, T> mapper) throws IOException {
        EthFilter filter = new EthFilter(
                new DefaultBlockParameterNumber(firstBlock),
                new DefaultBlockParameterNumber(lastBlock),
                contractAddress
        );
        filter.addSingleTopic(logFilter);

        ArrayList<T> events = new ArrayList<>();

        EthLog logs = web3j.ethGetLogs(filter).send();
        for (EthLog.LogResult logResult : logs.getLogs()) {
            Log log = (Log) logResult;
            T value = mapper.apply(log);
            if (value != null) {
                events.add(value);
            }
        }

        return events;
    }
}
