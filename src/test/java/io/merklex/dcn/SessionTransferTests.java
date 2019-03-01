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
import org.web3j.protocol.core.methods.response.TransactionReceipt;

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
                    DCN.user_deposit(userId, assetId, totalSupply)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("12345678901", creator.getAddress())));
        });

        it("should not be able to transfer more than balance", () -> {
            long transfer = totalSupply.divide(BigInteger.TEN.pow(10)).add(BigInteger.ONE).longValueExact();
            assertRevert("0x04", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));
        });


        long transfer = 10;
        BigInteger bigTransfer = BigInteger.valueOf(transfer);
        BigInteger scaledTransfer = bigTransfer.multiply(BigInteger.TEN.pow(10));

        it("should be able to transfer to session", () -> {
            TransactionReceipt tx = assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));

            assertEquals(1, tx.getLogs().size());
            DCN.ExchangeDeposit exchangeDeposit = DCN.ExtractExchangeDeposit(tx.getLogs().get(0));
            assertNotNull(exchangeDeposit);
            assertEquals(assetId, exchangeDeposit.asset_id);
            assertEquals(userId, exchangeDeposit.user_id);
            assertEquals(exchangeId, exchangeDeposit.exchange_id);

            DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                    DCN.get_balance(userId, assetId));
            assertEquals(totalSupply.subtract(scaledTransfer), userBalance.return_balance);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer, sessionBalance.total_deposit);
            assertEquals(transfer, sessionBalance.asset_balance);
        });

        it("should only be able to transfer to with withdraw address", () -> {
            assertRevert("0x03", creator.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));

            assertRevert("0x03", bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));

            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_withdraw_address_update(userId, bobBackup.getAddress())));

            assertRevert("0x03", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));

            TransactionReceipt tx = assertSuccess(bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_to_session(userId, exchangeId, assetId, transfer)));

            assertEquals(1, tx.getLogs().size());
            DCN.ExchangeDeposit exchangeDeposit = DCN.ExtractExchangeDeposit(tx.getLogs().get(0));
            assertNotNull(exchangeDeposit);
            assertEquals(assetId, exchangeDeposit.asset_id);
            assertEquals(userId, exchangeDeposit.user_id);
            assertEquals(exchangeId, exchangeDeposit.exchange_id);

            DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                    DCN.get_balance(userId, assetId));
            assertEquals(totalSupply.subtract(scaledTransfer.multiply(BigInteger.valueOf(2))), userBalance.return_balance);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer.multiply(BigInteger.valueOf(2)), sessionBalance.total_deposit);
            assertEquals(transfer * 2, sessionBalance.asset_balance);
        });

        it("should not be able to transfer from with more than balance", () -> {
            assertRevert("0x05", bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transfer * 2 + 1)));
        });

        it("should only be able to transfer from with trade address", () -> {
            assertRevert("0x03", bobBackup.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transfer)));

            assertRevert("0x03", creator.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transfer)));

            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.transfer_from_session(userId, exchangeId, assetId, transfer)));

            DCN.GetBalanceReturnValue userBalance = query.query(DCN::query_get_balance,
                    DCN.get_balance(userId, assetId));
            assertEquals(totalSupply.subtract(scaledTransfer), userBalance.return_balance);

            DCN.GetSessionBalanceReturnValue sessionBalance = query.query(DCN::query_get_session_balance,
                    DCN.get_session_balance(userId, exchangeId, assetId));
            assertEquals(bigTransfer.multiply(BigInteger.valueOf(2)), sessionBalance.total_deposit);
            assertEquals(transfer, sessionBalance.asset_balance);
        });

        it("should not be able to transfer from to a balance below unsettled", () -> {
        });
    }
}
