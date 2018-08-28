package io.merklex.settlement.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.merklex.settlement.contracts.DCN;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.tuples.generated.Tuple3;

import java.io.*;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class EtherDevNet implements Closeable {
    private final Process geth;
    private final File networkDir;

    private final Web3j web3j;

    private final StringBuilder logs = new StringBuilder();

    public EtherDevNet() throws IOException, InterruptedException {
        networkDir = Utils.TempDir();
        String ipcPath = Utils.TempDir().getAbsolutePath();

        ProcessBuilder setupNetwork = new ProcessBuilder("geth",
                "--datadir", networkDir.getAbsolutePath(),
                "init", "src/test/resources/test_net/genesis.json"
        );

        Process setupNetworkProcess = setupNetwork.start();
        if (setupNetworkProcess.waitFor() != 0) {
            throw new RuntimeException("Failed to init genesis block: " + Utils.ReadAll(setupNetworkProcess.getErrorStream()));
        }

        JsonNode genesisSettings = new ObjectMapper()
                .readTree(new FileInputStream("src/test/resources/test_net/genesis.json"));

        geth = new ProcessBuilder("geth",
                "--ipcpath", ipcPath,
                "--mine", "--minerthreads=1",
                "--targetgaslimit", genesisSettings.get("gasLimit").asText(),
                "--datadir", networkDir.getAbsolutePath(),
                "--networkid", "15",
                "--etherbase", "0x09332b1e45e6172fb26e46b3db4411201547560a"
        ).redirectErrorStream(true).start();

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

    public static void main(String[] args) throws Exception {
        try (EtherDevNet net = new EtherDevNet()) {
            RemoteCall<DCN> merklex = DCN.deploy(net.web3(), Keys.get("merkle"), BigInteger.ONE, BigInteger.valueOf(3000000));
            DCN dcn = merklex.send();
            System.out.println("SETUP");

            for (int i = 0; i < 3; i++) {
                RemoteCall<TransactionReceipt> result = dcn.add_exchange("MerkleX     ", Keys.get("merkle").getAddress());
                TransactionReceipt send1 = result.send();

                System.out.println("Results");
                for (Log log : send1.getLogs()) {
                    System.out.println(log.getData());
                }
            }


            Tuple3<String, String, BigInteger> send = dcn.get_exchange(BigInteger.valueOf(1)).send();
//
//            System.out.println("Queried");
        }
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
