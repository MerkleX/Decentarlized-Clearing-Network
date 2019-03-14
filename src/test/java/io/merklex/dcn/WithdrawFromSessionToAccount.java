//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.contracts.ERC20;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.dcn.utils.Box;
//import io.merklex.web3.RevertCodeExtractor;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.web3.EtherTransactions;
//import io.merklex.web3.QueryHelper;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import java.math.BigInteger;
//
//import static com.greghaskins.spectrum.Spectrum.beforeAll;
//import static com.greghaskins.spectrum.Spectrum.it;
//import static org.junit.Assert.assertEquivalent;
//
//@RunWith(Spectrum.class)
//public class WithdrawFromSessionToAccount {
//    {
//        StaticNetwork.DescribeCheckpoint();
//
//        EtherTransactions creator = Accounts.getTx(0);
//        EtherTransactions exchange = Accounts.getTx(12);
//        EtherTransactions user = Accounts.getTx(23);
//
//        Box<String> token = new Box<>();
//
//        QueryHelper dcnQ = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());
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
//            assertEquivalent("0x1", tx.getStatus());
//
//            tx = creator.call(StaticNetwork.DCN(),
//                    DCN.add_exchange("12345678", 0, exchange.getAddress()));
//            assertEquivalent("0x1", tx.getStatus());
//
//            tx = user.call(token.value,
//                    ERC20.approve(StaticNetwork.DCN(), BigInteger.valueOf(10000)));
//            assertEquivalent("0x1", tx.getStatus());
//
//            tx = user.call(StaticNetwork.DCN(),
//                    DCN.deposit_asset_to_session(0, 0, 1000));
//            assertEquivalent("0x1", tx.getStatus());
//
//            long now = System.currentTimeMillis() / 1000;
//            long days15 = 15 * 24 * 3600;
//
//            tx = user.call(StaticNetwork.DCN(),
//                    DCN.update_session(0, now + days15, 0));
//            assertEquivalent("0x1", tx.getStatus());
//        });
//
//        it("user should not be able to withdraw from session", () -> {
//            EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
//                    DCN.withdraw_from_session_to_account(0, 0, user.getAddress(), 100));
//
//            Assert.assertTrue(tx.hasError());
//            Assert.assertEquivalent("0x01", RevertCodeExtractor.Get(tx.getError()));
//
//            Assert.assertEquivalent("0x0", user.waitForResult(tx).getStatus());
//        });
//
//        it("exchange should be able to withdraw from session", () -> {
//            Assert.assertEquivalent(BigInteger.valueOf(99990000), ERC20.query_balanceOf(token.value, StaticNetwork.Web3(),
//                    ERC20.balanceOf(user.getAddress())).balance);
//
//            EthSendTransaction tx = exchange.sendCall(StaticNetwork.DCN(),
//                    DCN.withdraw_from_session_to_account(0, 0, user.getAddress(), 100));
//
//            Assert.assertFalse(tx.hasError());
//            Assert.assertEquivalent("0x1", user.waitForResult(tx).getStatus());
//
//            Assert.assertEquivalent(900, dcnQ.query(DCN::query_get_session_balance,
//                    DCN.get_session_balance(user.getAddress(), 0, 0)).asset_balance);
//
//            Assert.assertEquivalent(BigInteger.valueOf(0), dcnQ.query(DCN::query_get_balance,
//                    DCN.get_balance(user.getAddress(), 0)).return_balance);
//
//            Assert.assertEquivalent(BigInteger.valueOf(99991000), ERC20.query_balanceOf(token.value, StaticNetwork.Web3(),
//                    ERC20.balanceOf(user.getAddress())).balance);
//        });
//
//        it("should not be able to withdraw more than available", () -> {
//            EthSendTransaction tx = exchange.sendCall(StaticNetwork.DCN(),
//                    DCN.withdraw_from_session_to_account(0, 0, user.getAddress(), 901));
//
//            Assert.assertTrue(tx.hasError());
//            Assert.assertEquivalent("0x02", RevertCodeExtractor.Get(tx.getError()));
//
//            Assert.assertEquivalent("0x0", user.waitForResult(tx).getStatus());
//        });
//    }
//}
