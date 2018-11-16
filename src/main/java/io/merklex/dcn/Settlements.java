package io.merklex.dcn;

import org.agrona.concurrent.UnsafeBuffer;
import org.web3j.utils.Numeric;

import java.util.Set;

public class Settlements {
    private UnsafeBuffer buffer;
    private int offset;

    public Settlements wrap(UnsafeBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int exchangeId() {
        return buffer.getInt(offset);
    }

    public Group firstGruop(Group group) {
        group.buffer = buffer;
        group.offset = offset + 4;
        return group;
    }

    public static class Group {
        public static int BYTES = 5;

        private UnsafeBuffer buffer;
        private int offset;

        public int assetId() {
            return buffer.getInt(offset);
        }

        public int userCount() {
            return Byte.toUnsignedInt(buffer.getByte(offset + 4));
        }

        public Group assetId(int value) {
            buffer.putInt(offset, value);
            return this;
        }

        public Group userCount(byte count) {
            buffer.putByte(offset + 4, count);
            return this;
        }

        public Group nextGroup(Group group) {
            group.buffer = buffer;
            group.offset = offset + Group.BYTES + Settlement.BYTES * userCount();
            return group;
        }

        public Settlement settlement(Settlement settlement, int index) {
            settlement.buffer = buffer;
            settlement.offset = Group.BYTES + Settlement.BYTES * index;
            return settlement;
        }
    }

    public static class Settlement {
        public static final int BYTES = 44;

        private UnsafeBuffer buffer;
        private int offset;

        public String userAddress() {
            byte[] addrBytes = new byte[20];
            buffer.getBytes(offset, addrBytes);
            return Numeric.toHexString(addrBytes);
        }

        public long etherDelta() {
            return buffer.getLong(offset + 20);
        }

        public long assetDelta() {
            return buffer.getLong(offset + 28);
        }

        public long fees() {
            return buffer.getLong(offset + 36);
        }
    }
}
