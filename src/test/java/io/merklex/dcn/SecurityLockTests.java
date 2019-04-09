package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
public class SecurityLockTests {
    private static final EtherTransactions creator = Accounts.getTx(0);

    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions notCreator = Accounts.getTx(1);

        BigInteger allFeatures = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

        BigInteger feature1 = BigInteger.ONE.shiftLeft(5);
        BigInteger feature2 = BigInteger.ONE.shiftLeft(10);

        describe("manage locks", () -> {
            StaticNetwork.DescribeCheckpoint();

            it("only creator should be able to call security functions", () -> {
                assertRevert("0x01", notCreator.sendCall(StaticNetwork.DCN(),
                        DCN.security_lock(BigInteger.ONE)));

                assertRevert("0x01", notCreator.sendCall(StaticNetwork.DCN(),
                        DCN.security_propose(BigInteger.ONE)));

                assertRevert("0x01", notCreator.sendCall(StaticNetwork.DCN(),
                        DCN.security_set_proposed()));
            });

            it("Lock should OR features", () -> {
                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_lock(feature1)));

                DCN.GetSecurityStateReturnValue securityState;
                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                assertEquals(feature1, securityState.locked_features);
                assertEquals(allFeatures, securityState.locked_features_proposed);


                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_lock(feature2)));

                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                assertEquals(feature1.or(feature2), securityState.locked_features);
                assertEquals(allFeatures, securityState.locked_features_proposed);
            });

            describe("Applying proposed should lock everything", () -> {
                StaticNetwork.DescribeCheckpoint();

                it("set proposed should lock all", () -> {
                    assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                            DCN.security_set_proposed()));

                    DCN.GetSecurityStateReturnValue securityState;
                    securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                            creator.getWeb3(), DCN.get_security_state());

                    assertEquals(allFeatures, securityState.locked_features);
                    assertEquals(allFeatures, securityState.locked_features_proposed);
                });
            });

            Box<BigInteger> unlockTimestamp = new Box<>();

            it("propose should update unlock_timestamp", () -> {
                creator.reloadNonce();

                DCN.GetSecurityStateReturnValue securityState;
                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                assertEquals(BigInteger.ZERO, securityState.proposed_unlock_timestamp);

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_propose(feature1)));

                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                unlockTimestamp.value = securityState.proposed_unlock_timestamp;
                assertEquals(feature1.or(feature2), securityState.locked_features);
                assertEquals(feature1, securityState.locked_features_proposed);
                assertNotEquals(BigInteger.ZERO, unlockTimestamp.value);
            });

            it("locking more in proposed should not update unlock", () -> {
                DCN.GetSecurityStateReturnValue securityState;

                BigInteger updated = feature1.or(BigInteger.ONE.shiftLeft(55));
                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_propose(updated)));

                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                assertEquals(feature1.or(feature2), securityState.locked_features);
                assertEquals(updated, securityState.locked_features_proposed);
                assertEquals(unlockTimestamp.value, securityState.proposed_unlock_timestamp);
            });

            it("unlocking proposed should update unlock", () -> {
                DCN.GetSecurityStateReturnValue securityState;

                /* wait 2 seconds so timestamp will update */
                Thread.sleep(1000);

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_propose(feature2)));

                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                assertEquals(feature1.or(feature2), securityState.locked_features);
                assertEquals(feature2, securityState.locked_features_proposed);
                assertTrue(securityState.proposed_unlock_timestamp.compareTo(unlockTimestamp.value) > 0);

                unlockTimestamp.value = securityState.proposed_unlock_timestamp;
            });

            it("should be able to propose complete unlock", () -> {
                DCN.GetSecurityStateReturnValue securityState;

                /* wait 2 seconds so timestamp will update */
                Thread.sleep(1000);

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_propose(BigInteger.ZERO)));

                securityState = DCN.query_get_security_state(StaticNetwork.DCN(),
                        creator.getWeb3(), DCN.get_security_state());

                assertEquals(feature1.or(feature2), securityState.locked_features);
                assertEquals(BigInteger.ZERO, securityState.locked_features_proposed);
                assertTrue(securityState.proposed_unlock_timestamp.compareTo(unlockTimestamp.value) > 0);
            });

            it("should fail to set proposed when locked", () -> {
                assertRevert("0x02", creator.sendCall(StaticNetwork.DCN(),
                        DCN.security_set_proposed()));
            });
        });

        describe("functions should be locked", () -> {
            StaticNetwork.DescribeCheckpointForEach();

            it("add_asset", () -> testLock(FeatureLocks.ADD_ASSET, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.add_asset("13", 1, "0x00"))
            ));

            it("add_exchange", () -> testLock(FeatureLocks.ADD_EXCHANGE, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.add_exchange("13", "0x00"))
            ));

            it("create_user", () -> testLock(FeatureLocks.CREATE_USER, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.user_create())
            ));

            it("exchange_deposit", () -> testLock(FeatureLocks.EXCHANGE_DEPOSIT, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.exchange_deposit(0, 0, 0))
            ));

            it("user_deposit", () -> testLock(FeatureLocks.USER_DEPOSIT, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.user_deposit(0, 0, BigInteger.ZERO))
            ));

            it("transfer_to_session", () -> testLock(FeatureLocks.TRANSFER_TO_SESSION, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.transfer_to_session(0, 0, 0, 0))
            ));

            it("deposit_asset_to_session", () -> testLock(FeatureLocks.DEPOSIT_ASSET_TO_SESSION, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.user_deposit_to_session(0, 0, 0, 0))
            ));

            it("exchange_transfer_from", () -> testLock(FeatureLocks.EXCHANGE_TRANSFER_FROM, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.exchange_transfer_from("0x000"))
            ));

            it("exchange_set_limit", () -> testLock(FeatureLocks.EXCHANGE_SET_LIMITS, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.exchange_set_limits("0x000"))
            ));

            it("apply_settlement_groups", () -> testLock(FeatureLocks.APPLY_SETTLEMENT_GROUPS, () ->
                    creator.sendCall(StaticNetwork.DCN(),
                            DCN.exchange_apply_settlement_groups("0x000"))
            ));
        });
    }

    private static void testLock(BigInteger feature, TxSupplier tx) throws Exception {
        creator.reloadNonce();

        EthSendTransaction txRes = tx.get();
        if (txRes.hasError()) {
            assertNotEquals("0x00", getRevert(txRes));
        }

        assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                DCN.security_lock(feature)));

        assertRevert("0x00", tx.get());
    }

    private interface TxSupplier {
        EthSendTransaction get() throws Exception;
    }
}
