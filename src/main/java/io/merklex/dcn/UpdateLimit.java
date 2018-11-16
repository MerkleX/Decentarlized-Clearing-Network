package io.merklex.dcn;

import io.merklex.dcn.models.UpdateLimitMessage;
import org.agrona.MutableDirectBuffer;
import org.web3j.utils.Numeric;

public class UpdateLimit extends UpdateLimitMessage.UpdateLimit {
    @Override
    public UpdateLimit wrap(MutableDirectBuffer buffer, int offset) {
        return (UpdateLimit) super.wrap(buffer, offset);
    }

    public UpdateLimit user(String userHex) {
        byte[] userAddress = Numeric.hexStringToByteArray(userHex);
        super.setUserAddress(userAddress);

        return this;
    }

    public UpdateLimit signature(String signatureHex) {
        byte[] signature = Numeric.hexStringToByteArray(signatureHex);

        byte v = signature[64];
        if (v < 27) {
            v += 27;
        }

        super.setSigR(signature, 0);
        super.setSigS(signature, 32);
        super.sigV(v);

        return this;
    }

    @Override
    public UpdateLimit exchangeId(int value) {
        return (UpdateLimit) super.exchangeId(value);
    }

    @Override
    public UpdateLimit version(long value) {
        return (UpdateLimit) super.version(value);
    }

    @Override
    public UpdateLimit assetId(int value) {
        return (UpdateLimit) super.assetId(value);
    }

    @Override
    public UpdateLimit maxLongPrice(long value) {
        return (UpdateLimit) super.maxLongPrice(value);
    }

    @Override
    public UpdateLimit minShortPrice(long value) {
        return (UpdateLimit) super.minShortPrice(value);
    }

    @Override
    public UpdateLimit minEtherQty(long value) {
        return (UpdateLimit) super.minEtherQty(value);
    }

    @Override
    public UpdateLimit minAssetQty(long value) {
        return (UpdateLimit) super.minAssetQty(value);
    }

    @Override
    public UpdateLimit etherShift(long value) {
        return (UpdateLimit) super.etherShift(value);
    }

    @Override
    public UpdateLimit assetShift(long value) {
        return (UpdateLimit) super.assetShift(value);
    }
}
