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
    }
}
