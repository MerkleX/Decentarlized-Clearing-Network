//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.contracts.ERC20;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.dcn.utils.Box;
//import io.merklex.dcn.utils.RevertCodeExtractor;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.web3.EtherTransactions;
//import io.merklex.web3.QueryHelper;
//import org.junit.runner.RunWith;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import java.math.BigInteger;
//
//import static com.greghaskins.spectrum.Spectrum.beforeAll;
//import static com.greghaskins.spectrum.Spectrum.it;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//@RunWith(Spectrum.class)
//public class TransferTests {
//    {
//        StaticNetwork.DescribeCheckpoint();
//
//        EtherTransactions creator = Accounts.getTx(0);
//        EtherTransactions user = Accounts.getTx(23);
//
//        QueryHelper dcnQ = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());
//
//        Box<String> token = new Box<>();
//
//        beforeAll(() -> {
//            token.value = user.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT,
//                    ERC20.DeployData(BigInteger.valueOf(100000000), "tk", 8, "tk"),
//                    BigInteger.ZERO);
//
//            TransactionReceipt tx;
//
//            tx = creator.call(StaticNetwork.DCN(),
//                    DCN.add_asset("1234", 10, token.value));
//            assertEquals("0x1", tx.getStatus());
//
//            tx = creator.call(StaticNetwork.DCN(),
//                    DCN.add_exchange("12345678", 0, creator.getAddress()));
//            assertEquals("0x1", tx.getStatus());
//
//            tx = user.call(token.value,
//                    ERC20.approve(StaticNetwork.DCN(), BigInteger.valueOf(10000)));
//            assertEquals("0x1", tx.getStatus());
//
//            tx = user.call(StaticNetwork.DCN(),
//                    DCN.deposit_asset(0, BigInteger.valueOf(10000)));
//            assertEquals("0x1", tx.getStatus());
//        });
//
//        it("should not be able to transfer more than balance", () -> {
//            EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
//                    DCN.transfer_to_session(0, 0, 1001));
//
//            assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
//            assertEquals("0x0", user.waitForResult(tx).getStatus());
//        });
//
//        it("should transfer to session", () -> {
//            TransactionReceipt tx = user.call(StaticNetwork.DCN(),
//                    DCN.transfer_to_session(0, 0, 100));
//            assertEquals("0x1", tx.getStatus());
//
//            assertEquals(1, tx.getLogs().size());
//
//            DCN.PositionUpdated positionUpdated = DCN.ExtractPositionUpdated(tx.getLogs().get(0));
//            assertNotNull(positionUpdated);
//            assertEquals(0, positionUpdated.asset_id);
//            assertEquals(0, positionUpdated.exchange_id);
//            assertEquals(user.getAddress(), positionUpdated.user);
//
//            DCN.GetBalanceReturnValue balance = dcnQ.query(DCN::query_get_balance,
//                    DCN.get_balance(user.getAddress(), 0));
//            assertEquals(BigInteger.valueOf(10000 - 100 * 10), balance.return_balance);
//
//            DCN.GetSessionBalanceReturnValue sessionBalance = dcnQ.query(
//                    DCN::query_get_session_balance,
//                    DCN.get_session_balance(user.getAddress(), 0, 0)
//            );
//
//            assertEquals(100, sessionBalance.asset_balance);
//            assertEquals(100, sessionBalance.total_deposit);
//        });
//
//        it("should fail to overdraft from session", () -> {
//            TransactionReceipt tx = user.call(StaticNetwork.DCN(),
//                    DCN.transfer_from_session(0, 0, 101));
//            assertEquals("0x0", tx.getStatus());
//        });
//
//        it("should transfer from session", () -> {
//            TransactionReceipt tx = user.call(StaticNetwork.DCN(),
//                    DCN.transfer_from_session(0, 0, 50));
//            assertEquals("0x1", tx.getStatus());
//
//            assertEquals(1, tx.getLogs().size());
//
//            DCN.PositionUpdated positionUpdated = DCN.ExtractPositionUpdated(tx.getLogs().get(0));
//            assertNotNull(positionUpdated);
//            assertEquals(0, positionUpdated.asset_id);
//            assertEquals(0, positionUpdated.exchange_id);
//            assertEquals(user.getAddress(), positionUpdated.user);
//
//            DCN.GetBalanceReturnValue balance = dcnQ.query(DCN::query_get_balance,
//                    DCN.get_balance(user.getAddress(), 0));
//            assertEquals(BigInteger.valueOf(10000 - 50 * 10), balance.return_balance);
//
//            DCN.GetSessionBalanceReturnValue sessionBalance = dcnQ.query(
//                    DCN::query_get_session_balance,
//                    DCN.get_session_balance(user.getAddress(), 0, 0)
//            );
//
//            assertEquals(50, sessionBalance.asset_balance);
//            assertEquals(100, sessionBalance.total_deposit);
//        });
//    }
//}
