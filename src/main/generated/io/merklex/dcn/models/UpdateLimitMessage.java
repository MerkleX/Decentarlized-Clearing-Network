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
        public static final int USER_ADDRESS_OFFSET = 0;
        public static final int USER_ADDRESS_COUNT = 20;
        public static final int USER_ADDRESS_LENGTH = 20;
        public static final int USER_ADDRESS_ITEM_LENGTH = 1;
        public static final int EXCHANGE_ID_OFFSET = 20;
        public static final int EXCHANGE_ID_LENGTH = 4;
        public static final int ASSET_ID_OFFSET = 24;
        public static final int ASSET_ID_LENGTH = 4;
        public static final int VERSION_OFFSET = 28;
        public static final int VERSION_LENGTH = 8;
        public static final int MAX_LONG_PRICE_OFFSET = 36;
        public static final int MAX_LONG_PRICE_LENGTH = 8;
        public static final int MIN_SHORT_PRICE_OFFSET = 44;
        public static final int MIN_SHORT_PRICE_LENGTH = 8;
        public static final int MIN_ETHER_QTY_OFFSET = 52;
        public static final int MIN_ETHER_QTY_LENGTH = 8;
        public static final int MIN_ASSET_QTY_OFFSET = 60;
        public static final int MIN_ASSET_QTY_LENGTH = 8;
        public static final int ETHER_SHIFT_OFFSET = 68;
        public static final int ETHER_SHIFT_LENGTH = 8;
        public static final int ASSET_SHIFT_OFFSET = 76;
        public static final int ASSET_SHIFT_LENGTH = 8;
        public static final int SIG_R_OFFSET = 84;
        public static final int SIG_R_COUNT = 32;
        public static final int SIG_R_LENGTH = 32;
        public static final int SIG_R_ITEM_LENGTH = 1;
        public static final int SIG_S_OFFSET = 116;
        public static final int SIG_S_COUNT = 32;
        public static final int SIG_S_LENGTH = 32;
        public static final int SIG_S_ITEM_LENGTH = 1;
        public static final int SIG_V_OFFSET = 148;
        public static final int SIG_V_LENGTH = 1;
        public static final int BYTES = 149;
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
        public int exchangeId() {
            return this.buffer.getInt(this.offset + EXCHANGE_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit exchangeId(int value) {
            this.buffer.putInt(this.offset + EXCHANGE_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public int assetId() {
            return this.buffer.getInt(this.offset + ASSET_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit assetId(int value) {
            this.buffer.putInt(this.offset + ASSET_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long version() {
            return this.buffer.getLong(this.offset + VERSION_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit version(long value) {
            this.buffer.putLong(this.offset + VERSION_OFFSET, value, ByteOrder.BIG_ENDIAN);
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
        public long minEtherQty() {
            return this.buffer.getLong(this.offset + MIN_ETHER_QTY_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit minEtherQty(long value) {
            this.buffer.putLong(this.offset + MIN_ETHER_QTY_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long minAssetQty() {
            return this.buffer.getLong(this.offset + MIN_ASSET_QTY_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit minAssetQty(long value) {
            this.buffer.putLong(this.offset + MIN_ASSET_QTY_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long etherShift() {
            return this.buffer.getLong(this.offset + ETHER_SHIFT_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit etherShift(long value) {
            this.buffer.putLong(this.offset + ETHER_SHIFT_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long assetShift() {
            return this.buffer.getLong(this.offset + ASSET_SHIFT_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public UpdateLimit assetShift(long value) {
            this.buffer.putLong(this.offset + ASSET_SHIFT_OFFSET, value, ByteOrder.BIG_ENDIAN);
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
            sb.append("user_address: ");
            sb.append("[").append(userAddress(0)).append(", ").append(userAddress(1)).append(", ").append(userAddress(2)).append(", ")
                    .append(userAddress(3)).append(", ").append(userAddress(4)).append(", ").append(userAddress(5)).append(", ")
                    .append(userAddress(6)).append(", ").append(userAddress(7)).append(", ").append(userAddress(8)).append(", ")
                    .append(userAddress(9)).append(", ").append(userAddress(10)).append(", ").append(userAddress(11)).append(", ")
                    .append(userAddress(12)).append(", ").append(userAddress(13)).append(", ").append(userAddress(14)).append(", ")
                    .append(userAddress(15)).append(", ").append(userAddress(16)).append(", ").append(userAddress(17)).append(", ")
                    .append(userAddress(18)).append(", ").append(userAddress(19))
                    .append("]");
            sb.append(", exchange_id: ");
            sb.append(Integer.toUnsignedString(exchangeId()));
            sb.append(", asset_id: ");
            sb.append(Integer.toUnsignedString(assetId()));
            sb.append(", version: ");
            sb.append(Long.toUnsignedString(version()));
            sb.append(", max_long_price: ");
            sb.append(Long.toUnsignedString(maxLongPrice()));
            sb.append(", min_short_price: ");
            sb.append(Long.toUnsignedString(minShortPrice()));
            sb.append(", min_ether_qty: ");
            sb.append(minEtherQty());
            sb.append(", min_asset_qty: ");
            sb.append(minAssetQty());
            sb.append(", ether_shift: ");
            sb.append(etherShift());
            sb.append(", asset_shift: ");
            sb.append(assetShift());
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
