package io.merklex.dcn;

import org.web3j.utils.Numeric;

public class DCNConstants {
    public static void main(String[] args) {
        System.out.println("#define DCN_HEADER_HASH " + Numeric.toHexString(DCNHasher.instance.hash));
        System.out.println("#define UPDATE_LIMIT_TYPE_HASH " + Numeric.toHexString(UpdateLimits.TYPE_HASH));
    }
}
