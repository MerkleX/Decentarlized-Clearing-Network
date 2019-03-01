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
        public static final int USER_ID_OFFSET = 0;
        public static final int USER_ID_LENGTH = 8;
        public static final int QUOTE_DELTA_OFFSET = 8;
        public static final int QUOTE_DELTA_LENGTH = 8;
        public static final int BASE_DELTA_OFFSET = 16;
        public static final int BASE_DELTA_LENGTH = 8;
        public static final int FEES_OFFSET = 24;
        public static final int FEES_LENGTH = 8;
        public static final int BYTES = 32;
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
        public long userId() {
            return this.buffer.getLong(this.offset + USER_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public SettlementData userId(long value) {
            this.buffer.putLong(this.offset + USER_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
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
            sb.append("user_id: ");
            sb.append(Long.toUnsignedString(userId()));
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
        public static final int QUOTE_ASSET_ID_OFFSET = 0;
        public static final int QUOTE_ASSET_ID_LENGTH = 4;
        public static final int BASE_ASSET_ID_OFFSET = 4;
        public static final int BASE_ASSET_ID_LENGTH = 4;
        public static final int USER_COUNT_OFFSET = 8;
        public static final int USER_COUNT_LENGTH = 1;
        public static final int BYTES = 9;
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
        public int quoteAssetId() {
            return this.buffer.getInt(this.offset + QUOTE_ASSET_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public GroupHeader quoteAssetId(int value) {
            this.buffer.putInt(this.offset + QUOTE_ASSET_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
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
            sb.append("quote_asset_id: ");
            sb.append(Integer.toUnsignedString(quoteAssetId()));
            sb.append(", base_asset_id: ");
            sb.append(Integer.toUnsignedString(baseAssetId()));
            sb.append(", user_count: ");
            sb.append(Byte.toUnsignedInt(userCount()));
            sb.append(" }");
            return sb.toString();
        }
    }
}
