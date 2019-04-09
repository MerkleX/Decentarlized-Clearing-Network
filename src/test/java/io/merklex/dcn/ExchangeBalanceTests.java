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

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;
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

        BigInteger totalSupply = BigInteger.valueOf(2).pow(255);

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
        });

        it("should not be able to overflow deposit", () -> {
            assertRevert("0x01", creator.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_deposit(0, 0, -5000)));

//            DCN.GetExchangeBalanceReturnValue balance;
//            balance = DCN.query_get_exchange_balance(StaticNetwork.DCN(), creator.getWeb3(),
//                    DCN.get_exchange_balance(0, 0));
//
//            assertEquals(BigInteger.valueOf(2).shiftLeft(64).subtract(BigInteger.valueOf(100)),
//                    balance.exchange_balance);
        });
    }
}
