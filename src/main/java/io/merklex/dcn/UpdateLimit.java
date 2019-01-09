package io.merklex.dcn;

import io.merklex.dcn.models.UpdateLimitMessage;
import org.agrona.MutableDirectBuffer;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UpdateLimit extends UpdateLimitMessage.UpdateLimit {
    @Override
    public UpdateLimit wrap(MutableDirectBuffer buffer, int offset) {
        return (UpdateLimit) super.wrap(buffer, offset);
    }

    public UpdateLimit user(String userHex) {
        byte[] userAddress = Numeric.hexStringToByteArray(userHex);
        super.setUserAddress(userAddress);

        return this;
    }

    public UpdateLimit signature(String signatureHex) {
        byte[] signature = Numeric.hexStringToByteArray(signatureHex);

        byte v = signature[64];
        if (v < 27) {
            v += 27;
        }

        super.setSigR(signature, 0);
        super.setSigS(signature, 32);
        super.sigV(v);

        return this;
    }

    public UpdateLimit signature(Sign.SignatureData sig) {
        byte v = sig.getV();
        if (v < 27) {
            v += 27;
        }

        if (v != 27 && v != 28) {
            throw new IllegalArgumentException("Wrong version number must be 0, 1, 27, or 28");
        }

        super.setSigR(sig.getR());
        super.setSigS(sig.getS());
        super.sigV(v);

        return this;
    }

    @Override
    public UpdateLimit exchangeId(int value) {
        return (UpdateLimit) super.exchangeId(value);
    }

    @Override
    public UpdateLimit version(long value) {
        return (UpdateLimit) super.version(value);
    }

    @Override
    public UpdateLimit assetId(int value) {
        return (UpdateLimit) super.assetId(value);
    }

    @Override
    public UpdateLimit maxLongPrice(long value) {
        return (UpdateLimit) super.maxLongPrice(value);
    }

    @Override
    public UpdateLimit minShortPrice(long value) {
        return (UpdateLimit) super.minShortPrice(value);
    }

    @Override
    public UpdateLimit minQuoteQty(long value) {
        return (UpdateLimit) super.minQuoteQty(value);
    }

    @Override
    public UpdateLimit minBaseQty(long value) {
        return (UpdateLimit) super.minBaseQty(value);
    }

    @Override
    public UpdateLimit quoteShift(long value) {
        return (UpdateLimit) super.quoteShift(value);
    }

    @Override
    public UpdateLimit baseShift(long value) {
        return (UpdateLimit) super.baseShift(value);
    }

    private static final byte[] TYPE_HASH = KeccakHash.Hash(("UpdateLimit(" +
            "uint32 exchange_id," +
            "uint32 asset_id," +
            "uint64 version," +
            "uint64 max_long_price," +
            "uint64 min_short_price," +
            "int64 min_quote_qty," +
            "int64 min_base_qty," +
            "int64 quote_shift," +
            "int64 base_shift" +
            ")").getBytes()
    );

//    public static void main(String[] args) {
//        System.out.println(Numeric.toHexString(TYPE_HASH));
//    }

    public byte[] hash() {
        byte[] bytes = new byte[9 * 32];

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        SolidityBuffers.putUInt32(buffer, exchangeId());
        SolidityBuffers.putUInt32(buffer, assetId());
        SolidityBuffers.putUInt64(buffer, version());
        SolidityBuffers.putUInt64(buffer, maxLongPrice());
        SolidityBuffers.putUInt64(buffer, minShortPrice());
        SolidityBuffers.putInt64(buffer, minQuoteQty());
        SolidityBuffers.putInt64(buffer, minBaseQty());
        SolidityBuffers.putInt64(buffer, quoteShift());
        SolidityBuffers.putInt64(buffer, baseShift());
        buffer.flip();

        return KeccakHash.Hash(
                TYPE_HASH,
                bytes
        );
    }

}
