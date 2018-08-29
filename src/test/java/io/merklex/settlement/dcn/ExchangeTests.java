package io.merklex.settlement.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.settlement.contracts.DCN;
import io.merklex.settlement.utils.Box;
import io.merklex.settlement.utils.Genesis;
import org.junit.runner.RunWith;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static io.merklex.settlement.utils.BetterAssert.assertEquals;

@RunWith(Spectrum.class)
public class ExchangeTests {
    {
        StaticNetwork.DescribeCheckpoint();

        it("initial exchange count should be zero", () -> {
            assertEquals(0, StaticNetwork.DCN().get_exchange_count().send());
        });

        DCN bob = StaticNetwork.DCN("bob");
        Credentials bobKey = Genesis.GetKey("bob");
        Credentials henryKey = Genesis.GetKey("henry");

        describe("non creator should not be able to add an exchange", () -> {
            Box<TransactionReceipt> receipt = new Box<>();
            it("attempt to add exchange", () -> {
                receipt.value = bob.add_exchange("bobs network", bobKey.getAddress()).send();
            });

            it("should have no logs", () -> {
                assertEquals(0, receipt.value.getLogs().size());
            });

            it("exchange count should still be zero", () -> {
                assertEquals(0, StaticNetwork.DCN().get_exchange_count().send());
            });

            it("exchange data should be empty", () -> {
                Tuple3<String, String, BigInteger> exchangeData = bob.get_exchange(BigInteger.valueOf(0)).send();
                assertEquals("", exchangeData.getValue1().trim());
                assertEquals("0x0000000000000000000000000000000000000000", exchangeData.getValue2());
                assertEquals(0, exchangeData.getValue3());
            });
        });

        describe("creator should be able to add an exchange", () -> {
            Box<TransactionReceipt> receipt = new Box<>();
            it("add exchange", () -> {
                receipt.value = StaticNetwork.DCN().add_exchange("bobs network", bobKey.getAddress()).send();
            });

            it("should have log with new id", () -> {
                assertEquals(1, receipt.value.getLogs().size());
                assertEquals("0x00000000", receipt.value.getLogs().get(0).getData());
            });

            it("exchange count should be one", () -> {
                assertEquals(1, StaticNetwork.DCN().get_exchange_count().send());
            });

            it("should be able to query exchange", () -> {
                Tuple3<String, String, BigInteger> exchangeData = bob.get_exchange(BigInteger.valueOf(0)).send();
                assertEquals("bobs network", exchangeData.getValue1().trim());
                assertEquals(bobKey.getAddress(), exchangeData.getValue2());
                assertEquals(0, exchangeData.getValue3());
            });
        });

        describe("second exchange should not affect first", () -> {
            Box<TransactionReceipt> receipt = new Box<>();
            it("add exchange", () -> {
                receipt.value = StaticNetwork.DCN().add_exchange("other net yo", henryKey.getAddress()).send();
            });

            it("should have log with new id", () -> {
                assertEquals(1, receipt.value.getLogs().size());
                assertEquals("0x00000001", receipt.value.getLogs().get(0).getData());
            });

            it("exchange count should be two", () -> {
                assertEquals(2, StaticNetwork.DCN().get_exchange_count().send());
            });

            it("should be able to query exchange first exchange", () -> {
                Tuple3<String, String, BigInteger> exchangeData = bob.get_exchange(BigInteger.valueOf(0)).send();
                assertEquals("bobs network", exchangeData.getValue1().trim());
                assertEquals(bobKey.getAddress(), exchangeData.getValue2());
                assertEquals(0, exchangeData.getValue3());
            });

            it("should be able to query exchange new exchange", () -> {
                Tuple3<String, String, BigInteger> exchangeData = bob.get_exchange(BigInteger.valueOf(1)).send();
                assertEquals("other net yo", exchangeData.getValue1().trim());
                assertEquals(henryKey.getAddress(), exchangeData.getValue2());
                assertEquals(0, exchangeData.getValue3());
            });
        });
    }
}
