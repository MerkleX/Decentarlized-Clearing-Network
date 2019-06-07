package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.Box;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.runner.RunWith;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;
import static io.merklex.dcn.utils.AssertHelpers.assertRevert;
import static io.merklex.dcn.utils.AssertHelpers.assertSuccess;

@RunWith(Spectrum.class)
public class ExchangeTransferFromTests {
    {
        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions bob = Accounts.getTx(13);
        Box<String> token = new Box<>();

        long unitScale = 10000000000L;

        BigInteger totalSupply = BigInteger.valueOf(100000000_0000000000L);

        int bobDeposit = 10;
        long exchangeDepositAmount = 2000;

        final int assetId = 0;
        final int exchangeId = 0;

        beforeAll(() -> {
            token.value = bob.deployContract(
                    BigInteger.ZERO,
                    StaticNetwork.GAS_LIMIT,
                    ERC20.DeployData(
                            totalSupply,
                            "Token 1",
                            18,
                            "TK1"
                    ),
                    BigInteger.ZERO
            );

            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_asset("TK1 1234", unitScale, token.value)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_create()));
            assertSuccess(bob.sendCall(token.value,
                    ERC20.approve(StaticNetwork.DCN(), totalSupply)));
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.add_exchange("12345678901", creator.getAddress())));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_deposit(exchangeId, assetId, exchangeDepositAmount)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(0, 0, 0, bobDeposit)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(1, 0, 0, bobDeposit)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(2, 0, 0, bobDeposit)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(3, 0, 0, bobDeposit)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(4, 0, 0, bobDeposit)));
            assertSuccess(bob.sendCall(StaticNetwork.DCN(),
                    DCN.user_deposit_to_session(5, 0, 0, bobDeposit)));
        });

        it("Should be able to withdraw multiple with overdraft", () -> {
            byte[] bytes = new byte[10000];
            UnsafeBuffer buffer = new UnsafeBuffer(bytes);

            Transfers transfers = new Transfers().wrap(buffer, 0);
            Transfers.Group group = new Transfers.Group();
            Transfers.Transfer transfer = new Transfers.Transfer();

            transfers.exchangeId(exchangeId)
                    .firstGroup(group)
                    .transferCount(3)
                    .assetId(assetId)
                    .allowOverdraft(true)

                    .firstTransfer(transfer)
                    .userId(0)
                    .quantity(bobDeposit)

                    .nextTransfer(transfer)
                    .userId(1)
                    .quantity(bobDeposit + 2)

                    .nextTransfer(transfer)
                    .userId(2)
                    .quantity(bobDeposit * 2);

            group.nextGroup(group)
                    .transferCount(2)
                    .assetId(assetId)
                    .allowOverdraft(false)

                    .firstTransfer(transfer)
                    .userId(3)
                    .quantity(bobDeposit / 2)

                    .nextTransfer(transfer)
                    .userId(4)
                    .quantity(bobDeposit + 1);

            String payload = Numeric.toHexString(bytes, 0, transfers.bytes(2, group), true);
            assertRevert("0x08", creator.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_transfer_from(payload)));

            transfer.quantity(bobDeposit);
            payload = Numeric.toHexString(bytes, 0, transfers.bytes(2, group), true);
            assertSuccess(creator.sendCall(StaticNetwork.DCN(),
                    DCN.exchange_transfer_from(payload)));
        });
    }
}
