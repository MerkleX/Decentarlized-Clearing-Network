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

import java.math.BigInteger;
import java.util.function.Consumer;

import static com.greghaskins.spectrum.Spectrum.*;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;

@RunWith(Spectrum.class)
public class ApplySettlementsTests {

    private static final int userId1 = 0;
    private static final int userId2 = 1;
    private static final int userId3 = 2;
    private static final int exchangeId = 0;
    private static final int quoteAssetId = 0;
    private static final int baseAssetId = 1;

    {
        StaticNetwork.DescribeCheckpoint();
        StaticNetwork.DescribeCheckpointForEach();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchange = Accounts.getTx(5);
        EtherTransactions user1 = Accounts.getTx(1);
        EtherTransactions user2 = Accounts.getTx(2);
        EtherTransactions user3 = Accounts.getTx(3);
        Box<String> token = new Box<>();

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        beforeAll(() -> {
            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user3.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("merklex    ", exchange.getAddress())));

            BigInteger totalSupply = BigInteger.valueOf(1000000000);
            token.value = creator.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT,
                    ERC20.DeployData(totalSupply, "T", 2, "TT"),
                    BigInteger.ZERO);

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("abcd", 1, token.value)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("abce", 1, token.value)));

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
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId3, exchangeId, quoteAssetId, 1000)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId3, exchangeId, baseAssetId, 1000)));
        });

        byte[] updateLimitBytes = new byte[1024];
        UnsafeBuffer updateLimitBuffer = new UnsafeBuffer(updateLimitBytes);
        UpdateLimits updateLimits = new UpdateLimits()
                .wrap(updateLimitBuffer, 0);
        UpdateLimits.LimitUpdate limitUpdate = new UpdateLimits.LimitUpdate();

        byte[] settlementBytes = new byte[1024];
        UnsafeBuffer settlementBuffer = new UnsafeBuffer(settlementBytes);
        Settlements settlements = new Settlements()
                .wrap(settlementBuffer, 0);
        Settlements.Group settlementGroup = new Settlements.Group();
        Settlements.SettlementData settlementData = new Settlements.SettlementData();

        describe("should check limit", () -> {
            StaticNetwork.DescribeCheckpointForEach();

            it("min_quote_qty", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(-10)
                        .minBaseQty(0)
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(-100)
                        .minBaseQty(-100)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-11)
                        .baseDelta(100)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(11)
                        .baseDelta(-100)
                        .fees(0);

                assertRevert("0x09", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-10)
                        .baseDelta(100)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(10)
                        .baseDelta(-100)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("min_base_qty", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(0)
                        .minBaseQty(-10)
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(-100)
                        .minBaseQty(-100)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(100)
                        .baseDelta(-11)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-100)
                        .baseDelta(11)
                        .fees(0);

                assertRevert("0x09", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(100)
                        .baseDelta(-10)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-100)
                        .baseDelta(10)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("quote_qty overflow protection", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(-10000)
                        .minBaseQty(-10000)
                        .quoteShift(BigInteger.ONE.shiftLeft(63).subtract(BigInteger.TEN))
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(-10000)
                        .minBaseQty(-10000)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(10)
                        .baseDelta(-5)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-10)
                        .baseDelta(5)
                        .fees(0);

                assertRevert("0x07", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(9)
                        .baseDelta(-5)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-9)
                        .baseDelta(5)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("quote_qty underflow protection", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .quoteShift(BigInteger.ONE.shiftLeft(63).negate().add(BigInteger.ONE))
                        .baseShift(BigInteger.ONE.shiftLeft(62))
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-2)
                        .baseDelta(3)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(2)
                        .baseDelta(-3)
                        .fees(0);

                assertRevert("0x07", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-1)
                        .baseDelta(3)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(1)
                        .baseDelta(-3)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });


            it("base_qty overflow protection", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(-10000)
                        .minBaseQty(-10000)
                        .baseShift(BigInteger.ONE.shiftLeft(63).subtract(BigInteger.TEN))
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(-10000)
                        .minBaseQty(-10000)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-5)
                        .baseDelta(10)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(5)
                        .baseDelta(-10)
                        .fees(0);

                assertRevert("0x07", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-5)
                        .baseDelta(9)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(5)
                        .baseDelta(-9)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("base_qty underflow protection", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .baseShift(BigInteger.ONE.shiftLeft(63).negate().add(BigInteger.ONE))
                        .quoteShift(BigInteger.ONE.shiftLeft(62))
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(3)
                        .baseDelta(-2)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-3)
                        .baseDelta(2)
                        .fees(0);

                assertRevert("0x07", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(3)
                        .baseDelta(-1)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-3)
                        .baseDelta(1)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("fee_limit", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .feeLimit(100)
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(3)
                        .baseDelta(-1)
                        .fees(101)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-3)
                        .baseDelta(1)
                        .fees(0);

                assertRevert("0x08", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(3)
                        .baseDelta(-1)
                        .fees(100)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-3)
                        .baseDelta(1)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("negative position", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .baseShift(BigInteger.valueOf(10).negate())
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .baseShift(BigInteger.valueOf(200))
                        .quoteShift(BigInteger.valueOf(200))
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-11)
                        .baseDelta(9)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(11)
                        .baseDelta(-9)
                        .fees(0);

                assertRevert("0x0a", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-11)
                        .baseDelta(10)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(11)
                        .baseDelta(-10)
                        .fees(0);

                assertRevert("0x0a", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(0)
                        .baseDelta(9)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(0)
                        .baseDelta(-9)
                        .fees(0);

                assertRevert("0x0a", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-11)
                        .baseDelta(11)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(11)
                        .baseDelta(-11)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("long_max_price", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .feeLimit(0)
                        .longMaxPrice(1_00000000)
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-101)
                        .baseDelta(100)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(101)
                        .baseDelta(-100)
                        .fees(0);

                assertRevert("0x0b", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(-100)
                        .baseDelta(100)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(100)
                        .baseDelta(-100)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });

            it("short_min_price", () -> {
                exchange.reloadNonce();

                updateLimits
                        .exchangeId(exchangeId)
                        .firstLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId1))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .feeLimit(0)
                        .shortMinPrice(1_00000000)
                        .sign(user1.credentials(), DCNHasher.instance)

                        .nextLimitUpdate(limitUpdate)

                        .setValues(setDefault(userId2))
                        .minQuoteQty(Long.MIN_VALUE)
                        .minBaseQty(Long.MIN_VALUE)
                        .sign(user2.credentials(), DCNHasher.instance);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_set_limits(updateLimits.payload(2))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(100)
                        .baseDelta(-101)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-100)
                        .baseDelta(101)
                        .fees(0);

                assertRevert("0x0c", exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));

                settlements
                        .exchangeId(exchangeId)

                        .firstGroup(settlementGroup)
                        .quoteAssetId(quoteAssetId)
                        .baseAssetId(baseAssetId)
                        .userCount(2)

                        .firstSettlement(settlementData)
                        .userId(userId1)
                        .quoteDelta(100)
                        .baseDelta(-100)
                        .fees(0)

                        .nextSettlement(settlementData)
                        .userId(userId2)
                        .quoteDelta(-100)
                        .baseDelta(100)
                        .fees(0);

                assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_apply_settlement_groups(settlements.payload(1))));
            });
        });

        it("settlement should sum to zero", () -> {
            exchange.reloadNonce();

            updateLimits
                    .exchangeId(exchangeId)
                    .firstLimitUpdate(limitUpdate)

                    .setValues(setDefault(userId1))
                    .minQuoteQty(Long.MIN_VALUE)
                    .minBaseQty(Long.MIN_VALUE)
                    .sign(user1.credentials(), DCNHasher.instance)

                    .nextLimitUpdate(limitUpdate)

                    .setValues(setDefault(userId2))
                    .minQuoteQty(Long.MIN_VALUE)
                    .minBaseQty(Long.MIN_VALUE)
                    .sign(user2.credentials(), DCNHasher.instance)

                    .nextLimitUpdate(limitUpdate)

                    .setValues(setDefault(userId3))
                    .minQuoteQty(Long.MIN_VALUE)
                    .minBaseQty(Long.MIN_VALUE)
                    .sign(user3.credentials(), DCNHasher.instance);

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_limits(updateLimits.payload(3))));

            /* quote difference */

            settlements
                    .exchangeId(exchangeId)

                    .firstGroup(settlementGroup)
                    .quoteAssetId(quoteAssetId)
                    .baseAssetId(baseAssetId)
                    .userCount(3)

                    .firstSettlement(settlementData)
                    .userId(userId1)
                    .quoteDelta(1)
                    .baseDelta(-1)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId2)
                    .quoteDelta(2)
                    .baseDelta(-2)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId3)
                    .quoteDelta(-10)
                    .baseDelta(3)
                    .fees(0);

            assertRevert("0x0f", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_apply_settlement_groups(settlements.payload(1))));

            /* base difference */

            settlements
                    .exchangeId(exchangeId)

                    .firstGroup(settlementGroup)
                    .quoteAssetId(quoteAssetId)
                    .baseAssetId(baseAssetId)
                    .userCount(3)

                    .firstSettlement(settlementData)
                    .userId(userId1)
                    .quoteDelta(1)
                    .baseDelta(-1)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId2)
                    .quoteDelta(2)
                    .baseDelta(-1)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId3)
                    .quoteDelta(-3)
                    .baseDelta(5)
                    .fees(0);

            assertRevert("0x0f", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_apply_settlement_groups(settlements.payload(1))));

            /* quote & base difference */

            settlements
                    .exchangeId(exchangeId)

                    .firstGroup(settlementGroup)
                    .quoteAssetId(quoteAssetId)
                    .baseAssetId(baseAssetId)
                    .userCount(3)

                    .firstSettlement(settlementData)
                    .userId(userId1)
                    .quoteDelta(1)
                    .baseDelta(-1)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId2)
                    .quoteDelta(2)
                    .baseDelta(-2)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId3)
                    .quoteDelta(-10)
                    .baseDelta(5)
                    .fees(0);

            assertRevert("0x0f", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_apply_settlement_groups(settlements.payload(1))));

            /* 0 net */

            settlements
                    .exchangeId(exchangeId)

                    .firstGroup(settlementGroup)
                    .quoteAssetId(quoteAssetId)
                    .baseAssetId(baseAssetId)
                    .userCount(3)

                    .firstSettlement(settlementData)
                    .userId(userId1)
                    .quoteDelta(1)
                    .baseDelta(-1)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId2)
                    .quoteDelta(2)
                    .baseDelta(-1)
                    .fees(0)

                    .nextSettlement(settlementData)
                    .userId(userId3)
                    .quoteDelta(-3)
                    .baseDelta(2)
                    .fees(0);

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_apply_settlement_groups(settlements.payload(1))));
        });
    }

    private static Consumer<UpdateLimits.LimitUpdate> setDefault(long userId) {
        return limit -> limit
                .dcnId(1)
                .userId(userId)
                .exchangeId(exchangeId)
                .quoteAssetId(quoteAssetId)
                .baseAssetId(baseAssetId)
                .feeLimit(0)

                .minQuoteQty(0)
                .minBaseQty(0)
                .longMaxPrice(-1)
                .shortMinPrice(1)

                .limitVersion(1)
                .baseShift(BigInteger.ZERO)
                .quoteShift(BigInteger.ZERO);
    }

    private static void Run(boolean flip) {
//        describe("Run with " + (flip ? "quote,base asset order" : "base,quote asset order"), () -> {
//            StaticNetwork.DescribeCheckpoint();
//
//            EtherTransactions creator = Accounts.getTx(0);
//            EtherTransactions buyer = Accounts.getTx(1);
//            EtherTransactions seller = Accounts.getTx(2);
//
//            Box<String> baseToken = new Box<>();
//            Box<String> quoteToken = new Box<>();
//
//            long initBalance = 10_000L;
//            BigInteger qty = BigInteger.valueOf(initBalance);
//
//            final int quoteAssetId;
//            final int baseAssetId;
//
//            if (flip) {
//                quoteAssetId = 1;
//                baseAssetId = 0;
//            }
//            else {
//                quoteAssetId = 0;
//                baseAssetId = 1;
//            }
//
//            beforeAll(() -> {
//                quoteToken.value = buyer.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
//                        qty.multiply(BigInteger.TEN),
//                        "Test Token",
//                        10,
//                        "TK1"
//                ), BigInteger.ZERO);
//
//                baseToken.value = seller.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
//                        qty.multiply(BigInteger.valueOf(100)),
//                        "Test Token",
//                        10,
//                        "TK1"
//                ), BigInteger.ZERO);
//
//                if (flip) {
//                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("BASE", 100, baseToken.value)));
//                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("QTY!", 10, quoteToken.value)));
//                }
//                else {
//                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("QTY!", 10, quoteToken.value)));
//                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("BASE", 100, baseToken.value)));
//                }
//
//                success(creator.sendCall(StaticNetwork.DCN(), DCN.add_exchange("exchange", quoteAssetId, creator.getAddress())));
//
//                success(buyer.sendCall(quoteToken.value, ERC20.approve(StaticNetwork.DCN(), qty.multiply(BigInteger.TEN))));
//                success(seller.sendCall(baseToken.value, ERC20.approve(StaticNetwork.DCN(), qty.multiply(BigInteger.valueOf(100)))));
//
//                success(buyer.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, quoteAssetId, initBalance)));
//                success(seller.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, baseAssetId, initBalance)));
//            });
//
//            byte[] data = new byte[1000];
//            UnsafeBuffer buffer = new UnsafeBuffer(data);
//
//            Settlements settlements = new Settlements();
//
//            Settlements.Group group = settlements
//                    .wrap(buffer, 0)
//                    .exchangeId(0)
//                    .firstGroup(new Settlements.Group())
//                    .userCount(2)
//                    .baseAssetId(baseAssetId);
//
//            Settlements.SettlementData entry = new Settlements.SettlementData();
//            group
//                    .settlement(entry, 0)
//                    .setUserAddress(buyer.getAddress())
//                    .quoteDelta(-1000)
//                    .baseDelta(4000)
//                    .fees(10);
//
//            group
//                    .settlement(entry, 1)
//                    .setUserAddress(seller.getAddress())
//                    .quoteDelta(1000)
//                    .baseDelta(-4000)
//                    .fees(0);
//
//            it("should fail with invalid exchange id", () -> {
//                int oldExchangeId = settlements.exchangeId();
//                settlements.exchangeId(5);
//
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//
//                settlements.exchangeId(oldExchangeId);
//            });
//
//            it("should fail with non creator as caller", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = buyer.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", buyer.waitForResult(tx).getStatus());
//            });
//
//            it("should fail if missing data for settlement group", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size() - 1);
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x03", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("should fail with buyer not locking session", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x04", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("should not be able to make quote balance negative", () -> {
//                success(buyer.sendCall(StaticNetwork.DCN(), DCN.update_session(0, System.currentTimeMillis() / 1000 + 1000000, 0)));
//                group.settlement(entry, 0);
//
//                long initialQuoteSpend = entry.quoteDelta();
//                entry.quoteDelta(-qty.longValueExact() - 10);
//
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x05", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//
//                entry.quoteDelta(initialQuoteSpend);
//            });
//
//            it("spend limit should account for fees", () -> {
//                group.settlement(entry, 0);
//
//                long initialQuoteSpend = entry.quoteDelta();
//                long initialFees = entry.fees();
//
//                entry
//                        .quoteDelta(10 - qty.longValueExact())
//                        .fees(11);
//
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x05", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//
//                entry
//                        .quoteDelta(initialQuoteSpend)
//                        .fees(initialFees);
//            });
//
//            it("should fail because of fee limit", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x06", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("should not be able to make base balance negative", () -> {
//                success(buyer.sendCall(StaticNetwork.DCN(), DCN.update_session(0, System.currentTimeMillis() / 1000 + 1000000, 100)));
//
//                group.settlement(entry, 0);
//                long oldBaseDelta = entry.baseDelta();
//                entry.baseDelta(-1);
//
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x07", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//
//                entry.baseDelta(oldBaseDelta);
//            });
//
//            it("should fail because min quote is violated", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x09", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("should be able to update min_quote for buyer", () -> {
//                byte[] updateLimitBytes = new byte[1000];
//                LimitUpdate updateLimit = new LimitUpdate().wrap(new UnsafeBuffer(updateLimitBytes), 0);
//
//                updateLimit
//                        .user(buyer.getAddress())
//                        .exchangeId(0)
//                        .assetId(baseAssetId)
//                        .version(1)
//                        .maxLongPrice(0)
//                        .minShortPrice(-1)
//                        .minQuoteQty(group.settlement(entry, 0).quoteDelta())
//                        .minBaseQty(0)
//                        .quoteShift(0)
//                        .baseShift(0);
//
//                byte[] hash = DCNHasher.instance.hash(updateLimit.hash());
//                updateLimit.signature(buyer.signHash(hash));
//
//                String payload = Hex.toHexString(updateLimitBytes, 0, LimitUpdate.BYTES);
//                success(creator.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload)));
//            });
//
//            it("should fail because long_max_price is too low", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x0c", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("should update max long price", () -> {
//                byte[] updateLimitBytes = new byte[1000];
//                LimitUpdate updateLimit = new LimitUpdate().wrap(new UnsafeBuffer(updateLimitBytes), 0);
//
//                updateLimit
//                        .user(buyer.getAddress())
//                        .exchangeId(0)
//                        .assetId(baseAssetId)
//                        .version(2)
//                        .maxLongPrice(25000000L)
//                        .minShortPrice(-1)
//                        .minQuoteQty(group.settlement(entry, 0).quoteDelta())
//                        .minBaseQty(0)
//                        .quoteShift(0)
//                        .baseShift(0);
//
//                byte[] hash = DCNHasher.instance.hash(updateLimit.hash());
//                updateLimit.signature(buyer.signHash(hash));
//
//                String payload = Hex.toHexString(updateLimitBytes, 0, LimitUpdate.BYTES);
//                success(creator.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload)));
//            });
//
//            it("should fail because seller session is not setup", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//                assertEquals("0x04", RevertCodeExtractor.Get(tx.getError()));
//                assertEquals("0x0", creator.waitForResult(tx).getStatus());
//            });
//
//            it("should be able to setup seller", () -> {
//                {
//                    success(seller.sendCall(StaticNetwork.DCN(), DCN.update_session(0, System.currentTimeMillis() / 1000 + 1000000, 0)));
//                }
//
//                {
//                    byte[] updateLimitBytes = new byte[1000];
//                    LimitUpdate updateLimit = new LimitUpdate().wrap(new UnsafeBuffer(updateLimitBytes), 0);
//
//                    updateLimit
//                            .user(seller.getAddress())
//                            .exchangeId(0)
//                            .assetId(baseAssetId)
//                            .version(1)
//                            .maxLongPrice(0)
//                            .minShortPrice(25000000L)
//                            .minQuoteQty(0)
//                            .minBaseQty(group.settlement(entry, 1).baseDelta())
//                            .quoteShift(0)
//                            .baseShift(0);
//
//                    byte[] hash = DCNHasher.instance.hash(updateLimit.hash());
//                    updateLimit.signature(seller.signHash(hash));
//
//                    String payload = Hex.toHexString(updateLimitBytes, 0, LimitUpdate.BYTES);
//                    success(creator.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload)));
//                }
//            });
//
//            QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());
//
//            it("should settle", () -> {
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                success(creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload)));
//
//                DCN.GetSessionBalanceReturnValue balance;
//
//                /* buyer balances */
//                group.settlement(entry, 0);
//                balance = query.query(DCN::query_get_session_balance,
//                        DCN.get_session_balance(buyer.getAddress(), 0, quoteAssetId));
//                assertEquals(initBalance + entry.quoteDelta() - entry.fees(), balance.asset_balance);
//
//                balance = query.query(DCN::query_get_session_balance,
//                        DCN.get_session_balance(buyer.getAddress(), 0, baseAssetId));
//                assertEquals(entry.baseDelta(), balance.asset_balance);
//
//                /* seller balances */
//                group.settlement(entry, 1);
//                balance = query.query(DCN::query_get_session_balance,
//                        DCN.get_session_balance(seller.getAddress(), 0, quoteAssetId));
//                assertEquals(entry.quoteDelta(), balance.asset_balance);
//
//                balance = query.query(DCN::query_get_session_balance,
//                        DCN.get_session_balance(seller.getAddress(), 0, baseAssetId));
//                assertEquals(initBalance + entry.baseDelta(), balance.asset_balance);
//
//                /* exchange received fees */
//                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
//                assertEquals(group.settlement(entry, 0).fees(), exchange.fee_balance);
//
//                /* buyer fees used */
//                DCN.GetSessionReturnValue session = query.query(DCN::query_get_session, DCN.get_session(buyer.getAddress(), 0));
//                assertEquals(group.settlement(entry, 0).fees(), session.fee_used);
//            });
//
//            it("should not settle if locked", () -> {
//                success(creator.sendCall(StaticNetwork.DCN(), DCN.security_lock()));
//
//                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
//
//                Assert.assertTrue(tx.hasError());
//                assertEquals("0x64", RevertCodeExtractor.Get(tx.getError()));
//            });
//
//            it("non exchange should not be able to withdraw fee", () -> {
//                EthSendTransaction tx = buyer.sendCall(StaticNetwork.DCN(),
//                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 11));
//                Assert.assertTrue(tx.hasError());
//                assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
//            });
//
//            it("exchange should not be able to withdraw more than fee", () -> {
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(),
//                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 11));
//                Assert.assertTrue(tx.hasError());
//                assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
//            });
//
//            it("should be able to withdraw fee 50", () -> {
//                success(creator.sendCall(StaticNetwork.DCN(),
//                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 5)));
//
//                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
//                assertEquals(5, exchange.fee_balance);
//
//                ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(quoteToken.value, StaticNetwork.Web3(),
//                        ERC20.balanceOf(creator.getAddress()));
//                assertEquals(BigInteger.valueOf(50), balance.balance);
//            });
//
//            it("should be able to withdraw fee 0", () -> {
//                success(creator.sendCall(StaticNetwork.DCN(),
//                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 0)));
//
//                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
//                assertEquals(5, exchange.fee_balance);
//
//                ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(quoteToken.value, StaticNetwork.Web3(),
//                        ERC20.balanceOf(creator.getAddress()));
//                assertEquals(BigInteger.valueOf(50), balance.balance);
//            });
//
//            it("should be able to withdraw fee 50 to 0", () -> {
//                success(creator.sendCall(StaticNetwork.DCN(),
//                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 5)));
//
//                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
//                assertEquals(0, exchange.fee_balance);
//
//                ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(quoteToken.value, StaticNetwork.Web3(),
//                        ERC20.balanceOf(creator.getAddress()));
//                assertEquals(BigInteger.valueOf(100), balance.balance);
//            });
//
//            it("should fail to withdraw with no balance", () -> {
//                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(),
//                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 1));
//                Assert.assertTrue(tx.hasError());
//                assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
//            });
//        });
    }
}
