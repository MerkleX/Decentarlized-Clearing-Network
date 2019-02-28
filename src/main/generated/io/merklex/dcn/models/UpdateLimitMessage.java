package io.merklex.dcn.models;
import org.agrona.MutableDirectBuffer;
import java.util.HashMap;
import java.nio.ByteOrder;

@javax.annotation.Generated(value="merklex-code-gen")
public class UpdateLimitMessage {
    public static class Type {
        private Type() {}
    }
    public static class UpdateLimit {
        public UpdateLimit clearToZeros() {
            this.buffer.setMemory(offset, BYTES, (byte) 0);
            return this;
        }
        private MutableDirectBuffer buffer;
        private int offset;
        public MutableDirectBuffer messageMemoryBuffer() {
            return buffer;
        }
        public int messageMemoryOffset() {
            return offset;
        }
        public static final int DCN_ID_OFFSET = 0;
        public static final int DCN_ID_LENGTH = 4;
        public static final int USER_ID_OFFSET = 4;
        public static final int USER_ID_LENGTH = 8;
        public static final int EXCHANGE_ID_OFFSET = 12;
        public static final int EXCHANGE_ID_LENGTH = 4;
        public static final int QUOTE_ASSET_ID_OFFSET = 16;
        public static final int QUOTE_ASSET_ID_LENGTH = 4;
        public static final int BASE_ASSET_ID_OFFSET = 20;
        public static final int BASE_ASSET_ID_LENGTH = 4;
        public static final int FEE_LIMIT_OFFSET = 24;
        public static final int FEE_LIMIT_LENGTH = 8;
        public static final int MIN_QUOTE_QTY_OFFSET = 32;
        public static final int MIN_QUOTE_QTY_LENGTH = 8;
        public static final int MIN_BASE_QTY_OFFSET = 40;
        public static final int MIN_BASE_QTY_LENGTH = 8;
        public static final int LONG_MAX_PRICE_OFFSET = 48;
        public static final int LONG_MAX_PRICE_LENGTH = 8;
        public static final int SHORT_MIN_PRICE_OFFSET = 56;
        public static final int SHORT_MIN_PRICE_LENGTH = 8;
        public static final int LIMIT_VERSION_OFFSET = 64;
        public static final int LIMIT_VERSION_LENGTH = 8;
        public static final int QUOTE_SHIFT_MAJOR_OFFSET = 72;
        public static final int QUOTE_SHIFT_MAJOR_LENGTH = 4;
        public static final int QUOTE_SHIFT_OFFSET = 76;
        public static final int QUOTE_SHIFT_LENGTH = 8;
        public static final int BASE_SHIFT_MAJOR_OFFSET = 84;
        public static final int BASE_SHIFT_MAJOR_LENGTH = 4;
        public static final int BASE_SHIFT_OFFSET = 88;
        public static final int BASE_SHIFT_LENGTH = 8;
        public static final int SIG_R_OFFSET = 96;
        public static final int SIG_R_COUNT = 32;
        public static final int SIG_R_LENGTH = 32;
        public static final int SIG_R_ITEM_LENGTH = 1;
        public static final int SIG_S_OFFSET = 128;
        public static final int SIG_S_COUNT = 32;
        public static final int SIG_S_LENGTH = 32;
        public static final int SIG_S_ITEM_LENGTH = 1;
        public static final int SIG_V_OFFSET = 160;
        public static final int SIG_V_LENGTH = 1;
        public static final int BYTES = 161;
        public UpdateLimit wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(UpdateLimit other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public int dcnId() {
            return this.buffer.getInt(this.offset + DCN_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit dcnId(int value) {
            this.buffer.putInt(this.offset + DCN_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long userId() {
            return this.buffer.getLong(this.offset + USER_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit userId(long value) {
            this.buffer.putLong(this.offset + USER_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public int exchangeId() {
            return this.buffer.getInt(this.offset + EXCHANGE_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit exchangeId(int value) {
            this.buffer.putInt(this.offset + EXCHANGE_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public int quoteAssetId() {
            return this.buffer.getInt(this.offset + QUOTE_ASSET_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit quoteAssetId(int value) {
            this.buffer.putInt(this.offset + QUOTE_ASSET_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public int baseAssetId() {
            return this.buffer.getInt(this.offset + BASE_ASSET_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit baseAssetId(int value) {
            this.buffer.putInt(this.offset + BASE_ASSET_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long feeLimit() {
            return this.buffer.getLong(this.offset + FEE_LIMIT_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit feeLimit(long value) {
            this.buffer.putLong(this.offset + FEE_LIMIT_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long minQuoteQty() {
            return this.buffer.getLong(this.offset + MIN_QUOTE_QTY_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit minQuoteQty(long value) {
            this.buffer.putLong(this.offset + MIN_QUOTE_QTY_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long minBaseQty() {
            return this.buffer.getLong(this.offset + MIN_BASE_QTY_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit minBaseQty(long value) {
            this.buffer.putLong(this.offset + MIN_BASE_QTY_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long longMaxPrice() {
            return this.buffer.getLong(this.offset + LONG_MAX_PRICE_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit longMaxPrice(long value) {
            this.buffer.putLong(this.offset + LONG_MAX_PRICE_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long shortMinPrice() {
            return this.buffer.getLong(this.offset + SHORT_MIN_PRICE_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit shortMinPrice(long value) {
            this.buffer.putLong(this.offset + SHORT_MIN_PRICE_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long limitVersion() {
            return this.buffer.getLong(this.offset + LIMIT_VERSION_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit limitVersion(long value) {
            this.buffer.putLong(this.offset + LIMIT_VERSION_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public int quoteShiftMajor() {
            return this.buffer.getInt(this.offset + QUOTE_SHIFT_MAJOR_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit quoteShiftMajor(int value) {
            this.buffer.putInt(this.offset + QUOTE_SHIFT_MAJOR_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long quoteShift() {
            return this.buffer.getLong(this.offset + QUOTE_SHIFT_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit quoteShift(long value) {
            this.buffer.putLong(this.offset + QUOTE_SHIFT_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public int baseShiftMajor() {
            return this.buffer.getInt(this.offset + BASE_SHIFT_MAJOR_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit baseShiftMajor(int value) {
            this.buffer.putInt(this.offset + BASE_SHIFT_MAJOR_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long baseShift() {
            return this.buffer.getLong(this.offset + BASE_SHIFT_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit baseShift(long value) {
            this.buffer.putLong(this.offset + BASE_SHIFT_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public UpdateLimit sigR(int pos, byte value) {
            assert(pos >= 0 && pos < SIG_R_COUNT);
            buffer.putByte(this.offset + SIG_R_OFFSET + SIG_R_ITEM_LENGTH * pos, value);
            return this;
        }
        public byte sigR(int pos) {
            assert(pos >= 0 && pos < SIG_R_COUNT);
            return buffer.getByte(this.offset + SIG_R_OFFSET + SIG_R_ITEM_LENGTH * pos);
        }
        public UpdateLimit getSigR(byte[] value, int pos) {
            buffer.getBytes(this.offset + SIG_R_OFFSET, value, pos, 32);
            return this;
        }
        public UpdateLimit getSigR(byte[] value) {
            buffer.getBytes(this.offset + SIG_R_OFFSET, value, 0, 32);
            return this;
        }
        public UpdateLimit setSigR(byte[] value, int pos) {
            buffer.putBytes(this.offset + SIG_R_OFFSET, value, pos, 32);
            return this;
        }
        public UpdateLimit setSigR(byte[] value) {
            buffer.putBytes(this.offset + SIG_R_OFFSET, value, 0, 32);
            return this;
        }
        public UpdateLimit sigS(int pos, byte value) {
            assert(pos >= 0 && pos < SIG_S_COUNT);
            buffer.putByte(this.offset + SIG_S_OFFSET + SIG_S_ITEM_LENGTH * pos, value);
            return this;
        }
        public byte sigS(int pos) {
            assert(pos >= 0 && pos < SIG_S_COUNT);
            return buffer.getByte(this.offset + SIG_S_OFFSET + SIG_S_ITEM_LENGTH * pos);
        }
        public UpdateLimit getSigS(byte[] value, int pos) {
            buffer.getBytes(this.offset + SIG_S_OFFSET, value, pos, 32);
            return this;
        }
        public UpdateLimit getSigS(byte[] value) {
            buffer.getBytes(this.offset + SIG_S_OFFSET, value, 0, 32);
            return this;
        }
        public UpdateLimit setSigS(byte[] value, int pos) {
            buffer.putBytes(this.offset + SIG_S_OFFSET, value, pos, 32);
            return this;
        }
        public UpdateLimit setSigS(byte[] value) {
            buffer.putBytes(this.offset + SIG_S_OFFSET, value, 0, 32);
            return this;
        }
        public byte sigV() {
            return this.buffer.getByte(this.offset + SIG_V_OFFSET);
        }
        public UpdateLimit sigV(byte value) {
            this.buffer.putByte(this.offset + SIG_V_OFFSET, value);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UpdateLimit { ");
            sb.append("dcn_id: ");
            sb.append(Integer.toUnsignedString(dcnId()));
            sb.append(", user_id: ");
            sb.append(Long.toUnsignedString(userId()));
            sb.append(", exchange_id: ");
            sb.append(Integer.toUnsignedString(exchangeId()));
            sb.append(", quote_asset_id: ");
            sb.append(Integer.toUnsignedString(quoteAssetId()));
            sb.append(", base_asset_id: ");
            sb.append(Integer.toUnsignedString(baseAssetId()));
            sb.append(", fee_limit: ");
            sb.append(Long.toUnsignedString(feeLimit()));
            sb.append(", min_quote_qty: ");
            sb.append(minQuoteQty());
            sb.append(", min_base_qty: ");
            sb.append(minBaseQty());
            sb.append(", long_max_price: ");
            sb.append(Long.toUnsignedString(longMaxPrice()));
            sb.append(", short_min_price: ");
            sb.append(Long.toUnsignedString(shortMinPrice()));
            sb.append(", limit_version: ");
            sb.append(Long.toUnsignedString(limitVersion()));
            sb.append(", quote_shift_major: ");
            sb.append(Integer.toUnsignedString(quoteShiftMajor()));
            sb.append(", quote_shift: ");
            sb.append(Long.toUnsignedString(quoteShift()));
            sb.append(", base_shift_major: ");
            sb.append(Integer.toUnsignedString(baseShiftMajor()));
            sb.append(", base_shift: ");
            sb.append(Long.toUnsignedString(baseShift()));
            sb.append(", sig_r: ");
            sb.append("[").append(sigR(0)).append(", ").append(sigR(1)).append(", ").append(sigR(2)).append(", ")
                    .append(sigR(3)).append(", ").append(sigR(4)).append(", ").append(sigR(5)).append(", ")
                    .append(sigR(6)).append(", ").append(sigR(7)).append(", ").append(sigR(8)).append(", ")
                    .append(sigR(9)).append(", ").append(sigR(10)).append(", ").append(sigR(11)).append(", ")
                    .append(sigR(12)).append(", ").append(sigR(13)).append(", ").append(sigR(14)).append(", ")
                    .append(sigR(15)).append(", ").append(sigR(16)).append(", ").append(sigR(17)).append(", ")
                    .append(sigR(18)).append(", ").append(sigR(19)).append(", ").append(sigR(20)).append(", ")
                    .append(sigR(21)).append(", ").append(sigR(22)).append(", ").append(sigR(23)).append(", ")
                    .append(sigR(24)).append(", ").append(sigR(25)).append(", ").append(sigR(26)).append(", ")
                    .append(sigR(27)).append(", ").append(sigR(28)).append(", ").append(sigR(29)).append(", ")
                    .append(sigR(30)).append(", ").append(sigR(31))
                    .append("]");
            sb.append(", sig_s: ");
            sb.append("[").append(sigS(0)).append(", ").append(sigS(1)).append(", ").append(sigS(2)).append(", ")
                    .append(sigS(3)).append(", ").append(sigS(4)).append(", ").append(sigS(5)).append(", ")
                    .append(sigS(6)).append(", ").append(sigS(7)).append(", ").append(sigS(8)).append(", ")
                    .append(sigS(9)).append(", ").append(sigS(10)).append(", ").append(sigS(11)).append(", ")
                    .append(sigS(12)).append(", ").append(sigS(13)).append(", ").append(sigS(14)).append(", ")
                    .append(sigS(15)).append(", ").append(sigS(16)).append(", ").append(sigS(17)).append(", ")
                    .append(sigS(18)).append(", ").append(sigS(19)).append(", ").append(sigS(20)).append(", ")
                    .append(sigS(21)).append(", ").append(sigS(22)).append(", ").append(sigS(23)).append(", ")
                    .append(sigS(24)).append(", ").append(sigS(25)).append(", ").append(sigS(26)).append(", ")
                    .append(sigS(27)).append(", ").append(sigS(28)).append(", ").append(sigS(29)).append(", ")
                    .append(sigS(30)).append(", ").append(sigS(31))
                    .append("]");
            sb.append(", sig_v: ");
            sb.append(Byte.toUnsignedInt(sigV()));
            sb.append(" }");
            return sb.toString();
        }
    }
}
