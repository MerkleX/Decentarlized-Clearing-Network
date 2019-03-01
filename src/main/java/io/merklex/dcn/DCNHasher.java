package io.merklex.dcn;

import org.web3j.utils.Numeric;

import java.nio.ByteBuffer;

public class DCNHasher {
    private static final String VERSION = "0.1";
    private static final long CHAIN_ID = 3 /* Ropsten Network */;

    private static final byte[] RAW_HEADER = new byte[]{0x19, 0x01};

    public static final DCNHasher instance = new DCNHasher(VERSION, CHAIN_ID);

    private final String version;
    private final long chainId;

    public final byte[] hash;

    public DCNHasher(String version, long chainId) {
        this.version = version;
        this.chainId = chainId;

        byte[] tailBytes = new byte[32];
        ByteBuffer tailBuffer = ByteBuffer.wrap(tailBytes);
        SolidityBuffers.putUInt64(tailBuffer, chainId);

        hash = KeccakHash.Hash(
                KeccakHash.Hash("EIP712Domain(string name,string version,uint256 chainId)".getBytes()),
                KeccakHash.Hash("DCN".getBytes()),
                KeccakHash.Hash(version.getBytes()),
                tailBytes
        );
    }

    public String version() {
        return version;
    }

    public long chainId() {
        return chainId;
    }

    public byte[] hash(byte[] data) {
        return KeccakHash.Hash(
                RAW_HEADER,
                hash,
                data
        );
    }
}
