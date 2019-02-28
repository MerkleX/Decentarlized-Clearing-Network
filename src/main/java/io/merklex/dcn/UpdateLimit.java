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
    public UpdateLimit dcnId(int value) {
        return (UpdateLimit) super.dcnId(value);
    }

    @Override
    public UpdateLimit userId(long value) {
        return (UpdateLimit) super.userId(value);
    }

    @Override
    public UpdateLimit exchangeId(int value) {
        return (UpdateLimit) super.exchangeId(value);
    }

    @Override
    public UpdateLimit quoteAssetId(int value) {
        return (UpdateLimit) super.quoteAssetId(value);
    }

    @Override
    public UpdateLimit baseAssetId(int value) {
        return (UpdateLimit) super.baseAssetId(value);
    }

    @Override
    public UpdateLimit feeLimit(long value) {
        return (UpdateLimit) super.feeLimit(value);
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
    public UpdateLimit longMaxPrice(long value) {
        return (UpdateLimit) super.longMaxPrice(value);
    }

    @Override
    public UpdateLimit shortMinPrice(long value) {
        return (UpdateLimit) super.shortMinPrice(value);
    }

    @Override
    public UpdateLimit limitVersion(long value) {
        return (UpdateLimit) super.limitVersion(value);
    }

    @Override
    public UpdateLimit quoteShiftMajor(int value) {
        return (UpdateLimit) super.quoteShiftMajor(value);
    }

    @Override
    public UpdateLimit quoteShift(long value) {
        return (UpdateLimit) super.quoteShift(value);
    }

    @Override
    public UpdateLimit baseShiftMajor(int value) {
        return (UpdateLimit) super.baseShiftMajor(value);
    }

    @Override
    public UpdateLimit baseShift(long value) {
        return (UpdateLimit) super.baseShift(value);
    }

    private static final byte[] TYPE_HASH = KeccakHash.Hash(("UpdateLimit(" +
            "uint32 dcn_id," +
            "uint64 user_id," +
            "uint32 exchange_id," +
            "uint32 quote_asset_id," +
            "uint32 base_asset_id," +
            "uint64 fee_limit," +

            "int64 min_quote_qty," +
            "int64 min_base_qty," +
            "uint64 long_max_price," +
            "uint64 short_min_price," +

            "uint64 limit_version," +
            "int96 quote_shift," +
            "int96 base_shift" +
            ")").getBytes()
    );

//    public static void main(String[] args) {
//        System.out.println(Numeric.toHexString(TYPE_HASH));
//    }

    public byte[] hash() {
        byte[] bytes = new byte[13 * 32];

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        SolidityBuffers.putUInt32(buffer, dcnId());
        SolidityBuffers.putUInt64(buffer, userId());
        SolidityBuffers.putUInt32(buffer, exchangeId());
        SolidityBuffers.putUInt32(buffer, quoteAssetId());
        SolidityBuffers.putUInt32(buffer, baseAssetId());
        SolidityBuffers.putUInt64(buffer, feeLimit());

        SolidityBuffers.putInt64(buffer, minQuoteQty());
        SolidityBuffers.putInt64(buffer, minBaseQty());
        SolidityBuffers.putUInt64(buffer, longMaxPrice());
        SolidityBuffers.putUInt64(buffer, shortMinPrice());

        SolidityBuffers.putUInt64(buffer, limitVersion());
        SolidityBuffers.putInt96(buffer, quoteShiftMajor(), quoteShift());
        SolidityBuffers.putInt96(buffer, baseShiftMajor(), baseShift());

        buffer.flip();

        return KeccakHash.Hash(
                TYPE_HASH,
                bytes
        );
    }

}
