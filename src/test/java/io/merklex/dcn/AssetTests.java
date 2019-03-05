//package io.merklex.dcn;
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.web3.EtherTransactions;
//import org.junit.runner.RunWith;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
//import static com.greghaskins.spectrum.dsl.specification.Specification.it;
//import static org.junit.Assert.assertEquivalent;
//
//@RunWith(Spectrum.class)
//public class AssetTests {
//    {
//        StaticNetwork.DescribeCheckpoint();
//
//        describe("initial state checks", () -> {
//            it("asset count should be zero", () -> {
//                int count = DCN.query_get_asset_count(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset_count()
//                ).count;
//                assertEquivalent(0, count);
//            });
//
//            it("non allocated assets should be empty", () -> {
//                for (int i = 0; i < 10; i++) {
//                    DCN.GetAssetReturnValue asset = DCN.query_get_asset(
//                            StaticNetwork.DCN(), StaticNetwork.Web3(),
//                            DCN.get_asset(i)
//                    );
//
//                    assertEquivalent("", asset.symbol.trim());
//                    assertEquivalent(0L, asset.unit_scale);
//                    assertEquivalent("0x0000000000000000000000000000000000000000", asset.contract_address);
//                }
//            });
//        });
//
//        EtherTransactions creator = Accounts.getTx(0);
//        EtherTransactions bob = Accounts.getTx(1);
//
//        describe("add assets", () -> {
//            it("non creator should not be able to add asset", () -> {
//                TransactionReceipt tx = bob.call(StaticNetwork.DCN(), DCN.add_asset("ABCD", 100, bob.credentials().getAddress()));
//                assertEquivalent("0x0", tx.getStatus());
//
//                int count = DCN.query_get_asset_count(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset_count()
//                ).count;
//                assertEquivalent(0, count);
//            });
//
//            it("creator should be able to add asset", () -> {
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 1000, bob.credentials().getAddress()));
//                assertEquivalent("0x1", tx.getStatus());
//
//                int count = DCN.query_get_asset_count(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset_count()
//                ).count;
//                assertEquivalent(1, count);
//
//
//                DCN.GetAssetReturnValue asset = DCN.query_get_asset(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset(0)
//                );
//
//                assertEquivalent("1234", asset.symbol.trim());
//                assertEquivalent(1000L, asset.unit_scale);
//                assertEquivalent(bob.credentials().getAddress(), asset.contract_address);
//            });
//
//            it("should not be able to create asset with 0 unit scale", () -> {
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 0, bob.credentials().getAddress()));
//                assertEquivalent("0x0", tx.getStatus());
//
//                int count = DCN.query_get_asset_count(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset_count()
//                ).count;
//                assertEquivalent(1, count);
//            });
//
//            it("second asset should not effect first", () -> {
//                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("ABCD", 1001, creator.credentials().getAddress()));
//                assertEquivalent("0x1", tx.getStatus());
//
//                int count = DCN.query_get_asset_count(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset_count()
//                ).count;
//                assertEquivalent(2, count);
//
//
//                DCN.GetAssetReturnValue asset = DCN.query_get_asset(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset(0)
//                );
//
//                assertEquivalent("1234", asset.symbol.trim());
//                assertEquivalent(1000L, asset.unit_scale);
//                assertEquivalent(bob.credentials().getAddress(), asset.contract_address);
//
//                asset = DCN.query_get_asset(
//                        StaticNetwork.DCN(), StaticNetwork.Web3(),
//                        DCN.get_asset(1)
//                );
//
//                assertEquivalent("ABCD", asset.symbol.trim());
//                assertEquivalent(1001L, asset.unit_scale);
//                assertEquivalent(creator.credentials().getAddress(), asset.contract_address);
//            });
//        });
//    }
//}
