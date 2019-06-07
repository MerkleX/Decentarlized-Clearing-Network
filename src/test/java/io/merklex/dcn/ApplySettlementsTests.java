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
import static io.merklex.dcn.utils.ConsistentBalanceCheck.assertCorrectBalances;
import static org.junit.Assert.assertEquals;

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

            BigInteger unlockAt = BigInteger.valueOf(
                    System.currentTimeMillis() / 1000 + 28800 * 2
            );

            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(userId1, exchangeId, unlockAt)));
            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(userId2, exchangeId, unlockAt)));
            assertSuccess(user3.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(userId3, exchangeId, unlockAt)));

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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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


                DCN.GetExchangeBalanceReturnValue exchangeBalance = query.query(DCN::query_get_exchange_balance,
                        DCN.get_exchange_balance(exchangeId, quoteAssetId));
                assertEquals(BigInteger.ZERO, exchangeBalance.exchange_balance);

                DCN.GetSessionBalanceReturnValue feeUserBalance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(userId1, exchangeId, quoteAssetId));
                assertEquals(1000, feeUserBalance.asset_balance);

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


                exchangeBalance = DCN.query_get_exchange_balance(StaticNetwork.DCN(),
                        exchange.getWeb3(), DCN.get_exchange_balance(exchangeId, quoteAssetId));
                assertEquals(BigInteger.valueOf(100), exchangeBalance.exchange_balance);

                feeUserBalance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(userId1, exchangeId, quoteAssetId));
                assertEquals(1000 + 3 - 100, feeUserBalance.asset_balance);

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

                assertCorrectBalances(query);
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

            assertCorrectBalances(query);
        });

        it("should fail to settle with same asset id", () -> {
            exchange.reloadNonce();

            settlements
                    .exchangeId(exchangeId)

                    .firstGroup(settlementGroup)
                    .quoteAssetId(baseAssetId)
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

            assertRevert("0x10", exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_apply_settlement_groups(settlements.payload(1))));

            assertCorrectBalances(query);
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
}
