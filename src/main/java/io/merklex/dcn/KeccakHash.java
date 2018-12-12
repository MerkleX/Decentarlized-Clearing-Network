package io.merklex.dcn;

import org.bouncycastle.jcajce.provider.digest.Keccak;

public class KeccakHash {
    public static byte[] Hash(byte[] data) {
        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        kecc.update(data);
        return kecc.digest();
    }

    public static byte[] Hash(byte[]... data) {
        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        for (byte[] datum : data) {
            kecc.update(datum);
        }
        return kecc.digest();
    }

    public static byte[] HashEach(byte[]... data) {
        byte[] total = new byte[data.length * 32];

        int offset = 0;
        for (byte[] item : data) {
            byte[] hash = Hash(item);
            System.arraycopy(hash, 0, total, offset, 32);
            offset += 32;
        }

        return Hash(total);
    }
}
