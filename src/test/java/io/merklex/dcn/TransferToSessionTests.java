package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
public class TransferToSessionTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions tokenOwner = Accounts.getTx(22);
        EtherTransactions user = Accounts.getTx(33);
        Box<String> tokenAddress = new Box<>();

        beforeAll(() -> {
            user.call(StaticNetwork.DCN(), DCN.deposit_eth(), BigInteger.valueOf(15).multiply(BigInteger.TEN.pow(18)));

            tokenAddress.value = tokenOwner.deployContract(
                    BigInteger.ZERO,
                    StaticNetwork.GAS_LIMIT,
                    ERC20.DeployData(BigInteger.valueOf(10000_000000L), "Test Token", 6, "TT"),
                    BigInteger.ZERO
            );

            creator.call(StaticNetwork.DCN(), DCN.add_asset("XXXX", 10000, tokenAddress.value));
            tokenOwner.call(tokenAddress.value, ERC20.transfer(user.getAddress(), BigInteger.valueOf(5_000000L)));
            user.call(tokenAddress.value, ERC20.approve(StaticNetwork.DCN(), BigInteger.valueOf(5_000000L)));
        });

        RunTestWith(user, 0);
        RunTestWith(user, 1);
    }

    private static void RunTestWith(EtherTransactions user, int assetId) {
        QueryHelper helper = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        describe("For asset: " + assetId, () -> {
            Box<Long> unitScale = new Box<>();
            Box<BigInteger> initialBalance = new Box<>();

            beforeAll(() -> {
                unitScale.value = helper.query(DCN::query_get_asset, DCN.get_asset(assetId)).unit_scale;
                initialBalance.value = helper.query(DCN::query_get_balance, DCN.get_balance(user.getAddress(), assetId))
                        .return_balance;
            });

            it("should not be able to transfer from 0 funds account", () -> {
                TransactionReceipt tx = user.call(StaticNetwork.DCN(),
                        DCN.transfer_to_session(3, assetId, 10));
                Assert.assertEquals("0x0", tx.getStatus());
            });

//            it("should be able to transfer single unit", () -> {
//                user.call(StaticNetwork.DCN(), DCN.transfer_to_session())
//            });
        });
    }
}
