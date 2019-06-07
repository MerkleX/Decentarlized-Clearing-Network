package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.runner.RunWith;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

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
        Box<String> token = new Box<>();

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        int userId1 = 0;
        int userId2 = 1;
        int exchangeId = 0;

        int quoteAssetId = 0;
        int baseAssetId = 1;

        int unlockSeconds = 28800 * 2;

        beforeAll(() -> {
            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("merklex    ", exchange.getAddress())));


            BigInteger unlockAt = BigInteger.valueOf(
                    System.currentTimeMillis() / 1000 + unlockSeconds
            );

            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(userId1, exchangeId, unlockAt)));
            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(userId2, exchangeId, unlockAt)));

            BigInteger totalSupply = BigInteger.valueOf(1000000000);
            token.value = creator.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT,
                    ERC20.DeployData(totalSupply, "T", 2, "TT"),
                    BigInteger.ZERO);

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("abcd1234", 1, token.value)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("abce1234", 1, token.value)));

            assertSuccess(creator.sendCall(token.value,
                    ERC20.approve(StaticNetwork.DCN(), totalSupply)));

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId1, exchangeId, quoteAssetId, 1000)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId1, exchangeId, baseAssetId, 1000)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId2, exchangeId, quoteAssetId, 1000)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId2, exchangeId, baseAssetId, 1000)));
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
            assertEquals(0, state.quote_qty);
            assertEquals(0, state.base_qty);
            ShouldMatch(limitUpdate, state);
        });

        it("should fail to apply with same limit version", () -> {
            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertRevert("0x08", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));
        });

        it("should fail with invalid signature", () -> {
            limitUpdate.limitVersion(2);

            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertRevert("0x06", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));
        });

        it("should properly apply shift", () -> {
            limitUpdate
                    .limitVersion(2)
                    .quoteShift(BigInteger.valueOf(10000))
                    .baseShift(BigInteger.valueOf(-1000))
                    .sign(user1.credentials(), DCNHasher.instance);

            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));

            DCN.GetMarketStateReturnValue state = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));
            assertEquals(10000, state.quote_qty);
            assertEquals(-1000, state.base_qty);
            ShouldMatch(limitUpdate, state);

            limitUpdate
                    .limitVersion(3)
                    .quoteShift(BigInteger.valueOf(10000))
                    .baseShift(BigInteger.valueOf(2000))
                    .sign(user1.credentials(), DCNHasher.instance);


            payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));

            state = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));
            assertEquals(10000, state.quote_qty);
            assertEquals(2000, state.base_qty);
            ShouldMatch(limitUpdate, state);

            limitUpdate
                    .limitVersion(4)
                    .quoteShift(BigInteger.valueOf(-10000))
                    .baseShift(BigInteger.valueOf(1000))
                    .sign(user1.credentials(), DCNHasher.instance);

            payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));

            state = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));
            assertEquals(-10000, state.quote_qty);
            assertEquals(1000, state.base_qty);
            ShouldMatch(limitUpdate, state);
        });

        it("shift should should overflow quantity", () -> {
            limitUpdate
                    .limitVersion(10)
                    .minQuoteQty(-10000)
                    .minBaseQty(-10000)
                    .longMaxPrice(1_00000000)
                    .quoteShift(BigInteger.valueOf(0))
                    .baseShift(BigInteger.valueOf(0))
                    .sign(user1.credentials(), DCNHasher.instance);
            limitUpdate.nextLimitUpdate(limitUpdate)
                    .dcnId(1)
                    .userId(userId2)
                    .exchangeId(0)
                    .quoteAssetId(quoteAssetId)
                    .baseAssetId(baseAssetId)
                    .feeLimit(1000)
                    .minBaseQty(-10000)
                    .minQuoteQty(-10000)
                    .shortMinPrice(1000)
                    .limitVersion(1)
                    .quoteShiftMajor(0)
                    .quoteShift(0)
                    .baseShiftMajor(0)
                    .baseShift(0)
                    .sign(user2.credentials(), DCNHasher.instance);

            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(2), true);
            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));

            DCN.GetMarketStateReturnValue state;

            state = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));
            assertEquals(0, state.quote_qty);
            assertEquals(0, state.base_qty);
            ShouldMatch(updateLimits.firstLimitUpdate(limitUpdate), state);

            state = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId2, exchangeId, quoteAssetId, baseAssetId));
            assertEquals(0, state.quote_qty);
            assertEquals(0, state.base_qty);
            ShouldMatch(limitUpdate.nextLimitUpdate(limitUpdate), state);


            byte[] settlementBytes = new byte[1024];
            UnsafeBuffer settlementBuffer = new UnsafeBuffer(settlementBytes);

            Settlements settlements = new Settlements().wrap(settlementBuffer, 0);
            Settlements.SettlementData settlement = new Settlements.SettlementData();
            settlements
                    .exchangeId(exchangeId)
                    .firstGroup(new Settlements.Group())
                    .userCount(2)
                    .quoteAssetId(quoteAssetId)
                    .baseAssetId(baseAssetId)
                    .firstSettlement(settlement)
                    .userId(userId1)
                    .baseDelta(1000)
                    .quoteDelta(-1000)
                    .nextSettlement(settlement)
                    .userId(userId2)
                    .baseDelta(-1000)
                    .quoteDelta(1000);

            payload = Numeric.toHexString(settlementBytes, 0, settlements.bytes(1, new Settlements.Group()), true);
            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_apply_settlement_groups(payload)));
        });

        it("should fail to reset trading limit while locked", () -> {
            assertRevert("0x03", user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_market_reset(userId1, exchangeId, quoteAssetId, baseAssetId)));
        });

        it("should reset trading limit while unlocked", () -> {
            StaticNetwork.IncreaseTime(unlockSeconds + 100);

            DCN.GetMarketStateReturnValue marketStateBefore = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));

            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_market_reset(userId1, exchangeId, quoteAssetId, baseAssetId)));

            DCN.GetMarketStateReturnValue marketStateAfter = query.query(DCN::query_get_market_state,
                    DCN.get_market_state(userId1, exchangeId, quoteAssetId, baseAssetId));

            assertEquals(marketStateBefore.limit_version + 1, marketStateAfter.limit_version);
            assertEquals(0, marketStateAfter.quote_qty);
            assertEquals(0, marketStateAfter.base_qty);
            assertEquals(BigInteger.ZERO, marketStateAfter.base_shift);
            assertEquals(BigInteger.ZERO, marketStateAfter.quote_shift);
            assertEquals(0, marketStateAfter.short_min_price);
            assertEquals(0, marketStateAfter.long_max_price);
            assertEquals(0, marketStateAfter.min_base_qty);
            assertEquals(0, marketStateAfter.min_quote_qty);
        });

        it("should be able to set limit with session unlocked", () -> {
            updateLimits.firstLimitUpdate(limitUpdate)
                    .limitVersion(12)
                    .minQuoteQty(-10000)
                    .minBaseQty(-10000)
                    .longMaxPrice(1_00000000)
                    .quoteShift(BigInteger.valueOf(0))
                    .baseShift(BigInteger.valueOf(0))
                    .sign(user1.credentials(), DCNHasher.instance);

            String payload = Numeric.toHexString(bytes, 0, updateLimits.bytes(1), true);
            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(payload)));
        });
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
