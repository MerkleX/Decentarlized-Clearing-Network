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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.*;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Spectrum.class)
public class SessionTransferTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions bob = Accounts.getTx(13);
        EtherTransactions bobBackup = Accounts.getTx(14);
        Box<String> token = new Box<>();

        BigInteger totalSupply = BigInteger.valueOf(100000000_0000000000L);
        BigInteger bobDCNBalance = BigInteger.valueOf(10000_0000000000L);
        long exchangeDepositAmount = 100;
        BigInteger exchangeDepositScaled = BigInteger.valueOf(100_0000000000L);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        final int assetId = 0;
        final int userId = 0;
        final int exchangeId = 0;

        beforeAll(() -> {
            token.value = bob.deployContract(
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

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("TK1 ", 10000000000L, token.value)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(token.value,
                    ERC20.approve(StaticNetwork.DCN(), totalSupply)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit(userId, assetId, bobDCNBalance)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("12345678901", creator.getAddress())));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_deposit(exchangeId, assetId, exchangeDepositAmount)));

            ERC20.BalanceofReturnValue userBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(),
                    ERC20.balanceOf(bob.getAddress()));
            assertEquals(totalSupply.subtract(bobDCNBalance).subtract(exchangeDepositScaled), userBalance.balance);
        });

        it("should not be able to transfer more than balance", () -> {
            long transfer = bobDCNBalance.divide(BigInteger.TEN.pow(10)).add(BigInteger.ONE).longValueExact();
            assertRevert("0x04", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));
        });

        long transferAmount = 10;
        BigInteger bigTransfer = BigInteger.valueOf(transferAmount);
        BigInteger scaledTransfer = bigTransfer.multiply(BigInteger.TEN.pow(10));

        it("should be able to transfer to session", () -> {
            TransactionReceipt tx = assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount)));

            assertEquals(1, tx.getLogs().size());
            DCN.ExchangeDeposit exchangeDeposit = DCN.ExtractExchangeDeposit(tx.getLogs().get(0));
            assertNotNull(exchangeDeposit);
            assertEquals(assetId, exchangeDeposit.asset_id);
            assertEquals(userId, exchangeDeposit.user_id);
            assertEquals(exchangeId, exchangeDeposit.exchange_id);

            DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                    DCN.get_balance(userId, assetId));
            assertEquals(bobDCNBalance.subtract(scaledTransfer), userBalance.return_balance);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer, sessionBalance.total_deposit);
            assertEquals(0, sessionBalance.unsettled_withdraw_total);
            assertEquals(transferAmount, sessionBalance.asset_balance);
        });

        it("should only be able to transfer to with withdraw address", () -> {
            assertRevert("0x03", creator.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount)));

            assertRevert("0x03", bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount)));

            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_withdraw_address_update(userId, bobBackup.getAddress())));

            assertRevert("0x03", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount)));

            TransactionReceipt tx = assertSuccess(bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount)));

            assertEquals(1, tx.getLogs().size());
            DCN.ExchangeDeposit exchangeDeposit = DCN.ExtractExchangeDeposit(tx.getLogs().get(0));
            assertNotNull(exchangeDeposit);
            assertEquals(assetId, exchangeDeposit.asset_id);
            assertEquals(userId, exchangeDeposit.user_id);
            assertEquals(exchangeId, exchangeDeposit.exchange_id);

            DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                    DCN.get_balance(userId, assetId));
            assertEquals(bobDCNBalance.subtract(scaledTransfer.multiply(BigInteger.valueOf(2))), userBalance.return_balance);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer.multiply(BigInteger.valueOf(2)), sessionBalance.total_deposit);
            assertEquals(0, sessionBalance.unsettled_withdraw_total);
            assertEquals(transferAmount * 2, sessionBalance.asset_balance);
        });

        it("should not be able to transfer from with more than balance", () -> {
            assertRevert("0x05", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount * 2 + 1)));
        });

        it("should only be able to transfer from with trade address", () -> {
            assertRevert("0x03", bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount)));

            assertRevert("0x03", creator.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount)));

            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount)));

            DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                    DCN.get_balance(userId, assetId));
            assertEquals(bobDCNBalance.subtract(scaledTransfer), userBalance.return_balance);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer.multiply(BigInteger.valueOf(2)), sessionBalance.total_deposit);
            assertEquals(transferAmount, sessionBalance.asset_balance);
        });

        describe("should not be able to withdraw below unsettled", () -> {
            it("only exchange should be able to transfer overdraft from exchange balance", () -> {
                byte[] bytes = new byte[1000];
                UnsafeBuffer buffer = new UnsafeBuffer(bytes);

                Transfers transfers = new Transfers().wrap(buffer, 0);
                Transfers.Group group = new Transfers.Group();
                Transfers.Transfer transfer = new Transfers.Transfer();

                transfers.exchangeId(exchangeId)
                        .firstGroup(group)
                        .transferCount(1)
                        .assetId(assetId)
                        .allowOverdraft(true)
                        .firstTransfer(transfer)
                        .userId(userId)
                        .quantity(transferAmount * 3);

                String payload = Numeric.toHexString(bytes, 0, transfers.bytes(1, group), true);

                assertRevert("0x03", bob.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_transfer_from(payload)));

                assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                        DCN.exchange_transfer_from(payload)));

                DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(userId, exchangeId, assetId));
                assertEquals(bigTransfer.multiply(BigInteger.valueOf(2)), sessionBalance.total_deposit);
                assertEquals(transferAmount * 2, sessionBalance.unsettled_withdraw_total);
                assertEquals(0, sessionBalance.asset_balance);

                DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                        DCN.get_balance(userId, assetId));
                assertEquals(bobDCNBalance.add(scaledTransfer.multiply(BigInteger.valueOf(2))), userBalance.return_balance);
            });

            it("should only be able to withdraw under unsettled", () -> {
                assertSuccess(bobBackup.sendCall(StaticNetwork.DCN(),
                        DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount * 3)));

                DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(userId, exchangeId, assetId));
                assertEquals(bigTransfer.multiply(BigInteger.valueOf(5)), sessionBalance.total_deposit);
                assertEquals(transferAmount * 2, sessionBalance.unsettled_withdraw_total);
                assertEquals(transferAmount * 3, sessionBalance.asset_balance);

                assertRevert("0x06", bob.sendCall(StaticNetwork.DCN(),
                        DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount + transferAmount / 2)));

                assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                        DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount)));

                sessionBalance = query.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(userId, exchangeId, assetId));
                assertEquals(bigTransfer.multiply(BigInteger.valueOf(5)), sessionBalance.total_deposit);
                assertEquals(transferAmount * 2, sessionBalance.unsettled_withdraw_total);
                assertEquals(transferAmount * 2, sessionBalance.asset_balance);

                DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                        DCN.get_balance(userId, assetId));
                assertEquals(bobDCNBalance, userBalance.return_balance);
            });
        });

        it("should not be able to transfer from if session is locked", () -> {
            assertSuccess(bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transferAmount)));

            BigInteger unlockAt = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 30000);
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_session_set_unlock_at(userId, exchangeId, unlockAt)));

            assertRevert("0x04", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transferAmount)));
        });

        it("should be able deposit directly to session", () -> {
            TransactionReceipt tx = assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(userId, exchangeId, assetId, transferAmount)));

            assertEquals(2, tx.getLogs().size());
            DCN.ExchangeDeposit exchangeDeposit = DCN.ExtractExchangeDeposit(tx.getLogs().get(1));
            assertNotNull(exchangeDeposit);
            assertEquals(userId, exchangeDeposit.user_id);
            assertEquals(exchangeId, exchangeDeposit.exchange_id);
            assertEquals(assetId, exchangeDeposit.asset_id);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer.multiply(BigInteger.valueOf(7)), sessionBalance.total_deposit);
            assertEquals(transferAmount * 2, sessionBalance.unsettled_withdraw_total);
            assertEquals(transferAmount * 4, sessionBalance.asset_balance);

            ERC20.BalanceofReturnValue userBalance = ERC20.query_balanceOf(token.value, StaticNetwork.Web3(),
                    ERC20.balanceOf(bob.getAddress()));
            assertEquals(totalSupply
                    .subtract(bobDCNBalance)
                    .subtract(exchangeDepositScaled)
                    .subtract(scaledTransfer),
                    userBalance.balance
            );
        });
    }
}
