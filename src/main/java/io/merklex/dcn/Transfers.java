package io.merklex.dcn;

import io.merklex.dcn.models.ExchangeTransferFrom;
import org.agrona.MutableDirectBuffer;

public class Transfers extends ExchangeTransferFrom.ExchangeTransfersHeader {
    @Override
    public Transfers wrap(MutableDirectBuffer buffer, int offset) {
        return (Transfers) super.wrap(buffer, offset);
    }

    @Override
    public Transfers exchangeId(int value) {
        return (Transfers) super.exchangeId(value);
    }

    public Group firstGroup(Group group) {
        return group.wrap(messageMemoryBuffer(), messageMemoryOffset() + BYTES);
    }

    public int bytes(int groups, Group group) {
        int transfers = 0;

        firstGroup(group);
        for (int i = 0; i < groups; i++) {
            transfers += Byte.toUnsignedInt(group.transferCount());
            group.nextGroup(group);
        }

        return BYTES + Group.BYTES * groups + Transfer.BYTES * transfers;
    }

    public static class Transfer extends ExchangeTransferFrom.ExchangeTransfer {
        @Override
        public Transfer wrap(MutableDirectBuffer buffer, int offset) {
            return (Transfer) super.wrap(buffer, offset);
        }

        @Override
        public Transfer userId(long value) {
            return (Transfer) super.userId(value);
        }

        @Override
        public Transfer quantity(long value) {
            return (Transfer) super.quantity(value);
        }

        public Transfer nextTransfer(Transfer transfer) {
            return transfer.wrap(messageMemoryBuffer(), messageMemoryOffset() + BYTES);
        }
    }

    public static class Group extends ExchangeTransferFrom.ExchangeTransferGroup {
        @Override
        public Group wrap(MutableDirectBuffer buffer, int offset) {
            return (Group) super.wrap(buffer, offset);
        }

        @Override
        public Group assetId(int value) {
            return (Group) super.assetId(value);
        }

        @Override
        public Group allowOverdraft(byte value) {
            return (Group) super.allowOverdraft(value);
        }

        @Override
        public Group allowOverdraft(boolean value) {
            return (Group) super.allowOverdraft(value);
        }

        @Override
        public Group transferCount(byte value) {
            return (Group) super.transferCount(value);
        }

        public Group transferCount(int value) {
            if (value >= 256) {
                throw new IllegalArgumentException();
            }
            return transferCount((byte) value);
        }

        public Transfer firstTransfer(Transfer transfer) {
            return transfer.wrap(messageMemoryBuffer(), messageMemoryOffset() + BYTES);
        }

        public Group nextGroup(Group group) {
            return group.wrap(messageMemoryBuffer(), messageMemoryOffset()
                    + Group.BYTES + Byte.toUnsignedInt(transferCount()) * Transfer.BYTES);
        }
    }
}
