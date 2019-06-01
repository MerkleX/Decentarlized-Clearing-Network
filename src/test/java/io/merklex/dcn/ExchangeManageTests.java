package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExchangeManageTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchangeOwner = Accounts.getTx(3);
        EtherTransactions updatedExchangeOwner = Accounts.getTx(4);
        EtherTransactions updatedExchangeRecover = Accounts.getTx(5);
        EtherTransactions notExchange = Accounts.getTx(6);
        EtherTransactions withdraw = Accounts.getTx(7);

        QueryHelper query = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        it("only creator should be able to add exchange", () -> {
            assertRevert("0x01", exchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("12345678901", exchangeOwner.getAddress())));

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("12345678901", exchangeOwner.getAddress())));

            DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange,
                    DCN.get_exchange(0));

            assertEquals("12345678901", exchange.name);
            assertEquals(exchangeOwner.getAddress(), exchange.owner);
            assertEquals(exchangeOwner.getAddress(), exchange.withdraw_address);
            assertEquals(exchangeOwner.getAddress(), exchange.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", exchange.recovery_address_proposed);
        });

        it("should be able to update owner", () -> {
            assertRevert("0x01", notExchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_owner(0, updatedExchangeOwner.getAddress())));

            assertSuccess(exchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_owner(0, updatedExchangeOwner.getAddress())));

            DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange,
                    DCN.get_exchange(0));

            assertEquals(updatedExchangeOwner.getAddress(), exchange.owner);
            assertEquals(exchangeOwner.getAddress(), exchange.withdraw_address);
            assertEquals(exchangeOwner.getAddress(), exchange.recovery_address);
            assertEquals("0x0000000000000000000000000000000000000000", exchange.recovery_address_proposed);
        });

        it("should be able to propose recovery", () -> {
            assertRevert("0x01", notExchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_propose_recovery(0, updatedExchangeRecover.getAddress())));

            assertSuccess(exchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_propose_recovery(0, updatedExchangeRecover.getAddress())));

            DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange,
                    DCN.get_exchange(0));

            assertEquals(updatedExchangeOwner.getAddress(), exchange.owner);
            assertEquals(exchangeOwner.getAddress(), exchange.recovery_address);
            assertEquals(updatedExchangeRecover.getAddress(), exchange.recovery_address_proposed);
        });

        it("should be able to set recovery", () -> {
            assertRevert("0x01", exchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_recovery(0)));

            assertSuccess(updatedExchangeRecover.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_recovery(0)));

            DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange,
                    DCN.get_exchange(0));

            assertEquals(updatedExchangeOwner.getAddress(), exchange.owner);
            assertEquals(updatedExchangeRecover.getAddress(), exchange.recovery_address);
            assertEquals(updatedExchangeRecover.getAddress(), exchange.recovery_address_proposed);
        });

        it("recovery address should set owner", () -> {
            assertRevert("0x01", updatedExchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_owner(0, exchangeOwner.getAddress())));

            assertSuccess(updatedExchangeRecover.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_owner(0, exchangeOwner.getAddress())));

            DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange,
                    DCN.get_exchange(0));

            assertEquals(exchangeOwner.getAddress(), exchange.owner);
            assertEquals(updatedExchangeRecover.getAddress(), exchange.recovery_address);
            assertEquals(updatedExchangeRecover.getAddress(), exchange.recovery_address_proposed);
        });

        it("recovery address should set withdraw", () -> {
            assertRevert("0x01", notExchange.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_withdraw(0, withdraw.getAddress())));

            assertRevert("0x01", exchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_withdraw(0, withdraw.getAddress())));

            assertRevert("0x01", updatedExchangeOwner.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_withdraw(0, withdraw.getAddress())));

            assertSuccess(updatedExchangeRecover.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_set_withdraw(0, withdraw.getAddress())));

            DCN.GetExchangeReturnValue exchange = query.query(DCN::query_get_exchange,
                    DCN.get_exchange(0));

            assertEquals(exchangeOwner.getAddress(), exchange.owner);
            assertEquals(withdraw.getAddress(), exchange.withdraw_address);
        });
    }
}
