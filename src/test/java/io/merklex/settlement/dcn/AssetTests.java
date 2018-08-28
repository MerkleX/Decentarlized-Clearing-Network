package io.merklex.settlement.dcn;

import io.merklex.settlement.contracts.DCN;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;
import java.util.List;

public class AssetTests extends TestBase {
    @Test
    public void etherShouldAlreadyExist() throws Exception {
        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals("ETH ", asset.getValue1());
            Assert.assertEquals(BigInteger.valueOf(100000000), asset.getValue2());
            Assert.assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
        }

        Assert.assertEquals(BigInteger.ZERO, dcn.get_asset_count().send());
    }

    @Test
    public void futureAssetShouldBeEmpty() throws Exception {
        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(123)).send();
        {
            Assert.assertEquals("", asset.getValue1().trim());
            Assert.assertEquals(BigInteger.valueOf(0), asset.getValue2());
            Assert.assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
        }
    }

    @Test
    public void addAssetWithNonCreatorShouldFail() throws Exception {
        DCN bob = StaticNetwork.DCN("bob");
        TransactionReceipt result = bob.add_asset("TEST", BigInteger.ONE, bob.getContractAddress()).send();
        Assert.assertEquals(0, result.getLogs().size());

        Tuple3<String, BigInteger, String> asset = dcn.get_asset(BigInteger.valueOf(1)).send();
        {
            Assert.assertEquals("", asset.getValue1().trim());
            Assert.assertEquals(BigInteger.valueOf(0), asset.getValue2());
            Assert.assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
        }
    }

    @Test
    public void addAssets() throws Exception {
        TransactionReceipt receipt;
        Tuple3<String, BigInteger, String> asset;

        Assert.assertEquals(BigInteger.ZERO, dcn.get_asset_count().send());

        String addr = "0xca35b7d915458ef540ade6068dfe2f44e8fa733c";
        receipt = dcn.add_asset("ABCD", BigInteger.ONE, addr).send();
        {
            List<Log> logs = receipt.getLogs();
            Assert.assertEquals(1, logs.size());
            Log log = logs.get(0);
            Assert.assertEquals("0x0001", log.getData());
            Assert.assertEquals(BigInteger.ONE, dcn.get_asset_count().send());
        }

        asset = dcn.get_asset(BigInteger.valueOf(0)).send();
        {
            Assert.assertEquals("ETH ", asset.getValue1());
            Assert.assertEquals(BigInteger.valueOf(100000000), asset.getValue2());
            Assert.assertEquals("0x0000000000000000000000000000000000000000", asset.getValue3());
        }

        asset = dcn.get_asset(BigInteger.valueOf(1)).send();
        {
            Assert.assertEquals("ABCD", asset.getValue1());
            Assert.assertEquals(BigInteger.ONE, asset.getValue2());
            Assert.assertEquals(addr, asset.getValue3());
        }

        receipt = dcn.add_asset("ABC ", BigInteger.valueOf(231421), addr).send();
        {
            List<Log> logs = receipt.getLogs();
            Assert.assertEquals(1, logs.size());
            Log log = logs.get(0);
            Assert.assertEquals("0x0002", log.getData());
            Assert.assertEquals(BigInteger.valueOf(2), dcn.get_asset_count().send());
        }

        asset = dcn.get_asset(BigInteger.valueOf(1)).send();
        {
            Assert.assertEquals("ABCD", asset.getValue1());
            Assert.assertEquals(BigInteger.ONE, asset.getValue2());
            Assert.assertEquals(addr, asset.getValue3());
        }

        asset = dcn.get_asset(BigInteger.valueOf(2)).send();
        {
            Assert.assertEquals("ABC ", asset.getValue1());
            Assert.assertEquals(BigInteger.valueOf(231421), asset.getValue2());
            Assert.assertEquals(addr, asset.getValue3());
        }
    }
}
