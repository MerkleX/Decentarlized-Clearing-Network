//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.contracts.ERC20;
//import io.merklex.dcn.network.Utils;
//import io.merklex.dcn.utils.Box;
//import io.merklex.dcn.utils.Genesis;
//import io.merklex.dcn.utils.StaticNetwork;
//import org.junit.runner.RunWith;
//import org.web3j.crypto.Credentials;
//import org.web3j.protocol.core.methods.response.Log;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import java.math.BigInteger;
//
//import static com.greghaskins.spectrum.dsl.specification.Specification.*;
//import static io.merklex.dcn.utils.BetterAssert.assertEquals;
//
//@RunWith(Spectrum.class)
//public class UserAssetBalanceTests {
//    {
//        StaticNetwork.DescribeCheckpoint();
//        DCN jackDCN = StaticNetwork.DCN("jack");
//
//        Credentials jackKey = Genesis.GetKey("jack");
//        final ERC20 jackToken;
//
//        try {
//            jackToken = ERC20.deploy(StaticNetwork.Web3(), jackKey, BigInteger.ONE,
//                    BigInteger.valueOf(8000000),
//                    BigInteger.valueOf(10000000000000L),
//                    "jack token",
//                    BigInteger.valueOf(8),
//                    "JTK"
//            ).send();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        beforeAll(() -> {
//            jackDCN.executeTransaction(DCN.add_user(jackKey.getAddress()));
//            StaticNetwork.DCN().executeTransaction(DCN.add_asset("JTK ", BigInteger.ONE, jackToken.getContractAddress()));
//        });
//
//        it("DNC balance should 0", () -> {
//            assertEquals(
//                    0,
//                    jackToken.balanceOf(StaticNetwork.DCN().getContractAddress()).send()
//            );
//        });
//
//        it("should be fail to deposit funds without allowance", () -> {
//            jackDCN.executeTransaction(DCN.deposit_asset(BigInteger.valueOf(0), BigInteger.ONE, BigInteger.valueOf(100)));
//            BigInteger qty = jackDCN.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(1));
//            assertEquals(0, qty);
//        });
//
//        describe("should be able to deposit asset", () -> {
//            beforeAll(() -> {
//                jackToken.approve(StaticNetwork.DCN().getContractAddress(), BigInteger.valueOf(100000)).send();
//
//                assertEquals(
//                        100000,
//                        jackToken.allowance(jackKey.getAddress(), StaticNetwork.DCN().getContractAddress()).send()
//                );
//            });
//
//            Box<TransactionReceipt> receipt = new Box<>();
//            it("deposit funds", () -> {
//                receipt.value = jackDCN.executeTransaction(DCN.deposit_asset(BigInteger.valueOf(0), BigInteger.ONE, BigInteger.valueOf(100)));
//            });
//
//            it("should have log for ERC-20 transfer", () -> {
//                assertEquals(2, receipt.value.getLogs().size());
//
//                Log assetTransferLog = receipt.value.getLogs().get(0);
//                assertEquals(3, assetTransferLog.getTopics().size());
//                assertEquals("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", assetTransferLog.getTopics().get(0));
//                assertEquals(Utils.ToLargeHex(jackKey.getAddress()), assetTransferLog.getTopics().get(1));
//                assertEquals(Utils.ToLargeHex(jackDCN.getContractAddress()), assetTransferLog.getTopics().get(2));
//                assertEquals("0x0000000000000000000000000000000000000000000000000000000000000064", assetTransferLog.getData());
//            });
//
//            it("should have success log", () -> {
//                Log assetTransferLog = receipt.value.getLogs().get(receipt.value.getLogs().size() - 1);
//                assertEquals(0, assetTransferLog.getTopics().size());
//                assertEquals("0x00", assetTransferLog.getData());
//            });
//
//            it("DNC balance should have increased", () -> {
//                BigInteger qty = jackDCN.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(1));
//                assertEquals(100, qty);
//            });
//
//            it("asset balance should decrease", () -> {
//                assertEquals(
//                        10000000000000L - 100,
//                        jackToken.balanceOf(jackKey.getAddress()).send()
//                );
//            });
//
//            it("DNC balance should increase", () -> {
//                assertEquals(
//                        100,
//                        jackToken.balanceOf(StaticNetwork.DCN().getContractAddress()).send()
//                );
//            });
//        });
//
//        describe("should be able to withdraw funds", () -> {
//            Box<TransactionReceipt> receipt = new Box<>();
//
//            it("withdraw asset", () -> {
//                receipt.value = jackDCN.executeTransaction(DCN.withdraw_asset(BigInteger.valueOf(0), true,
//                        BigInteger.ONE, jackKey.getAddress(), BigInteger.valueOf(10)));
//            });
//
//            it("should have log for ERC-20 transfer", () -> {
//                assertEquals(2, receipt.value.getLogs().size());
//
//                Log assetTransferLog = receipt.value.getLogs().get(0);
//                assertEquals(3, assetTransferLog.getTopics().size());
//                assertEquals("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", assetTransferLog.getTopics().get(0));
//                assertEquals(Utils.ToLargeHex(jackDCN.getContractAddress()), assetTransferLog.getTopics().get(1));
//                assertEquals(Utils.ToLargeHex(jackKey.getAddress()), assetTransferLog.getTopics().get(2));
//                assertEquals("0x000000000000000000000000000000000000000000000000000000000000000a", assetTransferLog.getData());
//            });
//
//            it("should have success log", () -> {
//                Log assetTransferLog = receipt.value.getLogs().get(receipt.value.getLogs().size() - 1);
//                assertEquals(0, assetTransferLog.getTopics().size());
//                assertEquals("0x00", assetTransferLog.getData());
//            });
//
//            it("DNC balance should be decreased", () -> {
//                assertEquals(
//                        90,
//                        jackDCN.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(1))
//                );
//            });
//
//            it("asset balance should increase", () -> {
//                assertEquals(
//                        10000000000000L - 90,
//                        jackToken.balanceOf(jackKey.getAddress()).send()
//                );
//            });
//
//            it("DNC's asset balance should decrease", () -> {
//                assertEquals(
//                        90,
//                        jackToken.balanceOf(StaticNetwork.DCN().getContractAddress()).send()
//                );
//            });
//        });
//    }
//}
