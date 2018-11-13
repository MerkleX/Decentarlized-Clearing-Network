package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.ArrayList;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static io.merklex.dcn.utils.BetterAssert.assertEquals;

@RunWith(Spectrum.class)
public class BalanceTests {
    {
        StaticNetwork.DescribeCheckpoint();

        ArrayList<String> tokens = new ArrayList<>();

        EtherTransactions creator = Accounts.getTx(0);

        beforeAll(() -> {
            for (int i = 1; i <= 5; i++) {
                EtherTransactions tokenPerson = Accounts.getTx(i);

                try {
                    String data = ERC20.DeployData(BigInteger.valueOf(1000000000000000000L), "Token " + i, 1 + i, "MTK" + i);
                    String addr = tokenPerson.deployContract(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, data, BigInteger.ZERO);
                    tokens.add(addr);

                    creator.call(
                            StaticNetwork.DCN(),
                            DCN.add_asset("MTK" + i, BigInteger.valueOf(10).pow(i).longValue(), addr)
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        it("Inserted assets should be as expected", () -> {
            for (int i = 1; i <= 5; i++) {
                DCN.GetAssetReturnValue asset = DCN.query_get_asset(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset(i)
                );

                assertEquals(BigInteger.valueOf(10).pow(i).longValue(), asset.unit_scale);
                assertEquals(tokens.get(i - 1), asset.contract_address);
                assertEquals("MTK" + i, asset.symbol);
            }
        });


        EtherTransactions bob = Accounts.getTx(12);

        describe("manage ether", () -> {
            Box<BigInteger> initialBalance = new Box<>();

            it("initial balance should be 0", () -> {
                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 0)
                ).return_balance;

                assertEquals(0, balance);

                initialBalance.value = StaticNetwork.GetBalance(bob.credentials().getAddress());
            });

            it("should be able to deposit", () -> {
                BigInteger amount = BigInteger.valueOf(1230000000000000000L);
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.deposit_eth(),
                        amount
                );

                assertEquals("0x1", tx.getStatus());


                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 0)
                ).return_balance;

                assertEquals(amount, balance);

                BigInteger walletBalance = StaticNetwork.GetBalance(bob.credentials().getAddress());
                assertEquals(initialBalance.value.subtract(amount), walletBalance);
            });

            it("should be able to partially withdraw", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_eth(bob.credentials().getAddress(), BigInteger.valueOf(200000000000000000L)),
                        BigInteger.ZERO
                );

                assertEquals("0x1", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 0)
                ).return_balance;

                assertEquals(1030000000000000000L, balance);

                BigInteger walletBalance = StaticNetwork.GetBalance(bob.credentials().getAddress());
                assertEquals(initialBalance.value.subtract(BigInteger.valueOf(1030000000000000000L)), walletBalance);
            });

            it("should fail to over withdraw", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_eth(bob.credentials().getAddress(), BigInteger.valueOf(1030000000001000000L)),
                        BigInteger.ZERO
                );

