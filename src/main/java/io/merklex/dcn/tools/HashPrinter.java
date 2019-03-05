package io.merklex.dcn.tools;

import io.merklex.dcn.DCNHasher;
import io.merklex.dcn.UpdateLimits;
import org.web3j.utils.Numeric;

public class HashPrinter {
    public static void main(String[] args) {
        System.out.println("UpdateLimit TYPE Hash: " + Numeric.toHexString(UpdateLimits.TYPE_HASH));
        System.out.println("DCN Hash: " + Numeric.toHexString(DCNHasher.instance.hash));
    }
}
