package io.merklex.dcn.models;
import org.agrona.MutableDirectBuffer;
import java.util.HashMap;
import java.nio.ByteOrder;

@javax.annotation.Generated(value="merklex-code-gen")
public class Settlement {
    public static class Type {
        private Type() {}
    }
    public static class SettlementData {
        public SettlementData clearToZeros() {
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
        public static final int QUOTE_DELTA_OFFSET = 20;
        public static final int QUOTE_DELTA_LENGTH = 8;
        public static final int BASE_DELTA_OFFSET = 28;
        public static final int BASE_DELTA_LENGTH = 8;
        public static final int FEES_OFFSET = 36;
        public static final int FEES_LENGTH = 8;
        public static final int BYTES = 44;
        public SettlementData wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(SettlementData other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public SettlementData userAddress(int pos, byte value) {
            assert(pos >= 0 && pos < USER_ADDRESS_COUNT);
            buffer.putByte(this.offset + USER_ADDRESS_OFFSET + USER_ADDRESS_ITEM_LENGTH * pos, value);
            return this;
        }
        public byte userAddress(int pos) {
            assert(pos >= 0 && pos < USER_ADDRESS_COUNT);
            return buffer.getByte(this.offset + USER_ADDRESS_OFFSET + USER_ADDRESS_ITEM_LENGTH * pos);
        }
        public SettlementData getUserAddress(byte[] value, int pos) {
            buffer.getBytes(this.offset + USER_ADDRESS_OFFSET, value, pos, 20);
            return this;
        }
        public SettlementData getUserAddress(byte[] value) {
            buffer.getBytes(this.offset + USER_ADDRESS_OFFSET, value, 0, 20);
            return this;
        }
        public SettlementData setUserAddress(byte[] value, int pos) {
            buffer.putBytes(this.offset + USER_ADDRESS_OFFSET, value, pos, 20);
            return this;
        }
        public SettlementData setUserAddress(byte[] value) {
            buffer.putBytes(this.offset + USER_ADDRESS_OFFSET, value, 0, 20);
            return this;
        }
        public long quoteDelta() {
            return this.buffer.getLong(this.offset + QUOTE_DELTA_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public SettlementData quoteDelta(long value) {
            this.buffer.putLong(this.offset + QUOTE_DELTA_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long baseDelta() {
            return this.buffer.getLong(this.offset + BASE_DELTA_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public SettlementData baseDelta(long value) {
            this.buffer.putLong(this.offset + BASE_DELTA_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long fees() {
            return this.buffer.getLong(this.offset + FEES_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public SettlementData fees(long value) {
            this.buffer.putLong(this.offset + FEES_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SettlementData { ");
            sb.append("user_address: ");
            sb.append("[").append(userAddress(0)).append(", ").append(userAddress(1)).append(", ").append(userAddress(2)).append(", ")
                    .append(userAddress(3)).append(", ").append(userAddress(4)).append(", ").append(userAddress(5)).append(", ")
                    .append(userAddress(6)).append(", ").append(userAddress(7)).append(", ").append(userAddress(8)).append(", ")
                    .append(userAddress(9)).append(", ").append(userAddress(10)).append(", ").append(userAddress(11)).append(", ")
                    .append(userAddress(12)).append(", ").append(userAddress(13)).append(", ").append(userAddress(14)).append(", ")
                    .append(userAddress(15)).append(", ").append(userAddress(16)).append(", ").append(userAddress(17)).append(", ")
                    .append(userAddress(18)).append(", ").append(userAddress(19))
                    .append("]");
            sb.append(", quote_delta: ");
            sb.append(quoteDelta());
            sb.append(", base_delta: ");
            sb.append(baseDelta());
            sb.append(", fees: ");
            sb.append(Long.toUnsignedString(fees()));
            sb.append(" }");
            return sb.toString();
        }
    }
    public static class GroupsHeader {
        public GroupsHeader clearToZeros() {
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
        public static final int EXCHANGE_ID_OFFSET = 0;
        public static final int EXCHANGE_ID_LENGTH = 4;
        public static final int BYTES = 4;
        public GroupsHeader wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(GroupsHeader other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public int exchangeId() {
            return this.buffer.getInt(this.offset + EXCHANGE_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public GroupsHeader exchangeId(int value) {
            this.buffer.putInt(this.offset + EXCHANGE_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("GroupsHeader { ");
            sb.append("exchange_id: ");
            sb.append(Integer.toUnsignedString(exchangeId()));
            sb.append(" }");
            return sb.toString();
        }
    }
    public static class GroupHeader {
        public GroupHeader clearToZeros() {
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
        public static final int BASE_ASSET_ID_OFFSET = 0;
        public static final int BASE_ASSET_ID_LENGTH = 4;
        public static final int USER_COUNT_OFFSET = 4;
        public static final int USER_COUNT_LENGTH = 1;
        public static final int BYTES = 5;
        public GroupHeader wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(GroupHeader other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public int baseAssetId() {
            return this.buffer.getInt(this.offset + BASE_ASSET_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public GroupHeader baseAssetId(int value) {
            this.buffer.putInt(this.offset + BASE_ASSET_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public byte userCount() {
            return this.buffer.getByte(this.offset + USER_COUNT_OFFSET);
        }
        public GroupHeader userCount(byte value) {
            this.buffer.putByte(this.offset + USER_COUNT_OFFSET, value);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("GroupHeader { ");
            sb.append("base_asset_id: ");
            sb.append(Integer.toUnsignedString(baseAssetId()));
            sb.append(", user_count: ");
            sb.append(Byte.toUnsignedInt(userCount()));
            sb.append(" }");
            return sb.toString();
        }
    }
}
