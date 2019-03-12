package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Spectrum.class)
public class UserManagementTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions user1 = Accounts.getTx(10);
        EtherTransactions user2 = Accounts.getTx(20);
        EtherTransactions user3 = Accounts.getTx(30);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        it("should be able to create user", () -> {
            TransactionReceipt result = assertSuccess(user1.sendCall(StaticNetwork.DCN(), DCN.user_create()));

            assertEquals(1, result.getLogs().size());
            DCN.UserCreated userCreated = DCN.ExtractUserCreated(result.getLogs().get(0));
            assertNotNull(userCreated);
            assertEquals(0, userCreated.user_id);

            DCN.GetUserReturnValue user = query.query(DCN::query_get_user, DCN.get_user(0));
            assertEquals(user1.getAddress(), user.trade_address);
            assertEquals(user1.getAddress(), user.withdraw_address);
            assertEquals(user1.getAddress(), user.recovery_address);

            DCN.GetUserCountReturnValue data = query.query(DCN::query_get_user_count, DCN.get_user_count());
            assertEquals(1, data.count);
        });
    }
}
