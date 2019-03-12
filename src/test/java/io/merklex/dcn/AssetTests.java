package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static junit.framework.TestCase.assertEquals;

@RunWith(Spectrum.class)
public class AssetTests {
    {
        StaticNetwork.DescribeCheckpoint();

        describe("initial state checks", () -> {
            it("asset count should be zero", () -> {
                int count = DCN.query_get_asset_count(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset_count()
                ).count;
                assertEquals(0, count);
            });

            it("non allocated assets should be empty", () -> {
                for (int i = 0; i < 10; i++) {
                    DCN.GetAssetReturnValue asset = DCN.query_get_asset(
                            StaticNetwork.DCN(), StaticNetwork.Web3(),
                            DCN.get_asset(i)
                    );

                    assertEquals("", asset.symbol.trim());
                    assertEquals(0L, asset.unit_scale);
                    assertEquals("0x0000000000000000000000000000000000000000", asset.contract_address);
                    assertEquals(BigInteger.ZERO, asset.net_deposits);
                }
            });
        });

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions bob = Accounts.getTx(1);

        describe("add assets", () -> {
            it("non creator should not be able to add asset", () -> {
                TransactionReceipt tx = bob.call(StaticNetwork.DCN(), DCN.add_asset("ABCD", 100, bob.credentials().getAddress()));
                assertEquals("0x0", tx.getStatus());

                int count = DCN.query_get_asset_count(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset_count()
                ).count;
                assertEquals(0, count);
            });

            it("creator should be able to add asset", () -> {
                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 1000, bob.credentials().getAddress()));
                assertEquals("0x1", tx.getStatus());

                int count = DCN.query_get_asset_count(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset_count()
                ).count;
                assertEquals(1, count);


                DCN.GetAssetReturnValue asset = DCN.query_get_asset(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset(0)
                );

                assertEquals("1234", asset.symbol.trim());
                assertEquals(1000L, asset.unit_scale);
                assertEquals(bob.credentials().getAddress(), asset.contract_address);
                assertEquals(BigInteger.ZERO, asset.net_deposits);
            });

            it("should not be able to create asset with 0 unit scale", () -> {
                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 0, bob.credentials().getAddress()));
                assertEquals("0x0", tx.getStatus());

                int count = DCN.query_get_asset_count(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset_count()
                ).count;
                assertEquals(1, count);
            });

            it("second asset should not effect first", () -> {
                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("ABCD", 1001, creator.credentials().getAddress()));
                assertEquals("0x1", tx.getStatus());

                int count = DCN.query_get_asset_count(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset_count()
                ).count;
                assertEquals(2, count);


                DCN.GetAssetReturnValue asset = DCN.query_get_asset(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset(0)
                );

                assertEquals("1234", asset.symbol.trim());
                assertEquals(1000L, asset.unit_scale);
                assertEquals(bob.credentials().getAddress(), asset.contract_address);

                asset = DCN.query_get_asset(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_asset(1)
                );

                assertEquals("ABCD", asset.symbol.trim());
                assertEquals(1001L, asset.unit_scale);
                assertEquals(creator.credentials().getAddress(), asset.contract_address);
            });
        });
    }
}
