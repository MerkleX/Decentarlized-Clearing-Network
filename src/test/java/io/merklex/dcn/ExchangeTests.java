//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.dcn.utils.RevertCodeExtractor;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.web3.EtherTransactions;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import static com.greghaskins.spectrum.Spectrum.describe;
//import static com.greghaskins.spectrum.dsl.specification.Specification.it;
//import static org.junit.Assert.assertEquivalent;
//
//@RunWith(Spectrum.class)
//public class ExchangeTests {
//    {
//        StaticNetwork.DescribeCheckpoint();
//
//        EtherTransactions creator = Accounts.getTx(0);
//        EtherTransactions exchangeOwner0 = Accounts.getTx(1);
//        EtherTransactions exchangeOwner1 = Accounts.getTx(2);
//        EtherTransactions newOwner = Accounts.getTx(3);
//        EtherTransactions newBackup = Accounts.getTx(4);
//        EtherTransactions newOwner2 = Accounts.getTx(5);
//
//        describe("add exchange", () -> {
//            it("initial exchange count should be zero", () -> {
//                int count = DCN.query_get_exchange_count(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_exchange_count()
//                ).count;
//
//                assertEquivalent(0, count);
//            });
//
//            it("should not be able to add exchange with invalid quote asset", () -> {
//                EthSendTransaction tx = creator.sendCall(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("merklex ", 0, exchangeOwner0.credentials().getAddress())
//                );
//                Assert.assertEquivalent("0x03", RevertCodeExtractor.Get(tx.getError()));
//                Assert.assertEquivalent("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("creator should be able to create exchange", () -> {
//                creator.call(StaticNetwork.DCN(), DCN.add_asset("abcd", 1, "0x1"));
//
//                TransactionReceipt receipt = creator.call(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("merklex ", 0, exchangeOwner0.credentials().getAddress())
//                );
//                assertEquivalent("0x1", receipt.getStatus());
//
//                int count = DCN.query_get_exchange_count(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_exchange_count()
//                ).count;
//
//                assertEquivalent(1, count);
//
//                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_exchange(0)
//                );
//
//                assertEquivalent(0, exchange.fee_balance);
//                assertEquivalent(exchangeOwner0.credentials().getAddress(), exchange.addr);
//                assertEquivalent("merklex ", exchange.name);
//                assertEquivalent(exchangeOwner0.credentials().getAddress(), exchange.owner_backup);
//                assertEquivalent("0x0000000000000000000000000000000000000000", exchange.owner_backup_proposed);
//            });
//
//            it("non creator should fail to add exchange", () -> {
//                EthSendTransaction tx = exchangeOwner0.sendCall(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("bobs network", 0, exchangeOwner0.credentials().getAddress())
//                );
//                Assert.assertTrue(tx.hasError());
//                Assert.assertEquivalent("0x01", RevertCodeExtractor.Get(tx.getError()));
//
//                TransactionReceipt receipt = exchangeOwner0.waitForResult(tx);
//                assertEquivalent("0x0", receipt.getStatus());
//
//                int count = DCN.query_get_exchange_count(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_exchange_count()
//                ).count;
//
//                assertEquivalent(1, count);
//            });
//
//            it("should not be able to create exchange with 5 char name", () -> {
//                TransactionReceipt receipt = creator.call(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("12345", 0, exchangeOwner0.credentials().getAddress())
//                );
//                assertEquivalent("0x0", receipt.getStatus());
//            });
//
//            it("should not be able to create exchange with 15 char name", () -> {
//                TransactionReceipt receipt = creator.call(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("boby network :)", 0, exchangeOwner0.credentials().getAddress())
//                );
//                assertEquivalent("0x0", receipt.getStatus());
//            });
//
//            it("second exchange should not effect first", () -> {
//                TransactionReceipt receipt = creator.call(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("12345678", 0, exchangeOwner1.credentials().getAddress())
//                );
//                assertEquivalent("0x1", receipt.getStatus());
//
//                int count = DCN.query_get_exchange_count(
//                        StaticNetwork.DCN(),
//                        StaticNetwork.Web3(),
//                        DCN.get_exchange_count()
//                ).count;
//
//                assertEquivalent(2, count);
//
//                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_exchange(0)
//                );
//
//                assertEquivalent(0, exchange.fee_balance);
//                assertEquivalent(exchangeOwner0.credentials().getAddress(), exchange.addr);
//                assertEquivalent("merklex ", exchange.name);
//
//                exchange = DCN.query_get_exchange(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_exchange(1)
//                );
//
//                assertEquivalent(0, exchange.fee_balance);
//                assertEquivalent(exchangeOwner1.credentials().getAddress(), exchange.addr);
//                assertEquivalent("12345678", exchange.name);
//            });
//
//            it("should not be able to add exchange with invalid quote_asset", () -> {
//                TransactionReceipt receipt = creator.call(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("12 45 78", 1, exchangeOwner0.credentials().getAddress())
//                );
//                assertEquivalent("0x0", receipt.getStatus());
//            });
//
//            it("should be able to add exchange with non eth asset", () -> {
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 100, "0x1"));
//                assertEquivalent("0x1", tx.getStatus());
//
//                TransactionReceipt receipt = creator.call(
//                        StaticNetwork.DCN(),
//                        DCN.add_exchange("12 45 78", 1, exchangeOwner0.credentials().getAddress())
//                );
//                assertEquivalent("0x1", receipt.getStatus());
//            });
//
//            it("non owner should not be able to update owner", () -> {
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(),
//                        DCN.exchange_update_owner(0, newOwner.getAddress()));
//                assertEquivalent("0x0", tx.getStatus());
//            });
//
//            it("should be able to update owner", () -> {
//                TransactionReceipt tx = exchangeOwner0.call(StaticNetwork.DCN(),
//                        DCN.exchange_update_owner(0, newOwner.getAddress()));
//                assertEquivalent("0x1", tx.getStatus());
//
//                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_exchange(0)
//                );
//
//                Assert.assertEquivalent(newOwner.getAddress(), exchange.addr);
//            });
//
//            it("non owner should not be able to proposed backup", () -> {
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(),
//                        DCN.exchange_propose_backup(0, newBackup.getAddress()));
//                assertEquivalent("0x0", tx.getStatus());
//            });
//
//            it("should be able to proposed backup", () -> {
//                TransactionReceipt tx = exchangeOwner0.call(StaticNetwork.DCN(),
//                        DCN.exchange_propose_backup(0, newBackup.getAddress()));
//                assertEquivalent("0x1", tx.getStatus());
//
//                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_exchange(0)
//                );
//
//                Assert.assertEquivalent(newOwner.getAddress(), exchange.addr);
//                Assert.assertEquivalent(exchangeOwner0.getAddress(), exchange.owner_backup);
//                Assert.assertEquivalent(newBackup.getAddress(), exchange.owner_backup_proposed);
//            });
//
//            it("non proposed backup should not be able to set proposed", () -> {
//                TransactionReceipt tx = exchangeOwner0.call(StaticNetwork.DCN(),
//                        DCN.exchange_set_backup(0));
//                assertEquivalent("0x0", tx.getStatus());
//
//                tx = newOwner.call(StaticNetwork.DCN(),
//                        DCN.exchange_set_backup(0));
//                assertEquivalent("0x0", tx.getStatus());
//
//                tx = creator.call(StaticNetwork.DCN(),
//                        DCN.exchange_set_backup(0));
//                assertEquivalent("0x0", tx.getStatus());
//            });
//
//            it("should be able to set backup", () -> {
//                TransactionReceipt tx = newBackup.call(StaticNetwork.DCN(),
//                        DCN.exchange_set_backup(0));
//                assertEquivalent("0x1", tx.getStatus());
//
//                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_exchange(0)
//                );
//
//                Assert.assertEquivalent(newOwner.getAddress(), exchange.addr);
//                Assert.assertEquivalent(newBackup.getAddress(), exchange.owner_backup);
//                Assert.assertEquivalent(newBackup.getAddress(), exchange.owner_backup_proposed);
//            });
//        });
//    }
//}
