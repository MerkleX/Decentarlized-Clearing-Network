package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
public class SecurityLockTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions notCreator = Accounts.getTx(1);

        BigInteger allFeatures = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

        BigInteger feature1 = BigInteger.ONE.shiftLeft(5);
        BigInteger feature2 = BigInteger.ONE.shiftLeft(10);

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
    }
}
