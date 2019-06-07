package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Spectrum.class)
public class UserManagementTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions user0 = Accounts.getTx(1);
        EtherTransactions user0Trade = Accounts.getTx(2);
        EtherTransactions user0Withdraw = Accounts.getTx(3);
        EtherTransactions user0Recover = Accounts.getTx(4);
        EtherTransactions notUser = Accounts.getTx(10);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        int userId = 1;

        beforeAll(() -> {
            assertSuccess(user0.sendCall(StaticNetwork.DCN(), DCN.user_create()));
        });

        it("should be able to create user", () -> {
            DCN.GetUserCountReturnValue userCountBefore = query.query(DCN::query_get_user_count, DCN.get_user_count());

            TransactionReceipt result = assertSuccess(user0.sendCall(StaticNetwork.DCN(), DCN.user_create()));

            assertEquals(1, result.getLogs().size());
            DCN.UserCreated userCreated = DCN.ExtractUserCreated(result.getLogs().get(0));
            assertNotNull(userCreated);
            assertEquals(userId, userCreated.user_id);
            assertEquals(user0.getAddress(), userCreated.creator);

            DCN.GetUserReturnValue user = query.query(DCN::query_get_user, DCN.get_user(userId));
            assertEquals(user0.getAddress(), user.trade_address);
            assertEquals(user0.getAddress(), user.withdraw_address);
            assertEquals(user0.getAddress(), user.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", user.recovery_address_proposed);

            DCN.GetUserCountReturnValue userCountAfter = query.query(DCN::query_get_user_count, DCN.get_user_count());
            assertEquals(userCountBefore.count + 1, userCountAfter.count);
        });

        it("should be able to propose recovery address", () -> {
            assertRevert("0x01", notUser.sendCall(StaticNetwork.DCN(),
                    DCN.user_propose_recovery_address(userId, user0Recover.getAddress())));

            assertSuccess(user0.sendCall(StaticNetwork.DCN(),
                    DCN.user_propose_recovery_address(userId, user0Recover.getAddress())));

            DCN.GetUserReturnValue user = query.query(DCN::query_get_user, DCN.get_user(userId));
            assertEquals(user0.getAddress(), user.trade_address);
            assertEquals(user0.getAddress(), user.withdraw_address);
            assertEquals(user0.getAddress(), user.recovery_address);
            assertEquals(user0Recover.getAddress(), user.recovery_address_proposed);
        });

        it("should only be able to set recovery address using recovery address", () -> {
            assertRevert("0x01", user0.sendCall(StaticNetwork.DCN(),
                    DCN.user_set_recovery_address(userId)));

            assertSuccess(user0Recover.sendCall(StaticNetwork.DCN(),
                    DCN.user_set_recovery_address(userId)));

            DCN.GetUserReturnValue user = query.query(DCN::query_get_user, DCN.get_user(userId));
            assertEquals(user0.getAddress(), user.trade_address);
            assertEquals(user0.getAddress(), user.withdraw_address);
            assertEquals(user0Recover.getAddress(), user.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", user.recovery_address_proposed);
        });

        it("should be able to update trade address", () -> {
            assertRevert("0x01", notUser.sendCall(StaticNetwork.DCN(),
                    DCN.user_set_trade_address(userId, user0Trade.getAddress())));

            TransactionReceipt tx = assertSuccess(user0Recover.sendCall(StaticNetwork.DCN(),
                    DCN.user_set_trade_address(userId, user0Trade.getAddress())));

            assertEquals(1, tx.getLogs().size());
            DCN.UserTradeAddressUpdated updated = DCN.ExtractUserTradeAddressUpdated(tx.getLogs().get(0));

            assertNotNull(updated);
            assertEquals(userId, updated.user_id);

            DCN.GetUserReturnValue user = query.query(DCN::query_get_user, DCN.get_user(userId));
            assertEquals(user0Trade.getAddress(), user.trade_address);
            assertEquals(user0.getAddress(), user.withdraw_address);
            assertEquals(user0Recover.getAddress(), user.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", user.recovery_address_proposed);
        });

        it("should be able to update withdraw address", () -> {
            assertRevert("0x01", notUser.sendCall(StaticNetwork.DCN(),
                    DCN.user_set_withdraw_address(userId, user0Withdraw.getAddress())));

            assertSuccess(user0Recover.sendCall(StaticNetwork.DCN(),
                    DCN.user_set_withdraw_address(userId, user0Withdraw.getAddress())));

            DCN.GetUserReturnValue user = query.query(DCN::query_get_user, DCN.get_user(userId));
            assertEquals(user0Trade.getAddress(), user.trade_address);
            assertEquals(user0Withdraw.getAddress(), user.withdraw_address);
            assertEquals(user0Recover.getAddress(), user.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", user.recovery_address_proposed);
        });

        it("should not be able to create user if security locked", () -> {
            assertSuccess(creator.sendCall(StaticNetwork.DCN(), DCN.security_lock(FeatureLocks.CREATE_USER)));
            assertRevert("0x00", user0.sendCall(StaticNetwork.DCN(), DCN.user_create()));
        });
    }
}
