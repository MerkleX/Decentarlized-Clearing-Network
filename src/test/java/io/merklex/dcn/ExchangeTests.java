package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static io.merklex.dcn.utils.BetterAssert.assertEquals;

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
                TransactionReceipt receipt = bob.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("bobs network", bob.credentials().getAddress())
                );
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
                        DCN.add_exchange("boby network", bob.credentials().getAddress())
                );
                assertEquals("0x1", receipt.getStatus());

                assertEquals(1, receipt.getLogs().size());
                assertEquals("0x00000000", receipt.getLogs().get(0).getData());

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
                assertEquals("boby network", exchange.name);
            });

            it("second exchange should not effect first", () -> {
                TransactionReceipt receipt = creator.call(
                        StaticNetwork.DCN(),
                        DCN.add_exchange("hens network", henry.credentials().getAddress())
                );
                assertEquals("0x1", receipt.getStatus());

                assertEquals(1, receipt.getLogs().size());
                assertEquals("0x00000001", receipt.getLogs().get(0).getData());

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
                assertEquals("boby network", exchange.name);

                exchange = DCN.query_get_exchange(
                        StaticNetwork.DCN(), StaticNetwork.Web3(),
                        DCN.get_exchange(1)
                );

                assertEquals(0, exchange.fee_balance);
                assertEquals(henry.credentials().getAddress(), exchange.addr);
                assertEquals("hens network", exchange.name);
            });
        });
    }
}
