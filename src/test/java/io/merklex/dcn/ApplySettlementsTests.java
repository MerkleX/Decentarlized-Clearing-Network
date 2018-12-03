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
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
public class ApplySettlementsTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions user1 = Accounts.getTx(1);
        EtherTransactions user2 = Accounts.getTx(2);
        EtherTransactions user3 = Accounts.getTx(3);

        EtherTransactions tokenOwner = Accounts.getTx(4);
        EtherTransactions exchangeOwner = Accounts.getTx(5);

        Box<String> token = new Box<>();

        QueryHelper helper = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        beforeAll(() -> {
            token.value = tokenOwner.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, ERC20.DeployData(
                    new BigInteger("10000000000000000"),
                    "Test Token",
                    10,
                    "TK1"
            ), BigInteger.ZERO);

            creator.call(StaticNetwork.DCN(), DCN.add_asset("TT_1", 100, token.value));
            creator.call(StaticNetwork.DCN(), DCN.add_exchange("testexchange", 0, exchangeOwner.getAddress()));

            BigInteger tokenValue = new BigInteger("1000000000000000");
            tokenOwner.call(token.value, ERC20.transfer(user1.getAddress(), tokenValue));
            tokenOwner.call(token.value, ERC20.transfer(user2.getAddress(), tokenValue));
            tokenOwner.call(token.value, ERC20.transfer(user3.getAddress(), tokenValue));

            user1.call(token.value, ERC20.approve(StaticNetwork.DCN(), tokenValue));
            user2.call(token.value, ERC20.approve(StaticNetwork.DCN(), tokenValue));
            user3.call(token.value, ERC20.approve(StaticNetwork.DCN(), tokenValue));

            BigInteger etherValue = new BigInteger("1230000000000000000");

//            user1.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), etherValue);
//            user2.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), etherValue);
//            user3.call(StaticNetwork.DCN(), DCN.deposit_eth_to_session(1), etherValue);

            user1.call(StaticNetwork.DCN(), DCN.deposit_asset_to_session(1, 1, 10000000000000L));
            user2.call(StaticNetwork.DCN(), DCN.deposit_asset_to_session(1, 1, 10000000000000L));
            user3.call(StaticNetwork.DCN(), DCN.deposit_asset_to_session(1, 1, 10000000000000L));
        });

        it("check initial state", () -> {
            Assert.assertEquals(
                    123000000,
                    helper.query(DCN::query_get_session_balance, DCN.get_session_balance(user1.getAddress(), 1, 0)).asset_balance
            );
            Assert.assertEquals(
                    123000000,
                    helper.query(DCN::query_get_session_balance, DCN.get_session_balance(user2.getAddress(), 1, 0)).asset_balance
            );
            Assert.assertEquals(
                    123000000,
                    helper.query(DCN::query_get_session_balance, DCN.get_session_balance(user3.getAddress(), 1, 0)).asset_balance
            );

            Assert.assertEquals(
                    10000000000000L,
                    helper.query(DCN::query_get_session_balance, DCN.get_session_balance(user1.getAddress(), 1, 1)).asset_balance
            );
            Assert.assertEquals(
                    10000000000000L,
                    helper.query(DCN::query_get_session_balance, DCN.get_session_balance(user2.getAddress(), 1, 1)).asset_balance
            );
            Assert.assertEquals(
                    10000000000000L,
                    helper.query(DCN::query_get_session_balance, DCN.get_session_balance(user3.getAddress(), 1, 1)).asset_balance
            );
        });

        it("apply simple settlement", () -> {
            UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1000));
            Settlements settlements = new Settlements().wrap(buffer, 0);

            Settlements.Group group = settlements.firstGruop(new Settlements.Group());
            group.assetId(1);
        });
    }
}
