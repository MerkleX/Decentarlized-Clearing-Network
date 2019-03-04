package io.merklex.dcn;

import io.merklex.dcn.models.UpdateLimitMessage;
import org.agrona.MutableDirectBuffer;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public class UpdateLimits {
    public static final byte[] TYPE_HASH = KeccakHash.Hash(("LimitUpdate(" +
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

    private MutableDirectBuffer buffer;
    private int offset;

    public UpdateLimits wrap(MutableDirectBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int exchangeId() {
        return buffer.getInt(offset);
    }

    public UpdateLimits exchangeId(int value) {
        buffer.putInt(offset, value);
        return this;
    }

    public LimitUpdate firstLimitUpdate(LimitUpdate update) {
        return update.wrap(buffer, offset + Integer.BYTES);
    }

    public String payload(int limitUpdateCount) {
        return BufferToHex.ToHex(buffer, offset, bytes(limitUpdateCount));
    }

    public int bytes(int limitUpdateCount) {
        return Integer.BYTES + LimitUpdate.BYTES * limitUpdateCount;
    }

    public static class LimitUpdate extends UpdateLimitMessage.UpdateLimit {
        @Override
        public LimitUpdate wrap(MutableDirectBuffer buffer, int offset) {
            return (LimitUpdate) super.wrap(buffer, offset);
        }

        public LimitUpdate signature(String signatureHex) {
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

        public LimitUpdate signature(Sign.SignatureData sig) {
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

        public LimitUpdate setValues(Consumer<LimitUpdate> handle) {
            handle.accept(this);
            return this;
        }

        @Override
        public LimitUpdate dcnId(int value) {
            return (LimitUpdate) super.dcnId(value);
        }

        @Override
        public LimitUpdate userId(long value) {
            return (LimitUpdate) super.userId(value);
        }

        @Override
        public LimitUpdate exchangeId(int value) {
            return (LimitUpdate) super.exchangeId(value);
        }

        @Override
        public LimitUpdate quoteAssetId(int value) {
            return (LimitUpdate) super.quoteAssetId(value);
        }

        @Override
        public LimitUpdate baseAssetId(int value) {
            return (LimitUpdate) super.baseAssetId(value);
        }

        @Override
        public LimitUpdate feeLimit(long value) {
            return (LimitUpdate) super.feeLimit(value);
        }

        @Override
        public LimitUpdate minQuoteQty(long value) {
            return (LimitUpdate) super.minQuoteQty(value);
        }

        @Override
        public LimitUpdate minBaseQty(long value) {
            return (LimitUpdate) super.minBaseQty(value);
        }

        @Override
        public LimitUpdate longMaxPrice(long value) {
            return (LimitUpdate) super.longMaxPrice(value);
        }

        @Override
        public LimitUpdate shortMinPrice(long value) {
            return (LimitUpdate) super.shortMinPrice(value);
        }

        @Override
        public LimitUpdate limitVersion(long value) {
            return (LimitUpdate) super.limitVersion(value);
        }

        public LimitUpdate quoteShift(BigInteger value) {
            int majorValue = value.shiftRight(64).intValue();
            long minorValue = value.longValue();

            if (!value.equals(BigInteger.valueOf(majorValue).shiftLeft(64).or(BigInteger.valueOf(minorValue)))) {
                throw new IllegalStateException();
            }

            quoteShiftMajor(majorValue);
            quoteShift(minorValue);
            return this;
        }

        @Override
        public LimitUpdate quoteShiftMajor(int value) {
            return (LimitUpdate) super.quoteShiftMajor(value);
        }

        @Override
        public LimitUpdate quoteShift(long value) {
            return (LimitUpdate) super.quoteShift(value);
        }

        public BigInteger quoteShiftBig() {
            return BigInteger.valueOf(super.quoteShiftMajor()).shiftLeft(64)
                    .or(BigInteger.valueOf(quoteShift()));
        }

        public LimitUpdate baseShift(BigInteger value) {
            int majorValue = value.shiftRight(64).intValue();
            long minorValue = value.longValue();

            if (value.compareTo(BigInteger.valueOf(majorValue).shiftLeft(64).or(BigInteger.valueOf(minorValue))) != 0) {
                throw new IllegalStateException();
            }

            baseShiftMajor(majorValue);
            baseShift(minorValue);
            return this;
        }

        @Override
        public LimitUpdate baseShiftMajor(int value) {
            return (LimitUpdate) super.baseShiftMajor(value);
        }

        @Override
        public LimitUpdate baseShift(long value) {
            return (LimitUpdate) super.baseShift(value);
        }

        public BigInteger baseShiftBig() {
            return BigInteger.valueOf(baseShiftMajor()).shiftLeft(64)
                    .or(BigInteger.valueOf(baseShift()));
        }

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

        public LimitUpdate nextLimitUpdate(LimitUpdate limitUpdate) {
            return limitUpdate.wrap(limitUpdate.messageMemoryBuffer(), limitUpdate.messageMemoryOffset() + BYTES);
        }

        public LimitUpdate sign(Credentials credentials, DCNHasher hasher) {
            signature(Sign.signMessage(hasher.hash(hash()), credentials.getEcKeyPair(), false));
            return this;
        }
    }
}
