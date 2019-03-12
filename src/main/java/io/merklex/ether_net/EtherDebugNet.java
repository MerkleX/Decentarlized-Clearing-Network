package io.merklex.ether_net;

import io.merklex.web3.Utils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.methods.response.BooleanResponse;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EtherDebugNet implements Web3Provider {
    private final File networkDir;
    private final Process ganache;
    private final StringBuilder logs = new StringBuilder();

    private final HttpService httpServer;
    private final Web3j web3j;

    public EtherDebugNet(int rpcPort, String rpcHost, Map<String, String> initialBalances,
                         long blockGasLimit, int networkId) throws IOException, InterruptedException {
        networkDir = Utils.TempDir();
        if (!networkDir.mkdirs()) {
            throw new IOException("Failed to create data dir");
        }

        ArrayList<String> command = new ArrayList<>();
        command.add("ganache-cli");

        for (String privateKey : initialBalances.keySet()) {
            String balance = initialBalances.get(privateKey);
            command.add("--account=0x" + privateKey +
                    ",0x" + new BigInteger(balance).toString(16));
        }

        command.add("--gasLimit");
        command.add(String.valueOf(blockGasLimit));

        command.add("--db");
        command.add(networkDir.getAbsolutePath());

        command.add("--port");
        command.add(String.valueOf(rpcPort));

        command.add("--host");
        command.add(rpcHost);

        command.add("--networkId");
        command.add(String.valueOf(networkId));

        ganache = new ProcessBuilder(command).redirectErrorStream(true).start();

        AtomicBoolean ready = new AtomicBoolean(false);

        new Thread(() -> {
            InputStream stream = ganache.getInputStream();
            byte[] buffer = new byte[100];
            while (true) {
                try {
                    int read = stream.read(buffer);
                    if (read > 0) {
                        logs.append(new String(buffer, 0, read));
                    }
                    else {
                        break;
                    }

                    if (!ready.get()) {
                        if (logs.toString().contains("Listening on")) {
                            ready.set(true);
                        }
                    }
                } catch (IOException e) {
                    ready.set(true);
                    System.out.println(logs.toString());
                    e.printStackTrace();
                    break;
                }
            }
            ready.set(true);
        }).start();

        do {
            Thread.sleep(100);
        } while (!ready.get());

        if (!ganache.isAlive()) {
            int result = ganache.waitFor();
            System.out.println(result);
            System.out.println(String.join(" ", command));
            throw new RuntimeException(logs.toString());
        }

        Thread.sleep(100);
        httpServer = new HttpService("http://" + rpcHost + ":" + rpcPort);
        web3j = Web3j.build(httpServer);
    }

    public static class CheckpointResponse extends Response<String> {
        public BigInteger id() {
            return new BigInteger(getResult().substring(2), 16);
        }
    }

    public Request<Void, CheckpointResponse> checkpoint() {
        return new Request<>("evm_snapshot", Collections.emptyList(), httpServer, CheckpointResponse.class);
    }

    public Request<BigInteger, BooleanResponse> revert(BigInteger id) {
        return new Request<>("evm_revert", Collections.singletonList(id), httpServer, BooleanResponse.class);
    }

    @Override
    public Web3j web3() {
        return web3j;
    }

    @Override
    public void close() {
        ganache.destroy();
        try {
            ganache.waitFor();
        } catch (InterruptedException e) {
            ganache.destroyForcibly();
        }

        Utils.DeleteDir(networkDir);
    }
}
