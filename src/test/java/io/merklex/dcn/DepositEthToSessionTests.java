package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.BetterAssert;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
public class DepositEthToSessionTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions user = Accounts.getTx(23);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        it("should not revert, update, nor log with no value", () -> {
            TransactionReceipt tx = user.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1));
            Assert.assertEquals("0x1", tx.getStatus());

            Assert.assertEquals(0, tx.getLogs().size());

            Assert.assertEquals(
                    BigInteger.ZERO,
                    query.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), 0)).return_balance
            );

            Assert.assertEquals(
                    0,
                    query.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(user.getAddress(), 1, 0)).asset_balance
            );
        });

        it("should move remainder to balance", () -> {
            BigInteger depositAmount = new BigInteger("12340987654321");

            TransactionReceipt tx = user.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), depositAmount);
            Assert.assertEquals("0x1", tx.getStatus());

            Assert.assertEquals(1, tx.getLogs().size());

            BetterAssert.assertEquals(
                    987654321,
                    query.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), 0)).return_balance
            );

            BetterAssert.assertEquals(
                    1234,
                    query.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(user.getAddress(), 1, 0)).asset_balance
            );
        });

        it("should fail on session balance overflow", () -> {
            BigInteger depositAmount = BigInteger.valueOf(2).pow(64).subtract(BigInteger.valueOf(1000)).multiply(BigInteger.TEN.pow(10));

            TransactionReceipt tx = user.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), depositAmount);
            Assert.assertEquals("0x0", tx.getStatus());

            Assert.assertEquals(0, tx.getLogs().size());

            Assert.assertEquals(
                    BigInteger.valueOf(987654321),
                    query.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), 0)).return_balance
            );

            Assert.assertEquals(
                    1234,
                    query.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(user.getAddress(), 1, 0)).asset_balance
            );
        });

        it("should just move to balance if under unit scale", () -> {
            TransactionReceipt tx = user.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), BigInteger.valueOf(5000));
            Assert.assertEquals("0x1", tx.getStatus());

            Assert.assertEquals(1, tx.getLogs().size());

            Assert.assertEquals(
                    BigInteger.valueOf(987654321 + 5000),
                    query.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), 0)).return_balance
            );

            Assert.assertEquals(
                    1234,
                    query.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(user.getAddress(), 1, 0)).asset_balance
            );
        });

        it("should move overflow to balance", () -> {
            BigInteger unitScale = BigInteger.TEN.pow(10);

            BigInteger depositAmount = BigInteger.valueOf(512).multiply(unitScale)
                    .add(BigInteger.valueOf(909));
            BigInteger overflowAmount = BigInteger.valueOf(2).pow(65).multiply(unitScale);

            TransactionReceipt tx = user.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1),
                    depositAmount.add(overflowAmount));
            Assert.assertEquals("0x1", tx.getStatus());

            Assert.assertEquals(1, tx.getLogs().size());

            Assert.assertEquals(
                    BigInteger.valueOf(987654321 + 5000 + 909).add(overflowAmount),
                    query.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), 0)).return_balance
            );

            Assert.assertEquals(
                    1234 + 512,
                    query.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(user.getAddress(), 1, 0)).asset_balance
            );
        });

        it("should log position update", () -> {
            TransactionReceipt tx = user.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), BigInteger.valueOf(10).pow(11));
            Assert.assertEquals("0x1", tx.getStatus());

            Assert.assertEquals(1, tx.getLogs().size());
            DCN.PositionUpdated positionUpdated = DCN.ExtractPositionUpdated(tx.getLogs().get(0));
            Assert.assertNotNull(positionUpdated);

            Assert.assertEquals(0, positionUpdated.asset_id);
            Assert.assertEquals(1, positionUpdated.exchange_id);
            Assert.assertEquals(user.getAddress(), positionUpdated.user);

            Assert.assertEquals(
                    new BigInteger("368934881474191032320987660230"),
                    query.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), 0)).return_balance
            );

            Assert.assertEquals(
                    1234 + 512 + 10,
                    query.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(user.getAddress(), 1, 0)).asset_balance
            );
        });
    }
}
