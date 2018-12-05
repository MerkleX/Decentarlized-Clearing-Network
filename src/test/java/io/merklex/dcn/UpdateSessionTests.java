package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.BetterAssert;
import io.merklex.dcn.utils.RevertCodeExtractor;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
public class UpdateSessionTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchangeOwner = Accounts.getTx(1);
        EtherTransactions user = Accounts.getTx(2);

        beforeAll(() -> {
            creator.call(StaticNetwork.DCN(), DCN.add_asset("12cd", 1, "0x1"));

            TransactionReceipt tx = creator.call(StaticNetwork.DCN(),
                    DCN.add_exchange("merklex ", 0, exchangeOwner.credentials().getAddress()));
            Assert.assertEquals("0x1", tx.getStatus());
        });

        it("initial version should be zero", () -> {
            DCN.GetSessionReturnValue getSessionReturnValue = DCN.query_get_session(
                    StaticNetwork.DCN(),
                    StaticNetwork.Web3(),
                    DCN.get_session(user.credentials().getAddress(), 0)
            );

            Assert.assertEquals(0, getSessionReturnValue.expire_time);
            Assert.assertEquals(0, getSessionReturnValue.fee_limit);
            Assert.assertEquals(0, getSessionReturnValue.fee_used);
            Assert.assertEquals(0, getSessionReturnValue.version);
        });

        it("should fail to update with expire time = now", () -> {
            EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
                    DCN.update_session(0, System.currentTimeMillis() / 1000));
            Assert.assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
            Assert.assertEquals("0x0", user.waitForResult(tx).getStatus());
        });

        it("should fail to update with expire time = now + 31 days", () -> {
            long now = System.currentTimeMillis() / 1000;
            long days31 = 31 * 24 * 3600;
            EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
                    DCN.update_session(0, now + days31));
            Assert.assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
            Assert.assertEquals("0x0", user.waitForResult(tx).getStatus());
        });

        it("should fail to update with non existent exchange_id", () -> {
            long now = System.currentTimeMillis() / 1000;
            long days10 = 10 * 24 * 3600;
            EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
                    DCN.update_session(2, now + days10));
            Assert.assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
            Assert.assertEquals("0x0", user.waitForResult(tx).getStatus());
        });

        it("should be able to update, send log, update version/state", () -> {
            Assert.assertEquals(0, DCN.query_get_session_balance(
                    StaticNetwork.DCN(),
                    StaticNetwork.Web3(),
                    DCN.get_session_balance(user.credentials().getAddress(), 0, 0)
            ).asset_balance);

            long now = System.currentTimeMillis() / 1000;
            long days10 = 10 * 24 * 3600;
            TransactionReceipt tx = user.call(StaticNetwork.DCN(),
                    DCN.update_session(0, now + days10));
            Assert.assertEquals("0x1", tx.getStatus());

            List<Log> logs = tx.getLogs();
            Assert.assertEquals(1, logs.size());

            DCN.SessionUpdated sessionUpdated = DCN.ExtractSessionUpdated(logs.get(0));
            Assert.assertNotNull(sessionUpdated);
            Assert.assertEquals(0, sessionUpdated.exchange_id);
            Assert.assertEquals(user.credentials().getAddress(), sessionUpdated.user);

            Assert.assertEquals(BigInteger.ZERO, DCN.query_get_balance(
                    StaticNetwork.DCN(),
                    StaticNetwork.Web3(),
                    DCN.get_balance(user.credentials().getAddress(), 0)
            ).return_balance);

            Assert.assertEquals(0, DCN.query_get_session_balance(
                    StaticNetwork.DCN(),
                    StaticNetwork.Web3(),
                    DCN.get_session_balance(user.credentials().getAddress(), 0, 0)
            ).asset_balance);


            DCN.GetSessionReturnValue getSessionReturnValue = DCN.query_get_session(
                    StaticNetwork.DCN(),
                    StaticNetwork.Web3(),
                    DCN.get_session(user.credentials().getAddress(), 0)
            );

            Assert.assertEquals(now + days10, getSessionReturnValue.expire_time);
            Assert.assertEquals(0, getSessionReturnValue.fee_limit);
            Assert.assertEquals(0, getSessionReturnValue.fee_used);
            Assert.assertEquals(1, getSessionReturnValue.version);
        });
    }
}
