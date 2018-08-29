package io.merklex.settlement.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.settlement.contracts.DCN;
import io.merklex.settlement.utils.Box;
import io.merklex.settlement.utils.Genesis;
import org.junit.runner.RunWith;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static io.merklex.settlement.utils.BetterAssert.assertEquals;

@RunWith(Spectrum.class)
public class UserBalanceTests {
    {
        StaticNetwork.DescribeCheckpoint();

        describe("deposit ether", () -> {
            StaticNetwork.DescribeCheckpoint();

            DCN bob = StaticNetwork.DCN("bob");
            Credentials bobKey = Genesis.GetKey("bob");

            BigInteger bobInitialBalance = StaticNetwork.GetBalance(bobKey.getAddress());

            Box<TransactionReceipt> addUserReceipt = new Box<>();
            beforeAll(() -> {
                addUserReceipt.value = bob.add_user(bobKey.getAddress(), bobKey.getAddress()).send();
            });

            Box<TransactionReceipt> depositReceipt = new Box<>();
            it("deposit ether", () -> {
                depositReceipt.value = bob.deposit_eth(BigInteger.valueOf(0), true, BigInteger.valueOf(1000)).send();
            });

            it("user balance should be updated", () -> {
                BigInteger expectedBalance = bobInitialBalance
                        .subtract(addUserReceipt.value.getGasUsed())
                        .subtract(depositReceipt.value.getGasUsed())
                        .subtract(BigInteger.valueOf(1000));
                assertEquals(expectedBalance, StaticNetwork.GetBalance(bobKey.getAddress()));
            });

            it("contract should have balance", () -> {
                assertEquals(
                        BigInteger.valueOf(1000),
                        StaticNetwork.GetBalance(bob.getContractAddress())
                );
            });

            it("contract should register deposit", () -> {
                BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                assertEquals(BigInteger.valueOf(1000), value);
            });

            describe("should be able to have partial withdraw", () -> {
                StaticNetwork.DescribeCheckpoint();

                Box<BigInteger> startBalance = new Box<>();
                beforeAll(() -> {
                    startBalance.value = StaticNetwork.GetBalance(bobKey.getAddress());
                });

                Box<TransactionReceipt> withdrawReceipt = new Box<>();

                it("withdraw", () -> {
                    withdrawReceipt.value = bob.withdraw_eth(BigInteger.valueOf(0), bobKey.getAddress(),
                            true, BigInteger.valueOf(500)).send();
                });

                it("contract balance should be 500", () -> {
                    assertEquals(
                            BigInteger.valueOf(500),
                            StaticNetwork.GetBalance(bob.getContractAddress())
                    );
                });

                it("user balance should change -fee + 500", () -> {
                    assertEquals(
                            startBalance.value
                                    .subtract(withdrawReceipt.value.getGasUsed())
                                    .add(BigInteger.valueOf(500)),
                            StaticNetwork.GetBalance(bobKey.getAddress()));
                });

                it("contract user balance should be 500", () -> {
                    BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(BigInteger.valueOf(500), value);
                });
            });

            describe("should be able to have full withdraw", () -> {
                StaticNetwork.DescribeCheckpoint();

                Box<BigInteger> startBalance = new Box<>();
                beforeAll(() -> {
                    startBalance.value = StaticNetwork.GetBalance(bobKey.getAddress());
                });

                Box<TransactionReceipt> withdrawReceipt = new Box<>();

                it("withdraw", () -> {
                    withdrawReceipt.value = bob.withdraw_eth(BigInteger.valueOf(0), bobKey.getAddress(),
                            true, BigInteger.valueOf(1000)).send();
                });

                it("contract balance should be 0", () -> {
                    assertEquals(
                            BigInteger.valueOf(0),
                            StaticNetwork.GetBalance(bob.getContractAddress())
                    );
                });

                it("user balance should change -fee + 1000", () -> {
                    assertEquals(
                            startBalance.value
                                    .subtract(withdrawReceipt.value.getGasUsed())
                                    .add(BigInteger.valueOf(1000)),
                            StaticNetwork.GetBalance(bobKey.getAddress()));
                });

                it("contract user balance should be 0", () -> {
                    BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(BigInteger.valueOf(0), value);
                });
            });

            describe("should not be able to withdraw more than balance", () -> {
                StaticNetwork.DescribeCheckpoint();

                Box<BigInteger> startBalance = new Box<>();
                beforeAll(() -> {
                    startBalance.value = StaticNetwork.GetBalance(bobKey.getAddress());
                });

                Box<TransactionReceipt> withdrawReceipt = new Box<>();

                it("withdraw", () -> {
                    withdrawReceipt.value = bob.withdraw_eth(BigInteger.valueOf(0), bobKey.getAddress(),
                            true, BigInteger.valueOf(1001)).send();
                });

                it("contract balance should be the same", () -> {
                    assertEquals(
                            BigInteger.valueOf(1000),
                            StaticNetwork.GetBalance(bob.getContractAddress())
                    );
                });

                it("user balance should be the same", () -> {
                    assertEquals(startBalance.value.subtract(withdrawReceipt.value.getGasUsed()),
                            StaticNetwork.GetBalance(bobKey.getAddress()));
                });

                it("contract user balance should be the same", () -> {
                    BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(BigInteger.valueOf(1000), value);
                });
            });

            DCN alice = StaticNetwork.DCN("alice");
            Credentials aliceKey = Genesis.GetKey("alice");

            describe("other user should not be able to withdraw", () -> {
                StaticNetwork.DescribeCheckpoint();

                Box<BigInteger> startBalance = new Box<>();
                beforeAll(() -> {
                    startBalance.value = StaticNetwork.GetBalance(bobKey.getAddress());
                });

                it("attempt withdraw", () -> {
                    alice.withdraw_eth(BigInteger.valueOf(0), aliceKey.getAddress(),
                            false, BigInteger.valueOf(1)).send();
                });

                it("contract balance should be the same", () -> {
                    assertEquals(
                            BigInteger.valueOf(1000),
                            StaticNetwork.GetBalance(bob.getContractAddress())
                    );
                });

                it("contract user balance should be the same", () -> {
                    BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(BigInteger.valueOf(1000), value);
                });

                it("user balance should be the same", () -> {
                    assertEquals(startBalance.value, StaticNetwork.GetBalance(bobKey.getAddress()));
                });
            });

            describe("should not be able to withdraw to other address with check self", () -> {
                StaticNetwork.DescribeCheckpoint();

                Box<BigInteger> startBalance = new Box<>();
                beforeAll(() -> {
                    startBalance.value = StaticNetwork.GetBalance(bobKey.getAddress());
                });

                Box<TransactionReceipt> withdrawReceipt = new Box<>();

                it("attempt withdraw", () -> {
                    withdrawReceipt.value = bob.withdraw_eth(BigInteger.valueOf(0), aliceKey.getAddress(),
                            true, BigInteger.valueOf(1)).send();
                });

                it("contract balance should be the same", () -> {
                    assertEquals(
                            BigInteger.valueOf(1000),
                            StaticNetwork.GetBalance(bob.getContractAddress())
                    );
                });

                it("user balance should be the same", () -> {
                    assertEquals(startBalance.value.subtract(withdrawReceipt.value.getGasUsed()),
                            StaticNetwork.GetBalance(bobKey.getAddress()));
                });

                it("contract user balance should be the same", () -> {
                    BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(BigInteger.valueOf(1000), value);
                });
            });

            describe("should be able to withdraw to other address", () -> {
                StaticNetwork.DescribeCheckpoint();

                Box<BigInteger> bobStartBalance = new Box<>();
                Box<BigInteger> aliceStartBalance = new Box<>();
                beforeAll(() -> {
                    bobStartBalance.value = StaticNetwork.GetBalance(bobKey.getAddress());
                    aliceStartBalance.value = StaticNetwork.GetBalance(aliceKey.getAddress());
                });

                Box<TransactionReceipt> withdrawReceipt = new Box<>();

                it("withdraw 100", () -> {
                    withdrawReceipt.value = bob.withdraw_eth(BigInteger.valueOf(0), aliceKey.getAddress(),
                            false, BigInteger.valueOf(100)).send();
                });

                it("contract balance be 900", () -> {
                    assertEquals(
                            BigInteger.valueOf(900),
                            StaticNetwork.GetBalance(bob.getContractAddress())
                    );
                });

                it("bob's balance should decrease by fee", () -> {
                    assertEquals(bobStartBalance.value.subtract(withdrawReceipt.value.getGasUsed()),
                            StaticNetwork.GetBalance(bobKey.getAddress()));
                });

                it("alice's balance should increase by withdraw", () -> {
                    assertEquals(aliceStartBalance.value.add(BigInteger.valueOf(100)),
                            StaticNetwork.GetBalance(aliceKey.getAddress()));
                });

                it("contract user balance should be 900", () -> {
                    BigInteger value = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(BigInteger.valueOf(900), value);
                });
            });
        });
    }
}
