package io.merklex.dcn;

import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Accounts;
import io.merklex.dcn.utils.RevertCodeExtractor;
import io.merklex.dcn.utils.StaticNetwork;
import io.merklex.web3.EtherTransactions;
import io.merklex.web3.QueryHelper;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.nio.ByteBuffer;

import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
public class UpdateLimitTests {
    {

        StaticNetwork.DescribeCheckpoint();

        EtherTransactions creator = Accounts.getTx(0);
        EtherTransactions exchange = Accounts.getTx(1);

        beforeAll(() -> {
            creator.call(StaticNetwork.DCN(), DCN.add_exchange("testexchange", exchange.getAddress()));
        });

        QueryHelper helper = new QueryHelper(StaticNetwork.DCN(), StaticNetwork.Web3());

        it("should be able to update limit", () -> {
            byte[] bytes = new byte[UpdateLimit.BYTES];
            UnsafeBuffer buffer = new UnsafeBuffer(bytes);
            UpdateLimit update = new UpdateLimit().wrap(buffer, 0);

            String userAddress = "0x261514fb9e305df0a965a0963e7a190aa7ae8f22";
            update.user(userAddress);
            update.signature("0x8239f33ff9a872a74486ddda9763a0e7411370cf00e64c654625e5d997afa6ac049568db93152884dc06cb3dc2" +
                    "c1ef36d944e1df16395486423c674ceb48e0361b");
            update.exchangeId(0);

            update.version(1);
            update.assetId(2);
            update.maxLongPrice(10000000);
            update.minShortPrice(0);
            update.minEtherQty(-1000000);
            update.minAssetQty(0);
            update.etherShift(0);
            update.assetShift(0);

            String payload = Numeric.toHexString(bytes);
            System.out.println(payload);
            EthSendTransaction ethSendTransaction = exchange.sendCall(StaticNetwork.DCN(), DCN.set_limit(payload));


            if (ethSendTransaction.hasError()) {
                System.out.println(RevertCodeExtractor.Get(ethSendTransaction.getError()));
            }

            /*
            0x261514fb9e305df0a965a0963e7a190aa7ae8f22
                00000000 02000000020000000000000080969800000000000000000000000000c0bdf0ffffffffff0000000000000000000000000000000000000000000000008239f33ff9a872a74486ddda9763a0e7411370cf00e64c654625e5d997afa6ac049568db93152884dc06cb3dc2c1ef36d944e1df16395486423c674ceb48e0361b
            0x0000000002000000020000000000000080969800000000000000000000000000
             */

            TransactionReceipt call = exchange.waitForResult(ethSendTransaction);
            Assert.assertEquals("0x1", call.getStatus());

            DCN.GetSessionLimitReturnValue limit = helper.query(DCN::query_get_session_limit, DCN.get_session_limit(userAddress, 0, 2));
            System.out.println(limit);
        });
    }
}