                assertEquals("0x0", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 0)
                ).return_balance;

                assertEquals(1030000000000000000L, balance);

                BigInteger walletBalance = StaticNetwork.GetBalance(bob.credentials().getAddress());
                assertEquals(initialBalance.value.subtract(BigInteger.valueOf(1030000000000000000L)), walletBalance);
            });

            it("should be able to withdraw to zero", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_eth(bob.credentials().getAddress(), BigInteger.valueOf(1030000000000000000L)),
                        BigInteger.ZERO
                );

                assertEquals("0x1", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 0)
                ).return_balance;

                assertEquals(0, balance);

                BigInteger walletBalance = StaticNetwork.GetBalance(bob.credentials().getAddress());
                assertEquals(initialBalance.value, walletBalance);
            });


            it("should not be able withdraw at zero", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_eth(bob.credentials().getAddress(), BigInteger.valueOf(1)),
                        BigInteger.ZERO
                );

                assertEquals("0x0", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 0)
                ).return_balance;

                assertEquals(0, balance);

                BigInteger walletBalance = StaticNetwork.GetBalance(bob.credentials().getAddress());
                assertEquals(initialBalance.value, walletBalance);
            });
        });

        describe("manage assets", () -> {
            BigInteger initialBalance = BigInteger.valueOf(100000);

            int assetId = 3;
            int tokenIndex = 2;

            it("should be able to send bob funds over ERC20", () -> {
                BigInteger balance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(0, balance);

                EtherTransactions tokenBoy = Accounts.getTx(assetId);
                TransactionReceipt tx = tokenBoy.call(tokens.get(tokenIndex), ERC20.transfer(bob.credentials().getAddress(), initialBalance));

                assertEquals("0x1", tx.getStatus());

                balance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance, balance);
            });

            it("initial balance should be zero", () -> {
                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), 3)
                ).return_balance;

                assertEquals(0, balance);
            });

            it("should fail to deposit asset without allowance", () -> {
                TransactionReceipt tx = bob.call(StaticNetwork.DCN(), DCN.deposit_asset(assetId, BigInteger.valueOf(10000)));
                assertEquals("0x0", tx.getStatus());
            });

            it("should be able to deposit asset", () -> {
                BigInteger deposit = BigInteger.valueOf(11234);
                TransactionReceipt tx = bob.call(
                        tokens.get(tokenIndex),
                        ERC20.approve(StaticNetwork.DCN(), deposit.multiply(BigInteger.valueOf(2)))
                );

                assertEquals("0x1", tx.getStatus());

                tx = bob.call(StaticNetwork.DCN(), DCN.deposit_asset(assetId, deposit));
                assertEquals("0x1", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), assetId)
                ).return_balance;

                assertEquals(11234, balance);

                BigInteger ercBalance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance.subtract(deposit), ercBalance);
            });

            it("should fail to deposit more than allowance", () -> {
                BigInteger deposit = BigInteger.valueOf(11235);
                TransactionReceipt tx = bob.call(StaticNetwork.DCN(), DCN.deposit_asset(assetId, deposit));
                assertEquals("0x0", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), assetId)
                ).return_balance;

                assertEquals(11234, balance);

                BigInteger ercBalance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance.subtract(BigInteger.valueOf(11234)), ercBalance);
            });

            it("should be able to deposit more", () -> {
                BigInteger deposit = BigInteger.valueOf(100);
                TransactionReceipt tx = bob.call(StaticNetwork.DCN(), DCN.deposit_asset(assetId, deposit));
                assertEquals("0x1", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), assetId)
                ).return_balance;

                assertEquals(11334, balance);

                BigInteger ercBalance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance.subtract(BigInteger.valueOf(11334)), ercBalance);
            });

            it("should be able to partial withdraw", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_asset(assetId, bob.credentials().getAddress(), BigInteger.valueOf(334))
                );

                assertEquals("0x1", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), assetId)
                ).return_balance;

                assertEquals(11000, balance);

                BigInteger ercBalance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance.subtract(BigInteger.valueOf(11000)), ercBalance);
            });

            it("should not be able to overdraft", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_asset(assetId, bob.credentials().getAddress(), BigInteger.valueOf(11010))
                );

                assertEquals("0x0", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), assetId)
                ).return_balance;

                assertEquals(11000, balance);

                BigInteger ercBalance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance.subtract(BigInteger.valueOf(11000)), ercBalance);
            });

            it("should be able to withdraw to zero", () -> {
                TransactionReceipt tx = bob.call(
                        StaticNetwork.DCN(),
                        DCN.withdraw_asset(assetId, bob.credentials().getAddress(), BigInteger.valueOf(11000))
                );

                assertEquals("0x1", tx.getStatus());

                BigInteger balance = DCN.query_get_balance(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_balance(bob.credentials().getAddress(), assetId)
                ).return_balance;

                assertEquals(0, balance);

                BigInteger ercBalance = ERC20.query_balanceOf(
                        tokens.get(tokenIndex), StaticNetwork.Web3(),
                        ERC20.balanceOf(bob.credentials().getAddress())
                ).balance;
                assertEquals(initialBalance, ercBalance);
            });
        });
    }
}
