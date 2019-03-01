package io.merklex.dcn.models;
import org.agrona.MutableDirectBuffer;
import java.util.HashMap;
import java.nio.ByteOrder;

@javax.annotation.Generated(value="merklex-code-gen")
public class ExchangeTransferFrom {
    public static class Type {
        private Type() {}
    }
    public static class ExchangeTransfer {
        public ExchangeTransfer clearToZeros() {
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
        public static final int QUANTITY_OFFSET = 8;
        public static final int QUANTITY_LENGTH = 8;
        public static final int BYTES = 16;
        public ExchangeTransfer wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(ExchangeTransfer other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public long userId() {
            return this.buffer.getLong(this.offset + USER_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public ExchangeTransfer userId(long value) {
            this.buffer.putLong(this.offset + USER_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public long quantity() {
            return this.buffer.getLong(this.offset + QUANTITY_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public ExchangeTransfer quantity(long value) {
            this.buffer.putLong(this.offset + QUANTITY_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ExchangeTransfer { ");
            sb.append("user_id: ");
            sb.append(Long.toUnsignedString(userId()));
            sb.append(", quantity: ");
            sb.append(Long.toUnsignedString(quantity()));
            sb.append(" }");
            return sb.toString();
        }
    }
    public static class ExchangeTransferGroup {
        public ExchangeTransferGroup clearToZeros() {
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
        public static final int ASSET_ID_OFFSET = 0;
        public static final int ASSET_ID_LENGTH = 4;
        public static final int ALLOW_OVERDRAFT_OFFSET = 4;
        public static final int ALLOW_OVERDRAFT_LENGTH = 1;
        public static final int TRANSFER_COUNT_OFFSET = 5;
        public static final int TRANSFER_COUNT_LENGTH = 1;
        public static final int BYTES = 6;
        public ExchangeTransferGroup wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(ExchangeTransferGroup other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public int assetId() {
            return this.buffer.getInt(this.offset + ASSET_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public ExchangeTransferGroup assetId(int value) {
            this.buffer.putInt(this.offset + ASSET_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public byte allowOverdraft() {
            return this.buffer.getByte(this.offset + ALLOW_OVERDRAFT_OFFSET);
        }
        public ExchangeTransferGroup allowOverdraft(byte value) {
            this.buffer.putByte(this.offset + ALLOW_OVERDRAFT_OFFSET, value);
            return this;
        }
        public boolean isAllowOverdraft() {
            return this.buffer.getByte(this.offset + ALLOW_OVERDRAFT_OFFSET) == (byte) 1;
        }
        public ExchangeTransferGroup allowOverdraft(boolean value) {
            this.buffer.putByte(this.offset + ALLOW_OVERDRAFT_OFFSET, value ? (byte) 1 : (byte) 0);
            return this;
        }
        public byte transferCount() {
            return this.buffer.getByte(this.offset + TRANSFER_COUNT_OFFSET);
        }
        public ExchangeTransferGroup transferCount(byte value) {
            this.buffer.putByte(this.offset + TRANSFER_COUNT_OFFSET, value);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ExchangeTransferGroup { ");
            sb.append("asset_id: ");
            sb.append(Integer.toUnsignedString(assetId()));
            sb.append(", allow_overdraft: ");
            sb.append(allowOverdraft());
            sb.append(", transfer_count: ");
            sb.append(Byte.toUnsignedInt(transferCount()));
            sb.append(" }");
            return sb.toString();
        }
    }
    public static class ExchangeTransfersHeader {
        public ExchangeTransfersHeader clearToZeros() {
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
        public ExchangeTransfersHeader wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }
        public void copyFrom(ExchangeTransfersHeader other) {
            buffer.putBytes(offset, other.buffer, other.offset, BYTES);
        }
        public void writeTo(MutableDirectBuffer buffer, int offset) {
            buffer.putBytes(offset, this.buffer, this.offset, BYTES);
        }
        public int exchangeId() {
            return this.buffer.getInt(this.offset + EXCHANGE_ID_OFFSET, ByteOrder.BIG_ENDIAN);
        }
        public ExchangeTransfersHeader exchangeId(int value) {
            this.buffer.putInt(this.offset + EXCHANGE_ID_OFFSET, value, ByteOrder.BIG_ENDIAN);
            return this;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ExchangeTransfersHeader { ");
            sb.append("exchange_id: ");
            sb.append(Integer.toUnsignedString(exchangeId()));
            sb.append(" }");
            return sb.toString();
        }
    }
}
