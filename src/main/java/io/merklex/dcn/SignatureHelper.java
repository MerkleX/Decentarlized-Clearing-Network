package io.merklex.dcn;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;

import static org.web3j.crypto.Sign.recoverFromSignature;
import static org.web3j.utils.Assertions.verifyPrecondition;

public class SignatureHelper {
    public static boolean Verify(byte[] hash, String signatureHex, String publicKeyHex) {
        try {
            String recovered = RecoverKey(hash, Parse(signatureHex));
            return recovered.equals(Numeric.prependHexPrefix(publicKeyHex));
        } catch (Exception e) {
            return false;
        }
    }

    public static Sign.SignatureData Parse(String signatureHex) {
        byte[] signature = Numeric.hexStringToByteArray(signatureHex);
        if (signature.length != 65) {
            throw new IllegalArgumentException("Signature must be 65 bytes");
        }

        byte v = signature[64];
        if (v < 27) {
            v += 27;
        }

        byte[] r = new byte[32];
        byte[] s = new byte[32];

        System.arraycopy(signature, 0, r, 0, 32);
        System.arraycopy(signature, 32, s, 0, 32);

        return new Sign.SignatureData(v, r, s);
    }




    public static String RecoverKey(
            byte[] messageHash, Sign.SignatureData signatureData) throws SignatureException {

        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        verifyPrecondition(r != null && r.length == 32, "r must be 32 bytes");
        verifyPrecondition(s != null && s.length == 32, "s must be 32 bytes");

        int header = signatureData.getV() & 0xFF;
        // The header byte: 0x1B = first key with even y, 0x1C = first key with odd y,
        //                  0x1D = second key with even y, 0x1E = second key with odd y
        if (header < 27 || header > 34) {
            throw new SignatureException("Header byte out of range: " + header);
        }

        ECDSASignature sig = new ECDSASignature(
                new BigInteger(1, signatureData.getR()),
                new BigInteger(1, signatureData.getS()));

        int recId = header - 27;
        BigInteger key = recoverFromSignature(recId, sig, messageHash);
        if (key == null) {
            throw new SignatureException("Could not recover public key from signature");
        }
        return "0x" + Keys.getAddress(key);
    }

}
