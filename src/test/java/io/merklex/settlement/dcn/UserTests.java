package io.merklex.settlement.dcn;

import io.merklex.settlement.contracts.DCN;
import io.merklex.settlement.utils.Genesis;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.util.List;

public class UserTests extends TestBase {
    @Test
    public void addUser() throws Exception {
        DCN bob = StaticNetwork.DCN("bob");
        String bobAddress = Genesis.GetKey("bob").getAddress();

        TransactionReceipt receipt = bob.add_user(bobAddress, bobAddress).send();
        {
            List<Log> logs = receipt.getLogs();
            Assert.assertEquals(1, logs.size());
            Assert.assertEquals("0x00000000", logs.get(0).getData());
        }

        Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals(bobAddress, userData.getValue1());
            Assert.assertEquals(bobAddress, userData.getValue2());
        }
    }

    @Test
    public void addUserWithDifferentTradeKey() throws Exception {
        DCN bob = StaticNetwork.DCN("bob");
        String bobAddress = Genesis.GetKey("bob").getAddress();
        String henryAddress = Genesis.GetKey("henry").getAddress();

        TransactionReceipt receipt = bob.add_user(bobAddress, henryAddress).send();
        {
            List<Log> logs = receipt.getLogs();
            Assert.assertEquals(1, logs.size());
            Assert.assertEquals("0x00000000", logs.get(0).getData());
        }

        Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals(bobAddress, userData.getValue1());
            Assert.assertEquals(henryAddress, userData.getValue2());
        }
    }

    @Test
    public void shouldNotBeAbleToAddUserWithDifferentManagementKey() throws Exception {
        DCN bob = StaticNetwork.DCN("bob");
        String bobAddress = Genesis.GetKey("bob").getAddress();
        String henryAddress = Genesis.GetKey("henry").getAddress();

        TransactionReceipt receipt = bob.add_user(henryAddress, bobAddress).send();
        {
            Assert.assertEquals(0, receipt.getLogs().size());
        }

        Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals("0x0000000000000000000000000000000000000000", userData.getValue1());
            Assert.assertEquals("0x0000000000000000000000000000000000000000", userData.getValue2());
        }
    }

    @Test
    public void changeTradeAddress() throws Exception {
        addUser();

        DCN bob = StaticNetwork.DCN("bob");
        Credentials alice = Genesis.GetKey("alice");
        bob.update_user_trade_addresses(BigInteger.valueOf(0), alice.getAddress()).send();

        Tuple2<String, String> user = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals(Genesis.GetKey("bob").getAddress(), user.getValue1());
            Assert.assertEquals(alice.getAddress(), user.getValue2());
        }
    }

    @Test
    public void onlyManagerAddressCanUpdate() throws Exception {
        addUser();

        DCN bob = StaticNetwork.DCN("bob");
        DCN alice = StaticNetwork.DCN("alice");

        Credentials bobKey = Genesis.GetKey("bob");
        Credentials aliceKey = Genesis.GetKey("alice");
        Credentials jackKey = Genesis.GetKey("jack");

        alice.update_user_trade_addresses(BigInteger.valueOf(0), aliceKey.getAddress()).send();

        Tuple2<String, String> user = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals(bobKey.getAddress(), user.getValue1());
            Assert.assertEquals(bobKey.getAddress(), user.getValue2());
        }

        bob.update_user_trade_addresses(BigInteger.valueOf(0), aliceKey.getAddress()).send();

        user = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals(Genesis.GetKey("bob").getAddress(), user.getValue1());
            Assert.assertEquals(aliceKey.getAddress(), user.getValue2());
        }

        alice.update_user_trade_addresses(BigInteger.valueOf(0), jackKey.getAddress()).send();

        user = bob.get_user(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals(Genesis.GetKey("bob").getAddress(), user.getValue1());
            Assert.assertEquals(aliceKey.getAddress(), user.getValue2());
        }
    }
}
