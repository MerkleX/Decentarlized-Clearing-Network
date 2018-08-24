package io.merklex.settlement.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.merklex.settlement.contracts.MerkleX;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.ipc.UnixIpcService;

import java.io.*;
import java.math.BigInteger;

public class EtherDevNet implements Closeable {
    private final Process geth;
    private final File networkDir;
    private final String ipcPath;

    private final Web3j web3j;

    private final StringBuilder logs = new StringBuilder();

    public EtherDevNet() throws IOException, InterruptedException {
        networkDir = Utils.TempDir();
        ipcPath = Utils.TempDir().getAbsolutePath();

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

        new Thread(() -> {
            InputStream stream = geth.getInputStream();
            byte[] buffer = new byte[100];
            while (geth.isAlive()) {
                try {
                    int read = stream.read(buffer);
                    if (read > 0) {
                        System.out.print(new String(buffer, 0, read));
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }).start();

        Thread.sleep(5000);
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
            System.out.println("SETUP");
            RemoteCall<MerkleX> merkle = MerkleX.deploy(net.web3j, Keys.get("merkle"), BigInteger.ONE, BigInteger.valueOf(3000000));
            MerkleX merkleXContract = merkle.send();
            System.out.println("DEPLOYED");
        }
    }

    @Override
    public void close() {
        System.out.println("STOP");
        geth.destroy();
        try {
            System.out.println("WAIT");
            geth.waitFor();
        } catch (InterruptedException e) {
            geth.destroyForcibly();
        }
        System.out.println("DELTE");
        Utils.DeleteDir(networkDir);
    }
}
