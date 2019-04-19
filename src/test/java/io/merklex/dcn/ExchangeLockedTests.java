package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.runner.RunWith;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.*;
import static io.merklex.dcn.ApplySettlementBugFix.APPLY_LIMIT_TX;
import static io.merklex.dcn.ApplySettlementBugFix.APPLY_SETTLEMENT_PAYLOAD;
import static io.merklex.dcn.utils.AssertHelpers.*;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

@RunWith(Spectrum.class)
public class ExchangeLockedTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions notCreator = Accounts.getTx(1);

        beforeAll(() -> {
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("12345678901", creator.getAddress())));
        });

        it("only creator should be able to lock exchanges", () -> {
            assertRevert("0x01", notCreator.sendCall(StaticNetwork.DCN(),
                    DCN.set_exchange_locked(0, true)));
        });

        it("must be valid exchange", () -> {
            assertRevert("0x02", creator.sendCall(StaticNetwork.DCN(),
                    DCN.set_exchange_locked(134, true)));
        });

        it("should be able to lock exchange", () -> {
            DCN.GetExchangeReturnValue exchange;
            exchange = DCN.query_get_exchange(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_exchange(0));

            assertFalse(exchange.locked);

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.set_exchange_locked(0, true)));

            exchange = DCN.query_get_exchange(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_exchange(0));
            assertTrue(exchange.locked);
        });

        describe("exchange functions should be blocked", () -> {
            StaticNetwork.DescribeCheckpointForEach();

            it("exchange_set_limits should be blocked", () -> {
                creator.reloadNonce();

                assertRevert("0x03", creator.sendCall(
                        BigInteger.ZERO, StaticNetwork.GAS_LIMIT, StaticNetwork.DCN(),
                        APPLY_LIMIT_TX, BigInteger.ZERO)
                );

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.set_exchange_locked(0, false)));

                assertNotEquals("0x02", getRevert(creator.sendCall(
                        BigInteger.ZERO, StaticNetwork.GAS_LIMIT, StaticNetwork.DCN(),
                        APPLY_LIMIT_TX, BigInteger.ZERO)));
            });

            it("exchange_apply_settlement_group should be blocked", () -> {
                creator.reloadNonce();

                assertRevert("0x03", creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(APPLY_SETTLEMENT_PAYLOAD)));

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.set_exchange_locked(0, false)));

                assertNotEquals("0x04", getRevert(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(APPLY_SETTLEMENT_PAYLOAD))));
            });

            it("exchange_transfer_from should be blocked", () -> {
                creator.reloadNonce();

                byte[] bytes = new byte[1000];
                UnsafeBuffer buffer = new UnsafeBuffer(bytes);

                Transfers transfers = new Transfers().wrap(buffer, 0);
                Transfers.Group group = new Transfers.Group();
                Transfers.Transfer transfer = new Transfers.Transfer();

                transfers.exchangeId(0)
                        .firstGroup(group)
                        .transferCount(1)
                        .assetId(1)
                        .allowOverdraft(true)
                        .firstTransfer(transfer)
                        .userId(0)
                        .quantity(3);

                String payload = Numeric.toHexString(bytes, 0, transfers.bytes(1, group), true);

                assertRevert("0x04", creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_transfer_from(payload)));

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.set_exchange_locked(0, false)));

                assertNotEquals("0x03", getRevert(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_transfer_from(payload))));
            });
        });
    }
}
