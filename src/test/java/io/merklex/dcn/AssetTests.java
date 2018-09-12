package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;

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
                Tuple3<String, BigInteger, String> asset = StaticNetwork.DCN().get_asset(BigInteger.valueOf(0)).send();
                assertEquals("ETH ", asset.getValue1());
                assertEquals(BigInteger.valueOf(100000000), asset.getValue2());
                assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());

                assertEquals(BigInteger.ZERO, StaticNetwork.DCN().get_asset_count().send());
            });

            it("non allocated assets should be empty", () -> {
                Tuple3<String, BigInteger, String> asset = StaticNetwork.DCN().get_asset(BigInteger.valueOf(123)).send();
                assertEquals("", asset.getValue1().trim());
                assertEquals(BigInteger.valueOf(0), asset.getValue2());
                assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
            });
        });

        describe("add assets", () -> {
            describe("only creator should be able to add assets", () -> {
                DCN bob = StaticNetwork.DCN("bob");

                it("adding asset should have no logs", () -> {
                    TransactionReceipt result = bob.add_asset("TEST", BigInteger.ONE, bob.getContractAddress()).send();
                    assertEquals(0, result.getLogs().size());
                });

                it("query should yield no results", () -> {
                    Tuple3<String, BigInteger, String> asset = bob.get_asset(BigInteger.valueOf(1)).send();
                    assertEquals("", asset.getValue1().trim());
                    assertEquals(BigInteger.valueOf(0), asset.getValue2());
                    assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
                });
            });

            describe("add assets", () -> {
                StaticNetwork.DescribeCheckpoint();

                DCN dcn = StaticNetwork.DCN();
                String addr = "0xca35b7d915458ef540ade6068dfe2f44e8fa733c";

                describe("add first asset", () -> {
                    Box<TransactionReceipt> receipt = new Box<>();

                    it("add asset", () -> {
                        receipt.value = dcn.add_asset("ABCD", BigInteger.ONE, addr).send();
                    });

                    it("should have log as first asset", () -> {
                        List<Log> logs = receipt.value.getLogs();
                        assertEquals(1, logs.size());
                        Log log = logs.get(0);
                        assertEquals("0x0001", log.getData());
                        assertEquals(BigInteger.ONE, dcn.get_asset_count().send());
                    });

                    it("should not modify ether asset", () -> {
                        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(0)).send();
                        assertEquals("ETH ", asset.getValue1());
                        assertEquals(BigInteger.valueOf(100000000), asset.getValue2());
                        assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
                    });

                    it("should be able to query asset", () -> {
                        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(1)).send();
                        assertEquals("ABCD", asset.getValue1());
                        assertEquals(BigInteger.ONE, asset.getValue2());
                        assertEquals(addr, asset.getValue3());
                    });
                });

                describe("add second asset", () -> {
                    Box<TransactionReceipt> receipt = new Box<>();

                    it("add asset", () -> {
                        receipt.value = dcn.add_asset("ABC ", BigInteger.valueOf(231421), addr).send();
                        ;
                    });

                    it("should have log as second asset", () -> {
                        List<Log> logs = receipt.value.getLogs();
                        assertEquals(1, logs.size());
                        Log log = logs.get(0);
                        assertEquals("0x0002", log.getData());
                        assertEquals(BigInteger.valueOf(2), dcn.get_asset_count().send());
                    });

                    it("should not modify ether", () -> {
                        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(0)).send();
                        assertEquals("ETH ", asset.getValue1());
                        assertEquals(BigInteger.valueOf(100000000), asset.getValue2());
                        assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
                    });

                    it("should not modify asset 1", () -> {
                        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(1)).send();
                        assertEquals("ABCD", asset.getValue1());
                        assertEquals(BigInteger.ONE, asset.getValue2());
                        assertEquals(addr, asset.getValue3());
                    });

                    it("should be able to query asset 2", () -> {
                        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(2)).send();
                        assertEquals("ABC ", asset.getValue1());
                        assertEquals(BigInteger.valueOf(231421), asset.getValue2());
                        assertEquals(addr, asset.getValue3());
                    });
                });
            });
        });
    }
}
