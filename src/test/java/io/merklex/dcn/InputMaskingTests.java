package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.junit.runner.RunWith;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Spectrum.class)
public class InputMaskingTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchange = Accounts.getTx(1);

        it("add exchange", () -> {
            Function function = DCN.add_exchange("12345678901", exchange.getAddress());
            String actual = FunctionEncoder.encode(function);
            String expected = "0x136a9bf7" +
                    /* offset for string */
                    "0000000000000000000000000000000000000000000000000000000000000040" +
                    /* address */
                    "000000000000000000000000646d7e260269eddac2ad184d48221b39520177e4" +
                    /* string length */
                    "000000000000000000000000000000000000000000000000000000000000000b" +
                    /* string data */
                    "3132333435363738393031000000000000000000000000000000000000000000";

            assertEquals(expected, actual);

            String manipulatedSymbol = "0x136a9bf7" +
                    /* offset for string */
                    "0000000000000000000000000000000000000000000000000000000000000040" +
                    /* address */
                    "000000000000000000000000646d7e260269eddac2ad184d48221b39520177e4" +
                    /* string length */
                    "000000000000000000000000000000000000000000000000000000000000000b" +
                    /* string data */
                    "3132333435363738393031ffffffffffffffffffffffffffffffffffffffffff";

            assertSuccess(creator.sendCall(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, StaticNetwork.DCN(),
                    manipulatedSymbol, BigInteger.ZERO));

            DCN.GetExchangeReturnValue data = DCN.query_get_exchange(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_exchange(0));

            assertEquals("12345678901", data.name);
            assertEquals(exchange.getAddress(), data.owner);
            assertEquals(exchange.getAddress(), data.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", data.recovery_address_proposed);
            assertFalse(data.locked);

            String manipulatedAddress = "0x136a9bf7" +
                    /* offset for string */
                    "0000000000000000000000000000000000000000000000000000000000000040" +
                    /* address */
                    "ffffffffffffffffffffffff646d7e260269eddac2ad184d48221b39520177e4" +
                    /* string length */
                    "000000000000000000000000000000000000000000000000000000000000000b" +
                    /* string data */
                    "3132333435363738393031ffffffffffffffffffffffffffffffffffffffffff";

            assertSuccess(creator.sendCall(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, StaticNetwork.DCN(),
                    manipulatedAddress, BigInteger.ZERO));

            data = DCN.query_get_exchange(StaticNetwork.DCN(), creator.getWeb3(),
                    DCN.get_exchange(1));

            assertEquals("12345678901", data.name);
            assertEquals(exchange.getAddress(), data.owner);
            assertEquals(exchange.getAddress(), data.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", data.recovery_address_proposed);
            assertFalse(data.locked);

            String manipulatedOffset = "0x136a9bf7" +
                    /* offset for string */
                    "0000000000000000000000000000000000000000000000000000000000000020" +
                    /* address */
                    "000000000000000000000000646d7e260269eddac2ad184d48221b39520177e4" +
                    /* string length */
                    "000000000000000000000000000000000000000000000000000000000000000b" +
                    /* string data */
                    "3132333435363738393031ffffffffffffffffffffffffffffffffffffffffff";

            assertRevert("0x", creator.sendCall(BigInteger.ZERO, StaticNetwork.GAS_LIMIT, StaticNetwork.DCN(),
                    manipulatedOffset, BigInteger.ZERO));
        });
    }
}
