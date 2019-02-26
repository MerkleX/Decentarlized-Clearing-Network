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
        public static final int DCN_ID_COUNT = 96;
        public static final int DCN_ID_LENGTH = 96;
        public static final int DCN_ID_ITEM_LENGTH = 1;
        public static final int EXCHANGE_ID_OFFSET = 96;
        public static final int EXCHANGE_ID_LENGTH = 4;
        public static final int QUOTE_ASSET_ID_OFFSET = 100;
        public static final int QUOTE_ASSET_ID_LENGTH = 4;
        public static final int BASE_ASSET_ID_OFFSET = 104;
        public static final int BASE_ASSET_ID_LENGTH = 4;
        public static final int FEE_LIMIT_OFFSET = 108;
        public static final int FEE_LIMIT_LENGTH = 8;
        public static final int MIN_QUOTE_QTY_OFFSET = 116;
        public static final int MIN_QUOTE_QTY_LENGTH = 8;
        public static final int MIN_BASE_QTY_OFFSET = 124;
        public static final int MIN_BASE_QTY_LENGTH = 8;
        public static final int MAX_LONG_PRICE_OFFSET = 132;
        public static final int MAX_LONG_PRICE_LENGTH = 8;
        public static final int MIN_SHORT_PRICE_OFFSET = 140;
        public static final int MIN_SHORT_PRICE_LENGTH = 8;
        public static final int VERSION_OFFSET = 148;
        public static final int VERSION_LENGTH = 8;
        public static final int QUOTE_SHIFT_MAJOR_OFFSET = 156;
        public static final int QUOTE_SHIFT_MAJOR_LENGTH = 4;
        public static final int QUOTE_SHIFT_OFFSET = 160;
        public static final int QUOTE_SHIFT_LENGTH = 8;
        public static final int BASE_SHIFT_MAJOR_OFFSET = 168;
        public static final int BASE_SHIFT_MAJOR_LENGTH = 4;
        public static final int BASE_SHIFT_OFFSET = 172;
        public static final int BASE_SHIFT_LENGTH = 8;
        public static final int USER_ADDRESS_OFFSET = 180;
        public static final int USER_ADDRESS_COUNT = 20;
        public static final int USER_ADDRESS_LENGTH = 20;
        public static final int USER_ADDRESS_ITEM_LENGTH = 1;
        public static final int SIG_R_OFFSET = 200;
        public static final int SIG_R_COUNT = 32;
        public static final int SIG_R_LENGTH = 32;
        public static final int SIG_R_ITEM_LENGTH = 1;
        public static final int SIG_S_OFFSET = 232;
        public static final int SIG_S_COUNT = 32;
        public static final int SIG_S_LENGTH = 32;
        public static final int SIG_S_ITEM_LENGTH = 1;
        public static final int SIG_V_OFFSET = 264;
        public static final int SIG_V_LENGTH = 1;
        public static final int BYTES = 265;
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
        public UpdateLimit dcnId(int pos, byte value) {
            assert(pos >= 0 && pos < DCN_ID_COUNT);
            buffer.putByte(this.offset + DCN_ID_OFFSET + DCN_ID_ITEM_LENGTH * pos, value);
            return this;
        }
        public byte dcnId(int pos) {
            assert(pos >= 0 && pos < DCN_ID_COUNT);
            return buffer.getByte(this.offset + DCN_ID_OFFSET + DCN_ID_ITEM_LENGTH * pos);
        }
        public UpdateLimit getDcnId(byte[] value, int pos) {
            buffer.getBytes(this.offset + DCN_ID_OFFSET, value, pos, 96);
            return this;
        }
        public UpdateLimit getDcnId(byte[] value) {
            buffer.getBytes(this.offset + DCN_ID_OFFSET, value, 0, 96);
            return this;
        }
        public UpdateLimit setDcnId(byte[] value, int pos) {
            buffer.putBytes(this.offset + DCN_ID_OFFSET, value, pos, 96);
            return this;
        }
        public UpdateLimit setDcnId(byte[] value) {
            buffer.putBytes(this.offset + DCN_ID_OFFSET, value, 0, 96);
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
        public long maxLongPrice() {
            return this.buffer.getLong(this.offset + MAX_LONG_PRICE_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit maxLongPrice(long value) {
            this.buffer.putLong(this.offset + MAX_LONG_PRICE_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long minShortPrice() {
            return this.buffer.getLong(this.offset + MIN_SHORT_PRICE_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit minShortPrice(long value) {
            this.buffer.putLong(this.offset + MIN_SHORT_PRICE_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long version() {
            return this.buffer.getLong(this.offset + VERSION_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit version(long value) {
            this.buffer.putLong(this.offset + VERSION_OFFSET, value, ByteOrder.BIG_ENDIAN);
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
        public UpdateLimit userAddress(int pos, byte value) {
            assert(pos >= 0 && pos < USER_ADDRESS_COUNT);
            buffer.putByte(this.offset + USER_ADDRESS_OFFSET + USER_ADDRESS_ITEM_LENGTH * pos, value);
            return this;
        }
        public byte userAddress(int pos) {
            assert(pos >= 0 && pos < USER_ADDRESS_COUNT);
            return buffer.getByte(this.offset + USER_ADDRESS_OFFSET + USER_ADDRESS_ITEM_LENGTH * pos);
        }
        public UpdateLimit getUserAddress(byte[] value, int pos) {
            buffer.getBytes(this.offset + USER_ADDRESS_OFFSET, value, pos, 20);
            return this;
        }
        public UpdateLimit getUserAddress(byte[] value) {
            buffer.getBytes(this.offset + USER_ADDRESS_OFFSET, value, 0, 20);
            return this;
        }
        public UpdateLimit setUserAddress(byte[] value, int pos) {
            buffer.putBytes(this.offset + USER_ADDRESS_OFFSET, value, pos, 20);
            return this;
        }
        public UpdateLimit setUserAddress(byte[] value) {
            buffer.putBytes(this.offset + USER_ADDRESS_OFFSET, value, 0, 20);
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
            sb.append("[").append(dcnId(0)).append(", ").append(dcnId(1)).append(", ").append(dcnId(2)).append(", ")
                    .append(dcnId(3)).append(", ").append(dcnId(4)).append(", ").append(dcnId(5)).append(", ")
                    .append(dcnId(6)).append(", ").append(dcnId(7)).append(", ").append(dcnId(8)).append(", ")
                    .append(dcnId(9)).append(", ").append(dcnId(10)).append(", ").append(dcnId(11)).append(", ")
                    .append(dcnId(12)).append(", ").append(dcnId(13)).append(", ").append(dcnId(14)).append(", ")
                    .append(dcnId(15)).append(", ").append(dcnId(16)).append(", ").append(dcnId(17)).append(", ")
                    .append(dcnId(18)).append(", ").append(dcnId(19)).append(", ").append(dcnId(20)).append(", ")
                    .append(dcnId(21)).append(", ").append(dcnId(22)).append(", ").append(dcnId(23)).append(", ")
                    .append(dcnId(24)).append(", ").append(dcnId(25)).append(", ").append(dcnId(26)).append(", ")
                    .append(dcnId(27)).append(", ").append(dcnId(28)).append(", ").append(dcnId(29)).append(", ")
                    .append(dcnId(30)).append(", ").append(dcnId(31)).append(", ").append(dcnId(32)).append(", ")
                    .append(dcnId(33)).append(", ").append(dcnId(34)).append(", ").append(dcnId(35)).append(", ")
                    .append(dcnId(36)).append(", ").append(dcnId(37)).append(", ").append(dcnId(38)).append(", ")
                    .append(dcnId(39)).append(", ").append(dcnId(40)).append(", ").append(dcnId(41)).append(", ")
                    .append(dcnId(42)).append(", ").append(dcnId(43)).append(", ").append(dcnId(44)).append(", ")
                    .append(dcnId(45)).append(", ").append(dcnId(46)).append(", ").append(dcnId(47)).append(", ")
                    .append(dcnId(48)).append(", ").append(dcnId(49)).append(", ").append(dcnId(50)).append(", ")
                    .append(dcnId(51)).append(", ").append(dcnId(52)).append(", ").append(dcnId(53)).append(", ")
                    .append(dcnId(54)).append(", ").append(dcnId(55)).append(", ").append(dcnId(56)).append(", ")
                    .append(dcnId(57)).append(", ").append(dcnId(58)).append(", ").append(dcnId(59)).append(", ")
                    .append(dcnId(60)).append(", ").append(dcnId(61)).append(", ").append(dcnId(62)).append(", ")
                    .append(dcnId(63)).append(", ").append(dcnId(64)).append(", ").append(dcnId(65)).append(", ")
                    .append(dcnId(66)).append(", ").append(dcnId(67)).append(", ").append(dcnId(68)).append(", ")
                    .append(dcnId(69)).append(", ").append(dcnId(70)).append(", ").append(dcnId(71)).append(", ")
                    .append(dcnId(72)).append(", ").append(dcnId(73)).append(", ").append(dcnId(74)).append(", ")
                    .append(dcnId(75)).append(", ").append(dcnId(76)).append(", ").append(dcnId(77)).append(", ")
                    .append(dcnId(78)).append(", ").append(dcnId(79)).append(", ").append(dcnId(80)).append(", ")
                    .append(dcnId(81)).append(", ").append(dcnId(82)).append(", ").append(dcnId(83)).append(", ")
                    .append(dcnId(84)).append(", ").append(dcnId(85)).append(", ").append(dcnId(86)).append(", ")
                    .append(dcnId(87)).append(", ").append(dcnId(88)).append(", ").append(dcnId(89)).append(", ")
                    .append(dcnId(90)).append(", ").append(dcnId(91)).append(", ").append(dcnId(92)).append(", ")
                    .append(dcnId(93)).append(", ").append(dcnId(94)).append(", ").append(dcnId(95))
                    .append("]");
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
            sb.append(", max_long_price: ");
            sb.append(Long.toUnsignedString(maxLongPrice()));
            sb.append(", min_short_price: ");
            sb.append(Long.toUnsignedString(minShortPrice()));
            sb.append(", version: ");
            sb.append(Long.toUnsignedString(version()));
            sb.append(", quote_shift_major: ");
            sb.append(Integer.toUnsignedString(quoteShiftMajor()));
            sb.append(", quote_shift: ");
            sb.append(Long.toUnsignedString(quoteShift()));
            sb.append(", base_shift_major: ");
            sb.append(Integer.toUnsignedString(baseShiftMajor()));
            sb.append(", base_shift: ");
            sb.append(Long.toUnsignedString(baseShift()));
            sb.append(", user_address: ");
            sb.append("[").append(userAddress(0)).append(", ").append(userAddress(1)).append(", ").append(userAddress(2)).append(", ")
                    .append(userAddress(3)).append(", ").append(userAddress(4)).append(", ").append(userAddress(5)).append(", ")
                    .append(userAddress(6)).append(", ").append(userAddress(7)).append(", ").append(userAddress(8)).append(", ")
                    .append(userAddress(9)).append(", ").append(userAddress(10)).append(", ").append(userAddress(11)).append(", ")
                    .append(userAddress(12)).append(", ").append(userAddress(13)).append(", ").append(userAddress(14)).append(", ")
                    .append(userAddress(15)).append(", ").append(userAddress(16)).append(", ").append(userAddress(17)).append(", ")
                    .append(userAddress(18)).append(", ").append(userAddress(19))
                    .append("]");
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
