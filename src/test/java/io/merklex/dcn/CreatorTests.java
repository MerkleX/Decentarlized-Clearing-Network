package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
public class CreatorTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions account10 = Accounts.getTx(10);
        EtherTransactions account20 = Accounts.getTx(20);
        EtherTransactions account30 = Accounts.getTx(30);

        QueryHelper query = new QueryHelper(null, StaticNetwork.Web3());

        beforeAll(() -> {
            query.contractAddress = account10.deployContract(
                    BigInteger.ZERO,
                    StaticNetwork.GAS_LIMIT,
                    DCN.DeployData(),
                    BigInteger.ZERO
            );
        });

        it("contract creator should be creator", () -> {
            DCN.GetCreatorReturnValue creator = query.query(DCN::query_get_creator, DCN.get_creator());

            assertEquals(account10.getAddress(), creator.dcn_creator);
            assertEquals(account10.getAddress(), creator.dcn_creator_recovery);
            assertEquals("0x0000000000000000000000000000000000000000", creator.dcn_creator_recovery_proposed);
        });

        it("only creator should be able to add exchange", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.add_exchange("12345678901", account10.getAddress())));

            assertSuccess(account10.sendCall(query.contractAddress,
                    DCN.add_exchange("12345678901", account20.getAddress())));
        });

        it("only creator not be able to add asset", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.add_asset("1234", 1, account10.getAddress())));

            assertSuccess(account10.sendCall(query.contractAddress,
                    DCN.add_asset("1234", 1, account10.getAddress())));
        });

        it("only creator should be able to update address", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.creator_update(account20.getAddress())));

            assertSuccess(account10.sendCall(query.contractAddress,
                    DCN.creator_update(account20.getAddress())));

            DCN.GetCreatorReturnValue creator = query.query(DCN::query_get_creator, DCN.get_creator());
            assertEquals(account20.getAddress(), creator.dcn_creator);
            assertEquals(account10.getAddress(), creator.dcn_creator_recovery);
            assertEquals("0x0000000000000000000000000000000000000000", creator.dcn_creator_recovery_proposed);
        });

        it("only recover code should be able to update address", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.creator_update(account30.getAddress())));

            assertSuccess(account10.sendCall(query.contractAddress,
                    DCN.creator_update(account30.getAddress())));

            DCN.GetCreatorReturnValue creator = query.query(DCN::query_get_creator, DCN.get_creator());
            assertEquals(account30.getAddress(), creator.dcn_creator);
            assertEquals(account10.getAddress(), creator.dcn_creator_recovery);
            assertEquals("0x0000000000000000000000000000000000000000", creator.dcn_creator_recovery_proposed);
        });

        it("only recovery should be able to propose recovery update", () -> {
            assertRevert("0x01", account30.sendCall(query.contractAddress,
                    DCN.creator_propose_recovery(account20.getAddress())));

            assertSuccess(account10.sendCall(query.contractAddress,
                    DCN.creator_propose_recovery(account20.getAddress())));

            DCN.GetCreatorReturnValue creator = query.query(DCN::query_get_creator, DCN.get_creator());
            assertEquals(account30.getAddress(), creator.dcn_creator);
            assertEquals(account10.getAddress(), creator.dcn_creator_recovery);
            assertEquals(account20.getAddress(), creator.dcn_creator_recovery_proposed);
        });

        it("only proposed should be able to update recovery", () -> {
            assertRevert("0x01", account30.sendCall(query.contractAddress,
                    DCN.creator_update_recovery()));

            assertRevert("0x01", account10.sendCall(query.contractAddress,
                    DCN.creator_update_recovery()));

            assertSuccess(account20.sendCall(query.contractAddress,
                    DCN.creator_update_recovery()));

            DCN.GetCreatorReturnValue creator = query.query(DCN::query_get_creator, DCN.get_creator());
            assertEquals(account30.getAddress(), creator.dcn_creator);
            assertEquals(account20.getAddress(), creator.dcn_creator_recovery);
            assertEquals("0x0000000000000000000000000000000000000000", creator.dcn_creator_recovery_proposed);
        });

        it("only creator should be able to security lock", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.security_lock(BigInteger.valueOf(5))));

            assertSuccess(account30.sendCall(query.contractAddress,
                    DCN.security_lock(BigInteger.valueOf(5))));
        });

        it("only creator should be able to propose security", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.security_propose(BigInteger.valueOf(4))));

            assertSuccess(account30.sendCall(query.contractAddress,
                    DCN.security_propose(BigInteger.valueOf(4))));
        });

        it("only creator should be able to set proposed security", () -> {
            assertRevert("0x01", account20.sendCall(query.contractAddress,
                    DCN.security_set_proposed()));

            assertRevert("0x02", account30.sendCall(query.contractAddress,
                    DCN.security_set_proposed()));
        });
    }
}
