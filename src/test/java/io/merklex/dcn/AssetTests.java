package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.dnc.DCNResults;
import io.merklex.dnc.models.GetAssetResult;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static io.merklex.dcn.utils.BetterAssert.assertEquals;

@RunWith(Spectrum.class)
public class AssetTests {
    {
        StaticNetwork.DescribeCheckpoint();

        describe("initial state checks", () -> {
            it("asset count should be zero", () -> {
                assertEquals(0, StaticNetwork.DCN().get_asset_count().send());
            });

            it("ether should exist with asset_id=0", () -> {
                GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), StaticNetwork.DCN().get_asset(BigInteger.valueOf(0)).send());
                assertEquals("ETH ", asset.symbol);
                assertEquals(10000000000L, asset.unitScale);
                assertEquals("0x0000000000000000000000000000000000000000", asset.contractAddress);

                assertEquals(BigInteger.ZERO, StaticNetwork.DCN().get_asset_count().send());
            });

            it("non allocated assets should be empty", () -> {
                GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), StaticNetwork.DCN().get_asset(BigInteger.valueOf(123)).send());
                assertEquals("", asset.symbol.trim());
                assertEquals(0, asset.unitScale);
                assertEquals("0x0000000000000000000000000000000000000000", asset.contractAddress);
            });
        });

        describe("add assets", () -> {
            StaticNetwork.DescribeCheckpoint();

            describe("only creator should be able to add assets", () -> {
                DCN bob = StaticNetwork.DCN("bob");

                it("failed add should have no logs", () -> {
                    TransactionReceipt result = bob.add_asset("TEST", BigInteger.ONE, bob.getContractAddress()).send();
                    assertEquals(0, result.getLogs().size());
                });

                it("query should yield no results", () -> {
                    GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), bob.get_asset(BigInteger.valueOf(1)).send());
                    assertEquals("", asset.symbol.trim());
                    assertEquals(0, asset.unitScale);
                    assertEquals("0x0000000000000000000000000000000000000000", asset.contractAddress);
                });
            });


            DCN dcn = StaticNetwork.DCN();
            String assetAddress = "0xca35b7d915458ef540ade6068dfe2f44e8fa733c";

            describe("should validate add asset", () -> {
                it("should not be able to add asset with < 4 character symbol", () -> {
                    dcn.add_asset("TES", BigInteger.ONE, assetAddress).send();
                    assertEquals(0, dcn.get_asset_count().send());
                });

                it("should not be able to add asset with > 4 character symbol", () -> {
                    dcn.add_asset("TESTER", BigInteger.ONE, assetAddress).send();
                    assertEquals(0, dcn.get_asset_count().send());
                });

                it("should not be able to add asset with zero unit scale", () -> {
                    dcn.add_asset("1234", BigInteger.ZERO, assetAddress).send();
                    assertEquals(0, dcn.get_asset_count().send());
                });
            });

            describe("add first asset", () -> {
                Box<TransactionReceipt> receipt = new Box<>();

                it("add asset", () -> {
                    receipt.value = dcn.add_asset("ABCD", BigInteger.ONE, assetAddress).send();
                });

                it("should have log as first asset", () -> {
                    List<Log> logs = receipt.value.getLogs();
                    assertEquals(1, logs.size());
                    Log log = logs.get(0);
                    assertEquals("0x0001", log.getData());
                    assertEquals(BigInteger.ONE, dcn.get_asset_count().send());
                });

                it("should not modify ether asset", () -> {
                    GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), dcn.get_asset(BigInteger.valueOf(0)).send());
                    assertEquals("ETH ", asset.symbol);
                    assertEquals(10000000000L, asset.unitScale);
                    assertEquals("0x0000000000000000000000000000000000000000", asset.contractAddress);
                });

                it("should be able to query asset", () -> {
                    GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), dcn.get_asset(BigInteger.valueOf(1)).send());
                    assertEquals("ABCD", asset.symbol);
                    assertEquals(BigInteger.ONE, asset.unitScale);
                    assertEquals(assetAddress, asset.contractAddress);
                });
            });

            describe("add second asset", () -> {
                Box<TransactionReceipt> receipt = new Box<>();

                it("add asset", () -> {
                    receipt.value = dcn.add_asset("ABC ", BigInteger.valueOf(231421), assetAddress).send();
                    ;
                });

                it("should have log as second asset", () -> {
                    List<Log> logs = receipt.value.getLogs();
                    assertEquals(1, logs.size());
                    Log log = logs.get(0);
                    assertEquals("0x0002", log.getData());
                    assertEquals(2, dcn.get_asset_count().send());
                });

                it("should not modify ether", () -> {
                    GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), dcn.get_asset(BigInteger.valueOf(0)).send());
                    assertEquals("ETH ", asset.symbol);
                    assertEquals(10000000000L, asset.unitScale);
                    assertEquals("0x0000000000000000000000000000000000000000", asset.contractAddress);
                });

                it("should not modify asset 1", () -> {
                    GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), dcn.get_asset(BigInteger.valueOf(1)).send());
                    assertEquals("ABCD", asset.symbol);
                    assertEquals(BigInteger.ONE, asset.unitScale);
                    assertEquals(assetAddress, asset.contractAddress);
                });

                it("should be able to query asset 2", () -> {
                    GetAssetResult asset = DCNResults.GetAsset(new GetAssetResult(), dcn.get_asset(BigInteger.valueOf(2)).send());
                    assertEquals("ABC ", asset.symbol);
                    assertEquals(231421, asset.unitScale);
                    assertEquals(assetAddress, asset.contractAddress);
                });
            });
        });
    }
}
