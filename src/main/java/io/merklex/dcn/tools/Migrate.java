package io.merklex.dcn.tools;

import io.merklex.dcn.contracts.DCN;
import io.merklex.web3.EtherTransactions;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class Migrate {
    public static void main(String[] args) throws IOException {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/9ca2b8297394405b8ff0043f633c1d84"));
        String privateKey = "";
        EtherTransactions creator = new EtherTransactions(web3, Credentials.create(privateKey));

        String oldContract = "";
        String newContract = "";

        EthSendTransaction tx;

        int assetCount = DCN.query_get_asset_count(oldContract, web3, DCN.get_asset_count()).count;
        for (int i = 0; i < assetCount; i++) {
            DCN.GetAssetReturnValue asset = DCN.query_get_asset(oldContract, web3, DCN.get_asset(i));
            tx = creator.sendCall(newContract, DCN.add_asset(
                    asset.symbol,
                    asset.unit_scale,
                    asset.contract_address
            ));
            System.out.println("Add asset: " + tx.getTransactionHash());
        }

        tx = creator.sendCall(newContract, DCN.add_exchange("merklex    ", creator.getAddress()));
        System.out.println("Add exchange: " + tx.getTransactionHash());
    }
}
