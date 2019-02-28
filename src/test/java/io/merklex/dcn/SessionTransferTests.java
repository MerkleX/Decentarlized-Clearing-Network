package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.dsl.specification.Specification;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.beforeAll;

@RunWith(Spectrum.class)
public class SessionTransferTests {
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


    }
}
