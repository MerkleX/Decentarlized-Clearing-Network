package io.merklex.dcn;

import io.merklex.dcn.models.Settlement;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.web3j.utils.Numeric;

public class Settlements extends Settlement.GroupsHeader {
    @Override
    public Settlements wrap(MutableDirectBuffer buffer, int offset) {
        return (Settlements) super.wrap(buffer, offset);
    }

    @Override
    public Settlements exchangeId(int value) {
        return (Settlements) super.exchangeId(value);
    }

    @Override
    public Settlements clearToZeros() {
        return (Settlements) super.clearToZeros();
    }

    public Group firstGroup(Group group) {
        return group.wrap(
                this.messageMemoryBuffer(),
                this.messageMemoryOffset() + BYTES
        );
    }

    public static class Group extends Settlement.GroupHeader {
        @Override
        public Group clearToZeros() {
            return (Group) super.clearToZeros();
        }

        @Override
        public Group wrap(MutableDirectBuffer buffer, int offset) {
            return (Group) super.wrap(buffer, offset);
        }

        @Override
        public Group baseAssetId(int value) {
            return (Group) super.baseAssetId(value);
        }

        @Override
        public Group userCount(byte value) {
            return (Group) super.userCount(value);
        }

        public Group userCount(int value) {
            if ((value & 0xFF) != value) {
                throw new IllegalArgumentException();
            }

            return (Group) super.userCount((byte) value);
        }

        public SettlementData settlement(SettlementData settlement, int index) {
            return settlement.wrap(
                    this.messageMemoryBuffer(),
                    this.messageMemoryOffset() + Group.BYTES + SettlementData.BYTES * index
            );
        }

        public int size() {
            return Group.BYTES + SettlementData.BYTES * userCount();
        }
    }

    public static class SettlementData extends Settlement.SettlementData {
        @Override
        public SettlementData clearToZeros() {
            return (SettlementData) super.clearToZeros();
        }

        @Override
        public SettlementData wrap(MutableDirectBuffer buffer, int offset) {
            return (SettlementData) super.wrap(buffer, offset);
        }

        @Override
        public SettlementData userAddress(int pos, byte value) {
            return (SettlementData) super.userAddress(pos, value);
        }

        @Override
        public SettlementData getUserAddress(byte[] value, int pos) {
            return (SettlementData) super.getUserAddress(value, pos);
        }

        @Override
        public SettlementData setUserAddress(byte[] value, int pos) {
            return (SettlementData) super.setUserAddress(value, pos);
        }

        @Override
        public SettlementData setUserAddress(byte[] value) {
            return (SettlementData) super.setUserAddress(value);
        }
        
        public SettlementData setUserAddress(String value) {
            return (SettlementData) super.setUserAddress(Numeric.hexStringToByteArray(value));
        }

        @Override
        public SettlementData quoteDelta(long value) {
            return (SettlementData) super.quoteDelta(value);
        }

        @Override
        public SettlementData baseDelta(long value) {
            return (SettlementData) super.baseDelta(value);
        }

        @Override
        public SettlementData fees(long value) {
            return (SettlementData) super.fees(value);
        }
    }
}
