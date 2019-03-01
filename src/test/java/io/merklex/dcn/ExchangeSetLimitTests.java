package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.runner.RunWith;
import org.web3j.utils.Numeric;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExchangeSetLimitTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchange = Accounts.getTx(1);
        EtherTransactions user1 = Accounts.getTx(2);
        EtherTransactions user2 = Accounts.getTx(3);
        EtherTransactions user3 = Accounts.getTx(4);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        int userId1 = 0;
        int userId2 = 1;
        int userId3 = 2;
        int exchangeId = 0;

        int quoteAssetId = 0;
        int baseAssetId = 1;

        beforeAll(() -> {
            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user3.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("abcd", 1, creator.getAddress())));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("abce", 1, creator.getAddress())));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("merklex    ", exchange.getAddress())));
        });

        byte[] bytes = new byte[1024];
        UnsafeBuffer buffer = new UnsafeBuffer(bytes);
        UpdateLimits updateLimits = new UpdateLimits()
                .wrap(buffer, 0);


        UpdateLimits.LimitUpdate limitUpdate = new UpdateLimits.LimitUpdate();

        updateLimits.wrap(buffer, 0)
                .exchangeId(exchangeId)
                .firstLimitUpdate(limitUpdate)
                .dcnId(1)
                .userId(userId1)
                .exchangeId(0)
                .quoteAssetId(quoteAssetId)
                .baseAssetId(baseAssetId)
                .feeLimit(1000)
                .minBaseQty(-100)
                .minQuoteQty(-100)
                .longMaxPrice(100000)
                .shortMinPrice(1000)
                .limitVersion(1)
                .quoteShiftMajor(0)
                .quoteShift(0)
                .baseShiftMajor(0)
                .baseShift(0)
                .sign(user1.credentials(), DCNHasher.instance);

        it("only exchange should be able update limit", () -> {
            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertRevert("0x02", creator.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));

            DCN.GetMarketStateReturnValue state = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));
            ShouldMatch(limitUpdate, state);
        });

        it("should fail to apply with same limit version", () -> {
            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertRevert("0x07", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));
        });

//        it("should apply limit with valid signature", () -> {
//            updateLimits.wrap(buffer, 0)
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
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, LimitUpdate.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertFalse(ethSendTransaction.hasError());
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            assertEquals("0x1", call.getStatus());
//
//            DCN.GetSessionStateReturnValue query = helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user1.getAddress(), 0, baseAssetId));
//
//            ShouldMatch(updateLimits, query);
//        });
//
//        it("should fail to apply same version", () -> {
//            updateLimits.wrap(buffer, 0)
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
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, LimitUpdate.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            assertEquals("0x03", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            assertEquals("0x0", call.getStatus());
//        });
//
//        it("should fail to apply older version", () -> {
//            updateLimits.wrap(buffer, 0)
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
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, LimitUpdate.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            assertEquals("0x03", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            assertEquals("0x0", call.getStatus());
//        });
//
//        it("should fail to apply with invalid signature", () -> {
//            updateLimits.wrap(buffer, 0)
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
//            String payload = Numeric.toHexString(bytes, 0, LimitUpdate.BYTES, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            assertEquals("0x05", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            assertEquals("0x0", call.getStatus());
//        });
//
//        it("should be able to apply multiple updates", () -> {
//            updateLimits.wrap(buffer, 0)
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
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//            updateLimits.wrap(buffer, LimitUpdate.BYTES)
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
//                    .signature(user2.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//            updateLimits.wrap(buffer, LimitUpdate.BYTES * 2)
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
//                    .signature(user3.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, LimitUpdate.BYTES * 3, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertFalse(ethSendTransaction.hasError());
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            assertEquals("0x1", call.getStatus());
//
//            ShouldMatch(updateLimits.wrap(buffer, 0), helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user1.getAddress(), 0, baseAssetId)));
//
//            ShouldMatch(updateLimits.wrap(buffer, LimitUpdate.BYTES), helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user2.getAddress(), 0, baseAssetId)));
//
//            ShouldMatch(updateLimits.wrap(buffer, LimitUpdate.BYTES * 2), helper.query(DCN::query_get_session_state,
//                    DCN.get_session_state(user3.getAddress(), 0, baseAssetId)));
//        });
//
//        it("single bad update should stop everything", () -> {
//            updateLimits.wrap(buffer, 0)
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
//                    .signature(user1.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//            updateLimits.wrap(buffer, LimitUpdate.BYTES)
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
//            updateLimits.wrap(buffer, LimitUpdate.BYTES * 2)
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
//                    .signature(user3.signHash(DCNHasher.instance.hash(updateLimits.hash())));
//
//            String payload = Numeric.toHexString(bytes, 0, LimitUpdate.BYTES * 2, true);
//            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));
//
//            Assert.assertTrue(ethSendTransaction.hasError());
//            assertEquals("0x05", RevertCodeExtractor.Get(ethSendTransaction.getError()));
//
//            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
//            assertEquals("0x0", call.getStatus());
//        });
    }

    private static void ShouldMatch(UpdateLimits.LimitUpdate updateLimit, DCN.GetMarketStateReturnValue marketState) {
        assertEquals(updateLimit.feeLimit(), marketState.fee_limit);

        assertEquals(updateLimit.minQuoteQty(), marketState.min_quote_qty);
        assertEquals(updateLimit.minBaseQty(), marketState.min_base_qty);
        assertEquals(updateLimit.longMaxPrice(), marketState.long_max_price);
        assertEquals(updateLimit.shortMinPrice(), marketState.short_min_price);

        assertEquals(updateLimit.limitVersion(), marketState.limit_version);
        assertEquals(updateLimit.quoteShiftBig(), marketState.quote_shift);
        assertEquals(updateLimit.baseShiftBig(), marketState.base_shift);
    }
}
