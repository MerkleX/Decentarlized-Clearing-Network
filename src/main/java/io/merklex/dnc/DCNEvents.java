package io.merklex.dnc;

import io.merklex.dcn.contracts.DCN;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DCNEvents {
    public static List<DCN.SessionStartedEventResponse> ExtractSessionStartedEvents(TransactionReceipt tx) {
        List<Log> logs = tx.getLogs();

        String topicFilter = EventEncoder.encode(DCN.SESSIONSTARTED_EVENT);

        return logs.stream().filter(log -> {
            List<String> topics = log.getTopics();
            return topics.size() > 0 && topicFilter.equals(topics.get(0));
        }).map(log -> {
            EventValues eventValues = Contract.staticExtractEventParameters(DCN.SESSIONSTARTED_EVENT, log);

            DCN.SessionStartedEventResponse event = new DCN.SessionStartedEventResponse();
            event.log = log;
            event.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            return event;
        }).collect(Collectors.toList());
    }

    public static List<DCN.PositionAddedEventResponse> ExtractPositionAddedEvents(TransactionReceipt tx) {
        List<Log> logs = tx.getLogs();

        String topicFilter = EventEncoder.encode(DCN.POSITIONADDED_EVENT);

        return logs.stream().filter(log -> {
            List<String> topics = log.getTopics();
            return topics.size() > 0 && topicFilter.equals(topics.get(0));
        }).map(log -> {
            EventValues eventValues = Contract.staticExtractEventParameters(DCN.POSITIONADDED_EVENT, log);

            DCN.PositionAddedEventResponse event = new DCN.PositionAddedEventResponse();
            event.log = log;
            event.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            return event;
        }).collect(Collectors.toList());
    }

    public static CompletableFuture<List<DCN.SessionStartedEventResponse>>
    LoadSessionStartedEvents(Web3j web3j, String contractAddress, long firstBlock, long lastBlock) {
        EthFilter filter = new EthFilter(
                new DefaultBlockParameterNumber(firstBlock),
                new DefaultBlockParameterNumber(lastBlock),
                contractAddress
        );
        filter.addSingleTopic(EventEncoder.encode(DCN.SESSIONSTARTED_EVENT));

        return web3j.ethGetLogs(filter).sendAsync().thenApply(logs -> {
            ArrayList<DCN.SessionStartedEventResponse> events = new ArrayList<>();

            for (EthLog.LogResult logResult : logs.getLogs()) {
                Log log = (Log) logResult;
                EventValues eventValues = Contract.staticExtractEventParameters(DCN.SESSIONSTARTED_EVENT, log);

                DCN.SessionStartedEventResponse event = new DCN.SessionStartedEventResponse();
                event.log = log;
                event.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                events.add(event);
            }

            return events;
        });
    }

    public static CompletableFuture<List<DCN.PositionAddedEventResponse>>
    LoadPositionAddedEvents(Web3j web3j, String contractAddress, long firstBlock, long lastBlock) {
        EthFilter filter = new EthFilter(
                new DefaultBlockParameterNumber(firstBlock),
                new DefaultBlockParameterNumber(lastBlock),
                contractAddress
        );
        filter.addSingleTopic(EventEncoder.encode(DCN.POSITIONADDED_EVENT));

        return web3j.ethGetLogs(filter).sendAsync().thenApply(logs -> {
            ArrayList<DCN.PositionAddedEventResponse> events = new ArrayList<>();

            for (EthLog.LogResult logResult : logs.getLogs()) {
                Log log = (Log) logResult;
                EventValues eventValues = Contract.staticExtractEventParameters(DCN.POSITIONADDED_EVENT, log);

                DCN.PositionAddedEventResponse event = new DCN.PositionAddedEventResponse();
                event.log = log;
                event.session_id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                events.add(event);
            }

            return events;
        });
    }
}
