package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class UserDepositWithdrawTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions tokenOwner = Accounts.getTx(12);
        EtherTransactions bob = Accounts.getTx(13);
        EtherTransactions bobBackup = Accounts.getTx(14);
        Box<String> token = new Box<>();

        BigInteger totalSupply = BigInteger.valueOf(100000000_0000000000L);
        BigInteger initialBobWalletBalance = BigInteger.valueOf(30_0000000000L);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        beforeAll(() -> {
            token.value = tokenOwner.deployContract(
                    BigInteger.ZERO,
                    StaticNetwork.GAS_LIMIT,
                    ERC20.DeployData(
                            totalSupply,
                            "Token 1",
                            18,
                            "TK1"
                    ),
                    BigInteger.ZERO
            );

            creator.call(StaticNetwork.DCN(), DCN.add_asset("TK1 ", 10000000000L, token.value));
            tokenOwner.call(token.value, ERC20.transfer(bob.getAddress(), initialBobWalletBalance));
            bob.call(StaticNetwork.DCN(), DCN.user_create());
        });

        final int assetId = 0;
        final int userId = 0;

        it("initial balance should be zero", () -> {
            DCN.GetBalanceReturnValue balance = query.query(DCN::query_get_balance, DCN.get_balance(userId, assetId));
            assertEquals(BigInteger.ZERO, balance.return_balance);
        });

        BigInteger deposit = BigInteger.valueOf(10000);


        it("should fail without allowance", () -> {
            assertRevert("0x04", bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit(userId, assetId, deposit)));
        });

        it("should be able to deposit with allowance", () -> {
            assertSuccess(bob.sendCall(token.value,
                    ERC20.approve(StaticNetwork.DCN(), deposit.multiply(BigInteger.TEN))));

            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit(userId, assetId, deposit)));

            ERC20.BalanceofReturnValue walletBalance;

            walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(bob.getAddress()));
            assertEquals(initialBobWalletBalance.subtract(deposit), walletBalance.balance);

            walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(StaticNetwork.DCN()));
            assertEquals(deposit, walletBalance.balance);

            DCN.GetBalanceReturnValue balance = query.query(DCN::query_get_balance, DCN.get_balance(userId, assetId));
            assertEquals(deposit, balance.return_balance);
        });

        it("other user should be able to deposit to user", () -> {
            assertSuccess(tokenOwner.sendCall(token.value,
                    ERC20.approve(StaticNetwork.DCN(), deposit.multiply(BigInteger.TEN))));

            assertSuccess(tokenOwner.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit(userId, assetId, deposit)));

            ERC20.BalanceofReturnValue walletBalance;

            walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(tokenOwner.getAddress()));
            assertEquals(totalSupply.subtract(initialBobWalletBalance).subtract(deposit), walletBalance.balance);

            walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(StaticNetwork.DCN()));
            assertEquals(deposit.multiply(BigInteger.valueOf(2)), walletBalance.balance);

            DCN.GetBalanceReturnValue balance = query.query(DCN::query_get_balance, DCN.get_balance(userId, assetId));
            assertEquals(deposit.multiply(BigInteger.valueOf(2)), balance.return_balance);
        });

        it("user should not be able to withdraw more than balance", () -> {
            assertRevert("0x03", bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_withdraw(userId, assetId, bob.getAddress(), deposit.multiply(BigInteger.valueOf(3)))));
        });

        it("should be able to withdraw", () -> {
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_withdraw(userId, assetId, bob.getAddress(), deposit)));

            ERC20.BalanceofReturnValue walletBalance;

            walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(bob.getAddress()));
            assertEquals(initialBobWalletBalance, walletBalance.balance);

            walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(StaticNetwork.DCN()));
            assertEquals(deposit, walletBalance.balance);

            DCN.GetBalanceReturnValue balance = query.query(DCN::query_get_balance, DCN.get_balance(userId, assetId));
            assertEquals(deposit, balance.return_balance);
        });

        describe("only should be able to withdraw using withdraw address", () -> {
            it("should fail from other user", () -> {
                assertRevert("0x02", creator.sendCall(StaticNetwork.DCN(),
                        DCN.user_withdraw(userId, assetId, bob.getAddress(), deposit)));
            });

            it("should fail from user's trade_address and word with withdraw", () -> {
                assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                        DCN.user_set_withdraw_address(userId, bobBackup.getAddress())));

                assertRevert("0x02", bob.sendCall(StaticNetwork.DCN(),
                        DCN.user_withdraw(userId, assetId, bob.getAddress(), deposit)));

                assertSuccess(bobBackup.sendCall(StaticNetwork.DCN(),
                        DCN.user_withdraw(userId, assetId, tokenOwner.getAddress(), deposit)));

                ERC20.BalanceofReturnValue walletBalance;

                walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(tokenOwner.getAddress()));
                assertEquals(totalSupply.subtract(initialBobWalletBalance), walletBalance.balance);

                walletBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(), ERC20.balanceOf(StaticNetwork.DCN()));
                assertEquals(BigInteger.ZERO, walletBalance.balance);

                DCN.GetBalanceReturnValue balance = query.query(DCN::query_get_balance, DCN.get_balance(userId, assetId));
                assertEquals(BigInteger.ZERO, balance.return_balance);
            });
        });
    }
}
