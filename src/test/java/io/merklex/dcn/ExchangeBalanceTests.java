package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.*;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExchangeBalanceTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchange = Accounts.getTx(1);

        Box<String> token = new Box<>();

        BigInteger totalSupply = BigInteger.valueOf(3).pow(64);

        beforeAll(() -> {
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("merklex    ", exchange.getAddress())));

            token.value = creator.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT,
                    ERC20.DeployData(
                            totalSupply,
                            "test",
                            6,
                            "test"
                    ), BigInteger.ZERO
            );

            assertSuccess(creator.sendCall(token.value,
                    ERC20.approve(StaticNetwork.DCN(), totalSupply)));

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("test", 10, token.value)));
        });

        it("should be able to deposit into exchange", () -> {
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_deposit(0, 0, 5000)));

            DCN.GetExchangeBalanceReturnValue balance;
            balance = DCN.query_get_exchange_balance(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_exchange_balance(0, 0));

            assertEquals(BigInteger.valueOf(5000),
                    balance.exchange_balance);

            ERC20.BalanceofReturnValue tokenBalance;

            tokenBalance = ERC20.query_balanceOf(token.value, creator.getWeb3(),
                    ERC20.balanceOf(creator.getAddress()));
            assertEquals(totalSupply.subtract(BigInteger.valueOf(50000)), tokenBalance.balance);

            tokenBalance = ERC20.query_balanceOf(token.value, creator.getWeb3(),
                    ERC20.balanceOf(StaticNetwork.DCN()));
            assertEquals(BigInteger.valueOf(50000), tokenBalance.balance);
        });

        describe("should not be able to overflow", () -> {
            StaticNetwork.DescribeCheckpointForEach();

            it("should pass", () -> {
                creator.reloadNonce();
                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_deposit(0, 0, -5001)));
            });

            it("should fail", () -> {
                creator.reloadNonce();
                assertRevert("0x03", creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_deposit(0, 0, -5000)));
            });
        });

        it("should only be able to withdraw from exchange with owner", () -> {
            creator.reloadNonce();

            assertRevert("0x01", creator.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_withdraw(0, 0, creator.getAddress(), 500)));

            assertSuccess(exchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_withdraw(0, 0, exchange.getAddress(), 500)));

            ERC20.BalanceofReturnValue tokenBalance;

            tokenBalance = ERC20.query_balanceOf(token.value, creator.getWeb3(),
                    ERC20.balanceOf(StaticNetwork.DCN()));
            assertEquals(BigInteger.valueOf(50000 - 5000), tokenBalance.balance);

            tokenBalance = ERC20.query_balanceOf(token.value, creator.getWeb3(),
                    ERC20.balanceOf(exchange.getAddress()));
            assertEquals(BigInteger.valueOf(5000), tokenBalance.balance);
        });
    }
}
