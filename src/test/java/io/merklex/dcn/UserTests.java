package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Genesis;
import io.merklex.dcn.utils.StaticNetwork;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;

@RunWith(Spectrum.class)
public class UserTests {
    {
        StaticNetwork.DescribeCheckpoint();

        describe("add user", () -> {
            DCN bob = StaticNetwork.DCN("bob");
            DCN henry = StaticNetwork.DCN("henry");
            DCN alice = StaticNetwork.DCN("alice");

            String bobAddress = Genesis.GetKey("bob").getAddress();
            String henryAddress = Genesis.GetKey("henry").getAddress();
            String aliceAddress = Genesis.GetKey("alice").getAddress();

            it("user count should be zero", () -> {
                Assert.assertEquals(BigInteger.ZERO, bob.get_user_count().send());
            });

            describe("should able to add user with same management and trade key", () -> {
                it("should add user with id 0", () -> {
                    TransactionReceipt receipt = bob.add_user(bobAddress).send();

                    List<Log> logs = receipt.getLogs();
                    Assert.assertEquals(1, logs.size());
                    Assert.assertEquals("0x00000000", logs.get(0).getData());
                });

                it("user count should now be 1", () -> {
                    Assert.assertEquals(BigInteger.valueOf(1), bob.get_user_count().send());
                });

                it("should be able to query user", () -> {
                    Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(0)).send();
                    Assert.assertEquals(bobAddress, userData.getValue1());
                    Assert.assertEquals(bobAddress, userData.getValue2());
                });
            });

            describe("should able to add user with different management and trade key", () -> {
                it("should add user with id 0", () -> {
                    TransactionReceipt receipt = bob.add_user(henryAddress).send();

                    List<Log> logs = receipt.getLogs();
                    Assert.assertEquals(1, logs.size());
                    Assert.assertEquals("0x00000001", logs.get(0).getData());
                });

                it("user count should now be 2", () -> {
                    Assert.assertEquals(BigInteger.valueOf(2), bob.get_user_count().send());
                });

                it("should be able to query user", () -> {
                    Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(1)).send();
                    Assert.assertEquals(bobAddress, userData.getValue1());
                    Assert.assertEquals(henryAddress, userData.getValue2());
                });

                it("first user should not be modified", () -> {
                    Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(0)).send();
                    Assert.assertEquals(bobAddress, userData.getValue1());
                    Assert.assertEquals(bobAddress, userData.getValue2());
                });
            });

            describe("update user trade address", () -> {
                describe("should be able to update", () -> {
                    StaticNetwork.DescribeCheckpoint();

                    it("update address", () -> {
                        bob.update_user_trade_addresses(BigInteger.valueOf(0), aliceAddress).send();
                    });

                    it("update should be applied", () -> {
                        Tuple2<String, String> user = bob.get_user(BigInteger.valueOf(0)).send();
                        Assert.assertEquals(bobAddress, user.getValue1());
                        Assert.assertEquals(aliceAddress, user.getValue2());
                    });

                    it("user count should still be 2", () -> {
                        Assert.assertEquals(BigInteger.valueOf(2), bob.get_user_count().send());
                    });
                });

                describe("should be protected", () -> {
                    it("should not be able to update user 1", () -> {
                        alice.update_user_trade_addresses(BigInteger.valueOf(1), bobAddress).send();

                        Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(1)).send();
                        Assert.assertEquals(bobAddress, userData.getValue1());
                        Assert.assertEquals(henryAddress, userData.getValue2());

                        henry.update_user_trade_addresses(BigInteger.valueOf(1), bobAddress).send();

                        userData = bob.get_user(BigInteger.valueOf(1)).send();
                        Assert.assertEquals(bobAddress, userData.getValue1());
                        Assert.assertEquals(henryAddress, userData.getValue2());
                    });

                    it("should not be able to update user 0", () -> {
                        alice.update_user_trade_addresses(BigInteger.valueOf(0), henryAddress).send();

                        Tuple2<String, String> userData = bob.get_user(BigInteger.valueOf(0)).send();
                        Assert.assertEquals(bobAddress, userData.getValue1());
                        Assert.assertEquals(bobAddress, userData.getValue2());

                        henry.update_user_trade_addresses(BigInteger.valueOf(0), henryAddress).send();

                        userData = bob.get_user(BigInteger.valueOf(0)).send();
                        Assert.assertEquals(bobAddress, userData.getValue1());
                        Assert.assertEquals(bobAddress, userData.getValue2());
                    });
                });
            });
        });
    }
}