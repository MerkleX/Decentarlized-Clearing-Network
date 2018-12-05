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
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class DepositAssetToSessionTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions user = Accounts.getTx(23);

        QueryHelper dcnQ = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        describe("with unit_scale=1 asset", () -> {
            it("should error with invalid asset id", () -> {
                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, 0, 10));
                assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", user.waitForResult(tx).getStatus());
            });

            Box<String> token1 = new Box<>();

            BigInteger ogTokenBalance = BigInteger.valueOf(1000);

            it("should error if quantity is zero", () -> {
                token1.value = user.deployContract(
                        BigInteger.ZERO, StaticNetwork.GAS_LIMIT,
                        ERC20.DeployData(ogTokenBalance, "test", 16, "test"),
                        BigInteger.ZERO
                );

                TransactionReceipt addTx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 1, token1.value));
                assertEquals("0x1", addTx.getStatus());

                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, 0, 0));
                assertEquals("0x02", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", user.waitForResult(tx).getStatus());
            });

            it("should error with no token allowance", () -> {
                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(), DCN.deposit_asset_to_session(0, 0, 10));
                assertEquals("0x03", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", user.waitForResult(tx).getStatus());
            });

            BigInteger allowance = BigInteger.valueOf(800);

            it("should fail to deposit more than allowance", () -> {
                assertEquals("0x1",
                        user.call(token1.value, ERC20.approve(StaticNetwork.DCN(), allowance)).getStatus());

                EthSendTransaction tx = user.sendCall(StaticNetwork.DCN(),
                        DCN.deposit_asset_to_session(0, 0, allowance.longValue() + 1)
                );
                assertEquals("0x03", RevertCodeExtractor.Get(tx.getError()));
                assertEquals("0x0", user.waitForResult(tx).getStatus());
            });

            it("should deposit", () -> {
                assertEquals("0x1",
                        user.call(token1.value, ERC20.approve(StaticNetwork.DCN(), allowance)).getStatus());

                long deposit = allowance.longValue() / 2;

                TransactionReceipt tx = user.call(StaticNetwork.DCN(),
                        DCN.deposit_asset_to_session(0, 0, deposit)
                );
                assertEquals("0x1", tx.getStatus());

                DCN.GetBalanceReturnValue balance = dcnQ.query(DCN::query_get_balance,
                        DCN.get_balance(user.getAddress(), 0));
                assertEquals(BigInteger.ZERO, balance.return_balance);

                DCN.GetSessionBalanceReturnValue sessionBalance = dcnQ.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(user.getAddress(), 0, 0));
                assertEquals(deposit, sessionBalance.asset_balance);
                assertEquals(deposit, sessionBalance.total_deposit);

                ERC20.BalanceofReturnValue tokenBalance;

                tokenBalance = ERC20.query_balanceOf(token1.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(user.getAddress()));
                assertEquals(ogTokenBalance.subtract(BigInteger.valueOf(deposit)), tokenBalance.balance);

                tokenBalance = ERC20.query_balanceOf(token1.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(StaticNetwork.DCN()));
                assertEquals(BigInteger.valueOf(deposit), tokenBalance.balance);
            });

            it("deposit should add", () -> {
                assertEquals("0x1",
                        user.call(token1.value, ERC20.approve(StaticNetwork.DCN(), allowance)).getStatus());

                long deposit = allowance.longValue() / 2;

                TransactionReceipt tx = user.call(StaticNetwork.DCN(),
                        DCN.deposit_asset_to_session(0, 0, deposit)
                );
                assertEquals("0x1", tx.getStatus());

                DCN.GetBalanceReturnValue balance = dcnQ.query(DCN::query_get_balance,
                        DCN.get_balance(user.getAddress(), 0));
                assertEquals(BigInteger.ZERO, balance.return_balance);

                DCN.GetSessionBalanceReturnValue sessionBalance = dcnQ.query(DCN::query_get_session_balance,
                        DCN.get_session_balance(user.getAddress(), 0, 0));
                assertEquals(deposit * 2, sessionBalance.asset_balance);
                assertEquals(deposit * 2, sessionBalance.total_deposit);

                ERC20.BalanceofReturnValue tokenBalance;

                tokenBalance = ERC20.query_balanceOf(token1.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(user.getAddress()));
                assertEquals(ogTokenBalance.subtract(allowance), tokenBalance.balance);

                tokenBalance = ERC20.query_balanceOf(token1.value, StaticNetwork.Web3(),
                        ERC20.balanceOf(StaticNetwork.DCN()));
                assertEquals(allowance, tokenBalance.balance);
            });
        });
    }
}
