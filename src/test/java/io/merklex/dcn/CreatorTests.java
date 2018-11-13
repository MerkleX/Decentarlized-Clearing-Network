package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
public class CreatorTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions account10 = Accounts.getTx(10);
        EtherTransactions account20 = Accounts.getTx(20);
        EtherTransactions account30 = Accounts.getTx(30);

        Box<String> contractAddress = new Box<>();

        beforeAll(() -> {
            String s = DCN.DeployData();
            contractAddress.value = account10
                    .deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, s, BigInteger.ZERO);
        });

        it("contract creator should be creator", () -> {
            DCN.GetCreatorReturnValue creatorAddress = DCN.query_get_creator(contractAddress.value, StaticNetwork.Web3(), DCN.get_creator());
            Assert.assertEquals(account10.credentials().getAddress(), creatorAddress.dcn_creator);
        });

        it("non creator should not be able to change creator to self", () -> {
            TransactionReceipt tx = account20.call(contractAddress.value, DCN.set_creator(account20.credentials().getAddress()));
            Assert.assertEquals("0x0", tx.getStatus());
        });

        it("non creator should not be able to change creator to other", () -> {
            TransactionReceipt tx = account20.call(contractAddress.value, DCN.set_creator(account30.credentials().getAddress()));
            Assert.assertEquals("0x0", tx.getStatus());
        });

        it("creator should be able to change to other", () -> {
            TransactionReceipt tx = account10.call(contractAddress.value, DCN.set_creator(account30.credentials().getAddress()));
            Assert.assertEquals("0x1", tx.getStatus());

            DCN.GetCreatorReturnValue creatorAddress = DCN.query_get_creator(contractAddress.value, StaticNetwork.Web3(), DCN.get_creator());
            Assert.assertEquals(account30.credentials().getAddress(), creatorAddress.dcn_creator);
        });

        it("new creator should be able to change to other", () -> {
            TransactionReceipt tx = account30.call(contractAddress.value, DCN.set_creator(account20.credentials().getAddress()));
            Assert.assertEquals("0x1", tx.getStatus());

            DCN.GetCreatorReturnValue creatorAddress = DCN.query_get_creator(contractAddress.value, StaticNetwork.Web3(), DCN.get_creator());
            Assert.assertEquals(account20.credentials().getAddress(), creatorAddress.dcn_creator);
        });
    }
}
