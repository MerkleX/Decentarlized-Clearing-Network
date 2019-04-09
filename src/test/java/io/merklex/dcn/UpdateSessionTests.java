//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.web3.RevertCodeExtractor;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.web3.EtherTransactions;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.Log;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import java.math.BigInteger;
//import java.util.List;
//
//import static com.greghaskins.spectrum.Spectrum.*;
//
//@RunWith(Spectrum.class)
//public class UpdateSessionTests {
//    {
//        Run(true);
//        Run(false);
//    }
//
//    private static void Run(boolean quoteFirst) {
//        describe("Run with quote first ? " + quoteFirst, () -> {
//            StaticNetwork.DescribeCheckpoint();
//
//            EtherTransactions creator = Accounts.getTx(0);
//            EtherTransactions exchangeOwner = Accounts.getTx(1);
//            EtherTransactions user = Accounts.getTx(2);
//
//            beforeAll(() -> {
//                creator.call(StaticNetwork.DCN(), DCN.add_asset("ddcd", 1, "0x1"));
//                creator.call(StaticNetwork.DCN(), DCN.add_asset("12cd", 1, "0x1"));
//
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(),
//                        DCN.add_exchange("merklex ", quoteFirst ? 0 : 1, exchangeOwner.credentials().getAddress()));
//                Assert.assertEquivalent("0x1", tx.getStatus());
//            });
//
//            it("initial version should be zero", () -> {
//                DCN.GetSessionReturnValue getSessionReturnValue = DCN.query_get_session(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_session(user.credentials().getAddress(), 0)
//                );
//
//                Assert.assertEquivalent(0, getSessionReturnValue.unlock_at);
//                Assert.assertEquivalent(0, getSessionReturnValue.fee_limit);
//                Assert.assertEquivalent(0, getSessionReturnValue.fee_used);
//                Assert.assertEquivalent(0, getSessionReturnValue.version);
//            });
//
//            it("should fail to update with unlock_at = now", () -> {
//                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
//                        DCN.update_session(0, System.currentTimeMillis() / 1000, 0));
//                Assert.assertEquivalent("0x01", RevertCodeExtractor.GetRevert(tx.getError()));
//                Assert.assertEquivalent("0x0", user.waitForResult(tx).getStatus());
//            });
//
//            it("should fail to update with unlock_at = now + 31 days", () -> {
//                long now = System.currentTimeMillis() / 1000;
//                long days31 = 31 * 24 * 3600;
//                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
//                        DCN.update_session(0, now + days31, 0));
//                Assert.assertEquivalent("0x01", RevertCodeExtractor.GetRevert(tx.getError()));
//                Assert.assertEquivalent("0x0", user.waitForResult(tx).getStatus());
//            });
//
//            it("should fail to update with non existent exchange_id", () -> {
//                long now = System.currentTimeMillis() / 1000;
//                long days10 = 10 * 24 * 3600;
//                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
//                        DCN.update_session(2, now + days10, 0));
//                Assert.assertEquivalent("0x02", RevertCodeExtractor.GetRevert(tx.getError()));
//                Assert.assertEquivalent("0x0", user.waitForResult(tx).getStatus());
//            });
//
//            it("should be able to update, send log, update version/state", () -> {
//                Assert.assertEquivalent(0, DCN.query_get_session_balance(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_session_balance(user.credentials().getAddress(), 0, 0)
//                ).asset_balance);
//
//                long now = System.currentTimeMillis() / 1000;
//                long days10 = 10 * 24 * 3600;
//                TransactionReceipt tx = user.call(StaticNetwork.DCN(),
//                        DCN.update_session(0, now + days10, 0));
//                Assert.assertEquivalent("0x1", tx.getStatus());
//
//                List<Log> logs = tx.getLogs();
//                Assert.assertEquivalent(1, logs.size());
//
//                DCN.SessionUpdated sessionUpdated = DCN.ExtractSessionUpdated(logs.get(0));
//                Assert.assertNotNull(sessionUpdated);
//                Assert.assertEquivalent(0, sessionUpdated.exchange_id);
//                Assert.assertEquivalent(user.credentials().getAddress(), sessionUpdated.user);
//
//                Assert.assertEquivalent(BigInteger.ZERO, DCN.query_get_balance(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_balance(user.credentials().getAddress(), 0)
//                ).return_balance);
//
//                Assert.assertEquivalent(0, DCN.query_get_session_balance(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_session_balance(user.credentials().getAddress(), 0, 0)
//                ).asset_balance);
//
//
//                DCN.GetSessionReturnValue getSessionReturnValue = DCN.query_get_session(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_session(user.credentials().getAddress(), 0)
//                );
//
//                Assert.assertEquivalent(now + days10, getSessionReturnValue.unlock_at);
//                Assert.assertEquivalent(0, getSessionReturnValue.fee_limit);
//                Assert.assertEquivalent(0, getSessionReturnValue.fee_used);
//                Assert.assertEquivalent(1, getSessionReturnValue.version);
//            });
//        });
//    }
//}
