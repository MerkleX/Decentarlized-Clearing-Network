package io.merklex.dcn.utils.ether_net;

import io.merklex.web3.Utils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.ipc.UnixIpcService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EtherPrivateNet implements Web3Provider {
    private final Process geth;
    private final File networkDir;

    private final Web3j web3j;

    private final StringBuilder logs = new StringBuilder();

    public EtherPrivateNet(int rpcPort, String rpcHost, Map<String, String> initialBalances, long blockGasLimit, int networkId) throws IOException, InterruptedException {
        networkDir = Utils.TempDir();
        String ipcPath = Utils.TempDir().getAbsolutePath();

        File genesis = File.createTempFile("genesis", ".json");
        genesis.deleteOnExit();

        try (FileWriter writer = new FileWriter(genesis)) {
            writer.write("{\n" +
                    "  \"config\": {\n" +
                    "    \"chainId\": 15,\n" +
                    "    \"homesteadBlock\": 0,\n" +
                    "    \"eip155Block\": 0,\n" +
                    "    \"eip158Block\": 0\n" +
                    "  },\n" +
                    "  \"difficulty\": \"1\",\n" +
                    "  \"gasLimit\": \"" + blockGasLimit + "\",\n" +
                    "  \"alloc\": {");

            int count = initialBalances.size();
            for (String privateKey : initialBalances.keySet()) {
                Credentials credentials = Credentials.create(privateKey);
                writer.write("\n\"" + credentials.getAddress() + "\": {");
                writer.write("\n\"balance\": \"" + initialBalances.get(privateKey) + "\"");
                writer.write("\n}");
                if (--count > 0) {
                    writer.write(",");
                }
            }

            writer.write("\n  }\n}");
        }

        ProcessBuilder setupNetwork = new ProcessBuilder("geth",
                "--datadir", networkDir.getAbsolutePath(),
                "init", genesis.getAbsolutePath()
        );

        Process setupNetworkProcess = setupNetwork.start();
        if (setupNetworkProcess.waitFor() != 0) {
            throw new RuntimeException("Failed to init genesis block: " + Utils.ReadAll(setupNetworkProcess.getErrorStream()));
        }

        ArrayList<String> commandParts = new ArrayList<>(Arrays.asList(
                "geth",
                "--ipcpath", ipcPath,
                "--mine", "--minerthreads=1",
                "--targetgaslimit", String.valueOf(blockGasLimit),
                "--datadir", networkDir.getAbsolutePath(),
                "--networkid", String.valueOf(networkId),
                "--etherbase", "0x09332b1e45e6172fb26e46b3db4411201547560a"
        ));

        if (rpcPort > 0) {
            commandParts.add("--rpc");
            commandParts.add("--rpcport");
            commandParts.add(String.valueOf(rpcPort));
            commandParts.add("--rpcaddr");
            commandParts.add(rpcHost);
        }

        geth = new ProcessBuilder(commandParts).redirectErrorStream(true).start();

        AtomicBoolean doneWaiting = new AtomicBoolean(false);

        new Thread(() -> {
            InputStream stream = geth.getInputStream();
            byte[] buffer = new byte[100];
            while (geth.isAlive()) {
                try {
                    int read = stream.read(buffer);
                    if (read > 0) {
                        logs.append(new String(buffer, 0, read));

                        if (!doneWaiting.get()) {
                            if (logs.toString().contains("IPC endpoint opened")) {
                                doneWaiting.set(true);
                            }
                        }
                    }
                } catch (IOException e) {
                    doneWaiting.set(true);
                    break;
                }
            }
        }).start();

        do {
            Thread.sleep(100);
        } while (!doneWaiting.get() && geth.isAlive());

        if (!geth.isAlive()) {
            throw new RuntimeException("Failed to start geth");
        }

        web3j = Web3j.build(new UnixIpcService(ipcPath));
    }

    public Web3j web3() {
        return web3j;
    }

    @Override
    public void close() {
        geth.destroy();
        try {
            geth.waitFor();
        } catch (InterruptedException e) {
            geth.destroyForcibly();
        }
        Utils.DeleteDir(networkDir);
    }
}
