package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.runner.RunWith;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ApplySettlementBugFix {
    private static EtherTransactions creator = Accounts.getTx(0);

    private static EtherTransactions user1 = new EtherTransactions(StaticNetwork.Web3(),
            Credentials.create("A150C1069C8EC0BA140281E6CBEB6A036B95943FDA74E5BE9903C22F409DD0A3"))
            .withGas(BigInteger.ZERO, StaticNetwork.GAS_LIMIT);

    private static EtherTransactions user2 = new EtherTransactions(StaticNetwork.Web3(),
            Credentials.create("D66409126462E7AE60F3A656513D994E60C0654FB27A0FE2DD64D956002593E9"))
            .withGas(BigInteger.ZERO, StaticNetwork.GAS_LIMIT);

    {
        StaticNetwork.DescribeCheckpoint();


        System.out.println("USER1: " + user1.getAddress());
        System.out.println("USER2: " + user2.getAddress());

        Box<String> token1 = new Box<>();
        Box<String> token2 = new Box<>();

        long quant = 1000000000000000L;

        beforeAll(() -> {
            BigInteger deposit = BigInteger.valueOf(10).pow(30);
            token1.value = creator.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
                    deposit, "test", 18, "TX"),
                    BigInteger.ZERO);
            token2.value = creator.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
                    deposit, "test", 18, "TX"),
                    BigInteger.ZERO);

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("merklex    ", creator.getAddress())));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("test", 1, token1.value)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("test", 1, token2.value)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("test", 1, token2.value)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("test", 1, token1.value)));

            assertSuccess(user1.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(1, 0,
                            BigInteger.valueOf(System.currentTimeMillis() / 1000 + 30_000))));

            assertSuccess(user2.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(2, 0,
                            BigInteger.valueOf(System.currentTimeMillis() / 1000 + 30_000))));

            assertSuccess(creator.sendCall(token1.value, ERC20.approve(StaticNetwork.DCN(), deposit)));
            assertSuccess(creator.sendCall(token2.value, ERC20.approve(StaticNetwork.DCN(), deposit)));

            assertSuccess(creator.sendCall(StaticNetwork.DCN(), DCN.user_deposit_to_session(1, 0, 1, quant)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(), DCN.user_deposit_to_session(1, 0, 3, quant)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(), DCN.user_deposit_to_session(2, 0, 1, quant)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(), DCN.user_deposit_to_session(2, 0, 3, quant)));
        });

        it("apply set limit", () -> {
            assertSuccess(creator.sendCall(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, StaticNetwork.DCN(),
                    "0xec3b84f0000000000000000000000000000000000000000000000000000000000000002" +
                            "00000000000000000000000000000000000000000000000000000000000000146000000000" +
                            "000000000000000000000010000000000000001000000030000000000000000ffffffffff6d" +
                            "8400000000000000000000000000003d0900fffd5b44d6db000000000000000000020000000" +
                            "000000000000000000000000000000000000000009190b608793260a67804f71b23b956f6b8" +
                            "52f0afcc08053426ad169ea83ba5ff36eb058c5c7651c00b5330464bf754cc76cab45da73f70" +
                            "3305e706fac1baf59b1c0000000000000000000000020000000000000001000000030000000" +
                            "000000000ffffffffff676980ffffffffc465360000000000000f424000000000002dc6c000" +
                            "00000000000002000000000000000000000000000000000000000000000000119bf9db38f8b" +
                            "fb449493147f3842d0b7a43e8acd4b41c74bcf5479766c187c301f06d82d3f487770121e2ec" +
                            "da8cd8674e3b0ae27c88823df53c8606ad304bd71b000000000000000000000000000000000" +
                            "0000000000000000000", BigInteger.ZERO));

            DCN.GetMarketStateReturnValue state;

            state = DCN.query_get_market_state(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_market_state(1, 0, 1, 3));
            assertEquals(0, state.quote_qty);
            assertEquals(0, state.base_qty);

            state = DCN.query_get_market_state(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_market_state(2, 0, 1, 3));
            assertEquals(0, state.quote_qty);
            assertEquals(0, state.base_qty);
        });

        String settleData = "0x000000000000000100000003020000000000000001ffffffffff" +
                "6d8400000000001312d000000000000000000000000000000000020" +
                "000000000927c00ffffffffeced30000000000000000000";
        ApplySettlement("should apply settlement", settleData);
    }

    private static void ApplySettlement(String name, String settleData) {
        byte[] bytes = Numeric.hexStringToByteArray(settleData);
        UnsafeBuffer buffer = new UnsafeBuffer(bytes);
        Settlements settlements = new Settlements().wrap(buffer, 0);
        Settlements.Group group = settlements.firstGroup(new Settlements.Group());

        it(name, () -> {
            DCN.GetSessionBalanceReturnValue balance;
            DCN.GetMarketStateReturnValue state;

            long[] balances = new long[group.userCount() * 2];
            long[] states = new long[group.userCount() * 2];

            Settlements.SettlementData settlement = new Settlements.SettlementData();

            for (byte i = 0; i < group.userCount(); i++) {
                group.settlement(settlement, i);

                balance = DCN.query_get_session_balance(StaticNetwork.DCN(), creator.getWeb3(),
                        DCN.get_session_balance(settlement.userId(), 0, group.quoteAssetId()));
                balances[i * 2] = balance.asset_balance;

                balance = DCN.query_get_session_balance(StaticNetwork.DCN(), creator.getWeb3(),
                        DCN.get_session_balance(settlement.userId(), 0, group.baseAssetId()));
                balances[i * 2 + 1] = balance.asset_balance;

                state = DCN.query_get_market_state(StaticNetwork.DCN(), creator.getWeb3(),
                        DCN.get_market_state(settlement.userId(), 0, group.quoteAssetId(), group.baseAssetId()));
                states[i * 2] = state.quote_qty;
                states[i * 2 + 1] = state.base_qty;
            }

            assertSuccess(creator.sendCall(StaticNetwork.DCN(), DCN.exchange_apply_settlement_groups(settleData)));

            for (byte i = 0; i < group.userCount(); i++) {
                group.settlement(settlement, i);

                balance = DCN.query_get_session_balance(StaticNetwork.DCN(), creator.getWeb3(),
                        DCN.get_session_balance(settlement.userId(), 0, group.quoteAssetId()));
                assertEquals(Math.addExact(balances[i * 2], settlement.quoteDelta()), balance.asset_balance);

                balance = DCN.query_get_session_balance(StaticNetwork.DCN(), creator.getWeb3(),
                        DCN.get_session_balance(settlement.userId(), 0, group.baseAssetId()));
                assertEquals(Math.addExact(balances[i * 2 + 1], settlement.baseDelta()), balance.asset_balance);

                state = DCN.query_get_market_state(StaticNetwork.DCN(), creator.getWeb3(),
                        DCN.get_market_state(settlement.userId(), 0, group.quoteAssetId(), group.baseAssetId()));
                assertEquals(Math.addExact(states[i * 2], settlement.quoteDelta()), state.quote_qty);
                assertEquals(Math.addExact(states[i * 2 + 1], settlement.baseDelta()), state.base_qty);
            }
        });
    }
}
