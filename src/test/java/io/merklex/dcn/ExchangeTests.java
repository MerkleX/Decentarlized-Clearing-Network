package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.RevertCodeExtractor;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExchangeTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions bob = Accounts.getTx(1);
        EtherTransactions henry = Accounts.getTx(2);

        describe("add exchange", () -> {
            it("initial exchange count should be zero", () -> {
                int count = DCN.query_get_exchange_count(
                        StaticNetwork.DCN(),
                        StaticNetwork.Web3(),
                        DCN.get_exchange_count()
                ).count;

                assertEquals(0, count);
            });

            it("non creator should fail to add exchange", () -> {
                EthSendTransaction tx = bob.sendCall(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("bobs network", 0, bob.credentials().getAddress())
                );
                Assert.assertTrue(tx.hasError());
                Assert.assertEquals("0x01", RevertCodeExtractor.Get(tx.getError()));

                TransactionReceipt receipt = bob.waitForResult(tx);
                assertEquals("0x0", receipt.getStatus());

                int count = DCN.query_get_exchange_count(
                        StaticNetwork.DCN(),
                        StaticNetwork.Web3(),
                        DCN.get_exchange_count()
                ).count;

                assertEquals(0, count);
            });

            it("creator should be able to create exchange", () -> {
                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("merklex ", 0, bob.credentials().getAddress())
                );
                assertEquals("0x1", receipt.getStatus());

                int count = DCN.query_get_exchange_count(
                        StaticNetwork.DCN(),
                        StaticNetwork.Web3(),
                        DCN.get_exchange_count()
                ).count;

                assertEquals(1, count);

                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_exchange(0)
                );

                assertEquals(0, exchange.fee_balance);
                assertEquals(bob.credentials().getAddress(), exchange.addr);
                assertEquals("merklex ", exchange.name);
            });

            it("should not be able to create exchange with 5 char name", () -> {
                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("12345", 0, bob.credentials().getAddress())
                );
                assertEquals("0x0", receipt.getStatus());
            });

            it("should not be able to create exchange with 15 char name", () -> {
                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("boby network :)", 0, bob.credentials().getAddress())
                );
                assertEquals("0x0", receipt.getStatus());
            });

            it("second exchange should not effect first", () -> {
                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("12345678", 0, henry.credentials().getAddress())
                );
                assertEquals("0x1", receipt.getStatus());

                int count = DCN.query_get_exchange_count(
                        StaticNetwork.DCN(),
                        StaticNetwork.Web3(),
                        DCN.get_exchange_count()
                ).count;

                assertEquals(2, count);

                DCN.GetExchangeReturnValue exchange = DCN.query_get_exchange(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_exchange(0)
                );

                assertEquals(0, exchange.fee_balance);
                assertEquals(bob.credentials().getAddress(), exchange.addr);
                assertEquals("merklex ", exchange.name);

                exchange = DCN.query_get_exchange(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_exchange(1)
                );

                assertEquals(0, exchange.fee_balance);
                assertEquals(henry.credentials().getAddress(), exchange.addr);
                assertEquals("12345678", exchange.name);
            });

            it("should not be able to add exchange with invalid quote_asset", () -> {
                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("12 45 78", 1, bob.credentials().getAddress())
                );
                assertEquals("0x0", receipt.getStatus());
            });

            it("should be able to add exchange with non eth asset", () -> {
                TransactionReceipt tx = creator.call(StaticNetwork.DCN(), DCN.add_asset("1234", 100, "0x0"));
                assertEquals("0x1", tx.getStatus());

                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("12 45 78", 1, bob.credentials().getAddress())
                );
                assertEquals("0x1", receipt.getStatus());
            });
        });
    }
}
