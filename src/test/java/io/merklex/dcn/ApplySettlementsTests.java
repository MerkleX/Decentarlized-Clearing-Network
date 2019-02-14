package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.RevertCodeExtractor;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.agrona.concurrent.UnsafeBuffer;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Spectrum.class)
public class ApplySettlementsTests {
    private static void success(EthSendTransaction tx) throws Exception {
        if (tx.hasError()) {
            System.out.println(tx.getError());
            fail("TX Failed, Revert: " + RevertCodeExtractor.Get(tx.getError()));
        }

        Assert.assertEquals("0x1", Accounts.getTx(0).waitForResult(tx).getStatus());
    }

    {
        Run(true);
        Run(false);
    }

    private static void Run(boolean flip) {
        describe("Run with " + (flip ? "quote,base asset order" : "base,quote asset order"), () -> {
            StaticNetwork.DescribeCheckpoint();

            EtherTransactions creator = Accounts.getTx(0);
            EtherTransactions buyer = Accounts.getTx(1);
            EtherTransactions seller = Accounts.getTx(2);

            Box<String> baseToken = new Box<>();
            Box<String> quoteToken = new Box<>();

            long initBalance = 10_000L;
            BigInteger qty = BigInteger.valueOf(initBalance);

            final int quoteAssetId;
            final int baseAssetId;

            if (flip) {
                quoteAssetId = 1;
                baseAssetId = 0;
            }
            else {
                quoteAssetId = 0;
                baseAssetId = 1;
            }

            beforeAll(() -> {
                quoteToken.value = buyer.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
                        qty.multiply(BigInteger.TEN),
                        "Test Token",
                        10,
                        "TK1"
                ), BigInteger.ZERO);

                baseToken.value = seller.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
                        qty.multiply(BigInteger.valueOf(100)),
                        "Test Token",
                        10,
                        "TK1"
                ), BigInteger.ZERO);

                if (flip) {
                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("BASE", 100, baseToken.value)));
                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("QTY!", 10, quoteToken.value)));
                }
                else {
                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("QTY!", 10, quoteToken.value)));
                    success(creator.sendCall(StaticNetwork.DCN(), DCN.add_asset("BASE", 100, baseToken.value)));
                }

                success(creator.sendCall(StaticNetwork.DCN(), DCN.add_exchange("exchange", quoteAssetId, creator.getAddress())));

                success(buyer.sendCall(quoteToken.value, ERC20.approve(StaticNetwork.DCN(), qty.multiply(BigInteger.TEN))));
                success(seller.sendCall(baseToken.value, ERC20.approve(StaticNetwork.DCN(), qty.multiply(BigInteger.valueOf(100)))));

                success(buyer.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, quoteAssetId, initBalance)));
                success(seller.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, baseAssetId, initBalance)));
            });

            byte[] data = new byte[1000];
            UnsafeBuffer buffer = new UnsafeBuffer(data);

            Settlements settlements = new Settlements();

            Settlements.Group group = settlements
                    .wrap(buffer, 0)
                    .exchangeId(0)
                    .firstGroup(new Settlements.Group())
                    .userCount(2)
                    .baseAssetId(baseAssetId);

            Settlements.SettlementData entry = new Settlements.SettlementData();
            group
                    .settlement(entry, 0)
                    .setUserAddress(buyer.getAddress())
                    .quoteDelta(-1000)
                    .baseDelta(4000)
                    .fees(10);

            group
                    .settlement(entry, 1)
                    .setUserAddress(seller.getAddress())
                    .quoteDelta(1000)
                    .baseDelta(-4000)
                    .fees(0);

            it("should fail with invalid exchange id", () -> {
                int oldExchangeId = settlements.exchangeId();
                settlements.exchangeId(5);

                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());

                settlements.exchangeId(oldExchangeId);
            });

            it("should fail with non creator as caller", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = buyer.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", buyer.waitForResult(tx).getStatus());
            });

            it("should fail if missing data for settlement group", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size() - 1);
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x03", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());
            });

            it("should fail with buyer not locking session", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x04", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());
            });

            it("should not be able to make quote balance negative", () -> {
                success(buyer.sendCall(StaticNetwork.DCN(), DCN.update_session(0, System.currentTimeMillis() / 1000 + 1000000, 0)));
                group.settlement(entry, 0);

                long initialQuoteSpend = entry.quoteDelta();
                entry.quoteDelta(-qty.longValueExact() - 10);

                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x05", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());

                entry.quoteDelta(initialQuoteSpend);
            });

            it("spend limit should account for fees", () -> {
                group.settlement(entry, 0);

                long initialQuoteSpend = entry.quoteDelta();
                long initialFees = entry.fees();

                entry
                        .quoteDelta(10 - qty.longValueExact())
                        .fees(11);

                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x05", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());

                entry
                        .quoteDelta(initialQuoteSpend)
                        .fees(initialFees);
            });

            it("should fail because of fee limit", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x06", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());
            });

            it("should not be able to make base balance negative", () -> {
                success(buyer.sendCall(StaticNetwork.DCN(), DCN.update_session(0, System.currentTimeMillis() / 1000 + 1000000, 100)));

                group.settlement(entry, 0);
                long oldBaseDelta = entry.baseDelta();
                entry.baseDelta(-1);

                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x07", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());

                entry.baseDelta(oldBaseDelta);
            });

            it("should fail because min quote is violated", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x09", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());
            });

            it("should be able to update min_quote for buyer", () -> {
                byte[] updateLimitBytes = new byte[1000];
                UpdateLimit updateLimit = new UpdateLimit().wrap(new UnsafeBuffer(updateLimitBytes), 0);

                updateLimit
                        .user(buyer.getAddress())
                        .exchangeId(0)
                        .assetId(baseAssetId)
                        .version(1)
                        .maxLongPrice(0)
                        .minShortPrice(-1)
                        .minQuoteQty(group.settlement(entry, 0).quoteDelta())
                        .minBaseQty(0)
                        .quoteShift(0)
                        .baseShift(0);

                byte[] hash = DCNHasher.instance.hash(updateLimit.hash());
                updateLimit.signature(buyer.signHash(hash));

                String payload = Hex.toHexString(updateLimitBytes, 0, UpdateLimit.BYTES);
                success(creator.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload)));
            });

            it("should fail because long_max_price is too low", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x0c", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());
            });

            it("should update max long price", () -> {
                byte[] updateLimitBytes = new byte[1000];
                UpdateLimit updateLimit = new UpdateLimit().wrap(new UnsafeBuffer(updateLimitBytes), 0);

                updateLimit
                        .user(buyer.getAddress())
                        .exchangeId(0)
                        .assetId(baseAssetId)
                        .version(2)
                        .maxLongPrice(25000000L)
                        .minShortPrice(-1)
                        .minQuoteQty(group.settlement(entry, 0).quoteDelta())
                        .minBaseQty(0)
                        .quoteShift(0)
                        .baseShift(0);

                byte[] hash = DCNHasher.instance.hash(updateLimit.hash());
                updateLimit.signature(buyer.signHash(hash));

                String payload = Hex.toHexString(updateLimitBytes, 0, UpdateLimit.BYTES);
                success(creator.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload)));
            });

            it("should fail because seller session is not setup", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));
                assertEquals("0x04", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", creator.waitForResult(tx).getStatus());
            });

            it("should be able to setup seller", () -> {
                {
                    success(seller.sendCall(StaticNetwork.DCN(), DCN.update_session(0, System.currentTimeMillis() / 1000 + 1000000, 0)));
                }

                {
                    byte[] updateLimitBytes = new byte[1000];
                    UpdateLimit updateLimit = new UpdateLimit().wrap(new UnsafeBuffer(updateLimitBytes), 0);

                    updateLimit
                            .user(seller.getAddress())
                            .exchangeId(0)
                            .assetId(baseAssetId)
                            .version(1)
                            .maxLongPrice(0)
                            .minShortPrice(25000000L)
                            .minQuoteQty(0)
                            .minBaseQty(group.settlement(entry, 1).baseDelta())
                            .quoteShift(0)
                            .baseShift(0);

                    byte[] hash = DCNHasher.instance.hash(updateLimit.hash());
                    updateLimit.signature(seller.signHash(hash));

                    String payload = Hex.toHexString(updateLimitBytes, 0, UpdateLimit.BYTES);
                    success(creator.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload)));
                }
            });

            QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

            it("should settle", () -> {
                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                success(creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload)));

                DCN.GetSessionBalanceReturnValue balance;

                /* buyer balances */
                group.settlement(entry, 0);
                balance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(buyer.getAddress(), 0, quoteAssetId));
                assertEquals(initBalance + entry.quoteDelta() - entry.fees(), balance.asset_balance);

                balance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(buyer.getAddress(), 0, baseAssetId));
                assertEquals(entry.baseDelta(), balance.asset_balance);

                /* seller balances */
                group.settlement(entry, 1);
                balance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(seller.getAddress(), 0, quoteAssetId));
                assertEquals(entry.quoteDelta(), balance.asset_balance);

                balance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(seller.getAddress(), 0, baseAssetId));
                assertEquals(initBalance + entry.baseDelta(), balance.asset_balance);

                /* exchange received fees */
                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
                assertEquals(group.settlement(entry, 0).fees(), exchange.fee_balance);

                /* buyer fees used */
                DCN.GetSessionReturnValue session = query.query(DCN::query_get_session, DCN.get_session(buyer.getAddress(), 0));
                assertEquals(group.settlement(entry, 0).fees(), session.fee_used);
            });

            it("should not settle if locked", () -> {
                success(creator.sendCall(StaticNetwork.DCN(), DCN.security_lock()));

                String payload = Hex.toHexString(data, 0, Settlements.BYTES + group.size());
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(), DCN.apply_settlement_groups(payload));

                Assert.assertTrue(tx.hasError());
                Assert.assertEquals("0x64", RevertCodeExtractor.Get(tx.getError()));
            });

            it("non exchange should not be able to withdraw fee", () -> {
                EthSendTransaction tx = buyer.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 11));
                Assert.assertTrue(tx.hasError());
                Assert.assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
            });

            it("exchange should not be able to withdraw more than fee", () -> {
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 11));
                Assert.assertTrue(tx.hasError());
                Assert.assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
            });

            it("should be able to withdraw fee 50", () -> {
                success(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 5)));

                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
                assertEquals(5, exchange.fee_balance);

                ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(quoteToken.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(creator.getAddress()));
                Assert.assertEquals(BigInteger.valueOf(50), balance.balance);
            });

            it("should be able to withdraw fee 0", () -> {
                success(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 0)));

                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
                assertEquals(5, exchange.fee_balance);

                ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(quoteToken.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(creator.getAddress()));
                Assert.assertEquals(BigInteger.valueOf(50), balance.balance);
            });

            it("should be able to withdraw fee 50 to 0", () -> {
                success(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 5)));

                DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange, DCN.get_exchange(0));
                assertEquals(0, exchange.fee_balance);

                ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(quoteToken.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(creator.getAddress()));
                Assert.assertEquals(BigInteger.valueOf(100), balance.balance);
            });

            it("should fail to withdraw with no balance", () -> {
                EthSendTransaction tx = creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_withdraw_fees(0, creator.getAddress(), 1));
                Assert.assertTrue(tx.hasError());
                Assert.assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
            });
        });
    }
}
