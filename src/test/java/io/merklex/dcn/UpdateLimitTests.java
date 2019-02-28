//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.dcn.utils.RevertCodeExtractor;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.web3.EtherTransactions;
//import io.merklex.web3.QueryHelper;
//import org.agrona.concurrent.UnsafeBuffer;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//import org.web3j.utils.Numeric;
//
//import static com.greghaskins.spectrum.Spectrum.beforeAll;
//import static com.greghaskins.spectrum.Spectrum.it;
//
//@RunWith(Spectrum.class)
//public class UpdateLimitTests {
//    {
//        StaticNetwork.DescribeCheckpoint();
//
//        EtherTransactions creator = Accounts.getTx(0);
//        EtherTransactions exchange = Accounts.getTx(1);
//        EtherTransactions user1 = Accounts.getTx(2);
//        EtherTransactions user2 = Accounts.getTx(3);
//        EtherTransactions user3 = Accounts.getTx(4);
//
//        int quoteAssetId = 0;
//        int baseAssetId = 1;
//
//        beforeAll(() -> {
//            TransactionReceipt tx;
//
//            tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("abcd", 1, creator.getAddress()));
//            Assert.assertEquals("0x1", tx.getStatus());
//
//            tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("abce", 1, creator.getAddress()));
//            Assert.assertEquals("0x1", tx.getStatus());
//
//            tx = creator.call(StaticNetwork.DCN(), DCN.add_exchange("merklex ", quoteAssetId, exchange.getAddress()));
//            Assert.assertEquals("0x1", tx.getStatus());
//        });
//
//        QueryHelper helper = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());
//
//        byte[] bytes = new byte[1024];
//        UnsafeBuffer buffer = new UnsafeBuffer(bytes);
//        UpdateLimit updateLimit = new UpdateLimit()
//                .wrap(buffer, 0);
//
//        it("should fail to update limit for quote asset", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(quoteAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(3)
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            Assert.assertEquals("0x06", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x0", call.getStatus());
//        });
//
//        it("should apply limit with valid signature", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(3)
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertFalse(ethSendTransaction.hasError());
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x1", call.getStatus());
//
//            DCN.GetSessionStateReturnValue query = helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user1.getAddress(), 0, baseAssetId));
//
//            ShouldMatch(updateLimit, query);
//        });
//
//        it("should fail to apply same version", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(3)
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            Assert.assertEquals("0x03", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x0", call.getStatus());
//        });
//
//        it("should fail to apply older version", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(1)
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            Assert.assertEquals("0x03", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x0", call.getStatus());
//        });
//
//        it("should fail to apply with invalid signature", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(5);
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            Assert.assertEquals("0x05", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x0", call.getStatus());
//        });
//
//        it("should be able to apply multiple updates", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(5)
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//            updateLimit.wrap(buffer, UpdateLimit.BYTES)
//                    .user(user2.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(1241)
//                    .minQuoteQty(-1130)
//                    .maxLongPrice(32141)
//                    .minShortPrice(123)
//                    .quoteShift(-12543)
//                    .baseShift(10)
//                    .version(1)
//                    .signature(user2.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//            updateLimit.wrap(buffer, UpdateLimit.BYTES * 2)
//                    .user(user3.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(76)
//                    .minQuoteQty(-1)
//                    .maxLongPrice(412)
//                    .minShortPrice(5)
//                    .quoteShift(-3214)
//                    .baseShift(3)
//                    .version(1)
//                    .signature(user3.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES * 3, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertFalse(ethSendTransaction.hasError());
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x1", call.getStatus());
//
//            ShouldMatch(updateLimit.wrap(buffer, 0), helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user1.getAddress(), 0, baseAssetId)));
//
//            ShouldMatch(updateLimit.wrap(buffer, UpdateLimit.BYTES), helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user2.getAddress(), 0, baseAssetId)));
//
//            ShouldMatch(updateLimit.wrap(buffer, UpdateLimit.BYTES * 2), helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user3.getAddress(), 0, baseAssetId)));
//        });
//
//        it("single bad update should stop everything", () -> {
//            updateLimit.wrap(buffer, 0)
//                    .user(user1.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(-100)
//                    .minQuoteQty(-100)
//                    .maxLongPrice(100000)
//                    .minShortPrice(1000)
//                    .quoteShift(0)
//                    .baseShift(10)
//                    .version(6)
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//            updateLimit.wrap(buffer, UpdateLimit.BYTES)
//                    .user(user2.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(1241)
//                    .minQuoteQty(-1130)
//                    .maxLongPrice(32141)
//                    .minShortPrice(123)
//                    .quoteShift(-12543)
//                    .baseShift(10)
//                    .version(2);
//            updateLimit.wrap(buffer, UpdateLimit.BYTES * 2)
//                    .user(user3.getAddress())
//                    .exchangeId(0)
//                    .assetId(baseAssetId)
//                    .minBaseQty(76)
//                    .minQuoteQty(-1)
//                    .maxLongPrice(412)
//                    .minShortPrice(5)
//                    .quoteShift(-3214)
//                    .baseShift(3)
//                    .version(2)
//                    .signature(user3.signHash(DCNHasher.instance.hash(updateLimit.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, UpdateLimit.BYTES * 2, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            Assert.assertEquals("0x05", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            Assert.assertEquals("0x0", call.getStatus());
//        });
//    }
//
//    private static void ShouldMatch(UpdateLimit updateLimit, DCN.GetSessionStateReturnValue dcn) {
//        Assert.assertEquals(updateLimit.quoteShift(), dcn.quote_shift);
//        Assert.assertEquals(updateLimit.baseShift(), dcn.base_shift);
//        Assert.assertEquals(updateLimit.maxLongPrice(), dcn.long_max_price);
//        Assert.assertEquals(updateLimit.minShortPrice(), dcn.short_min_price);
//        Assert.assertEquals(updateLimit.version(), dcn.version);
//        Assert.assertEquals(updateLimit.minQuoteQty(), dcn.min_quote);
//        Assert.assertEquals(updateLimit.minBaseQty(), dcn.min_base);
//    }
//}
