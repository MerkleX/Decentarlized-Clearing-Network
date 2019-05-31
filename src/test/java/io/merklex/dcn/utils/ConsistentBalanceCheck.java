package io.merklex.dcn.utils;

import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.contracts.ERC20;
import io.merklex.web3.QueryHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ConsistentBalanceCheck {
    private static class Asset {
        String address;
        BigInteger unitScale = BigInteger.ONE;
        BigInteger contractBalance = BigInteger.ZERO;
        BigInteger userBalances = BigInteger.ZERO;
        BigInteger exchangeBalances = BigInteger.ZERO;
        BigInteger sessionBalances = BigInteger.ZERO;
    }

    public static void assertCorrectBalances(QueryHelper dcnQuery) throws IOException {
        int assetCount = dcnQuery.query(DCN::query_get_asset_count, DCN.get_asset_count()).count;
        int exchangeCount = dcnQuery.query(DCN::query_get_exchange_count, DCN.get_exchange_count()).count;
        int userCount = dcnQuery.query(DCN::query_get_user_count, DCN.get_user_count()).count;

        Asset[] assets = new Asset[assetCount];
        for (int assetId = 0; assetId < assetCount; assetId++) {
            Asset asset = assets[assetId] = new Asset();

            DCN.GetAssetReturnValue assetData = dcnQuery.query(DCN::query_get_asset, DCN.get_asset(assetId));
            asset.address = assetData.contract_address.toLowerCase();
            asset.unitScale = assetData.unit_scale;

            ERC20.BalanceofReturnValue balance = ERC20.query_balanceOf(assetData.contract_address, dcnQuery.web3j,
                    ERC20.balanceOf(dcnQuery.contractAddress));
            asset.contractBalance = balance.balance;

            for (int userId = 0; userId < userCount; userId++) {
                DCN.GetBalanceReturnValue userBalance = dcnQuery.query(DCN::query_get_balance,
                        DCN.get_balance(userId, assetId));
                asset.userBalances = asset.userBalances.add(userBalance.return_balance);
            }

            for (int exchangeId = 0; exchangeId < exchangeCount; exchangeId++) {
                DCN.GetExchangeBalanceReturnValue exchangeBalance = dcnQuery.query(DCN::query_get_exchange_balance,
                        DCN.get_exchange_balance(exchangeId, assetId));
                asset.exchangeBalances = asset.exchangeBalances.add(exchangeBalance.exchange_balance);

                for (int userId = 0; userId < userCount; userId++) {
                    DCN.GetSessionBalanceReturnValue sessionBalance = dcnQuery.query(DCN::query_get_session_balance,
                            DCN.get_session_balance(userId, exchangeId, assetId));
                    asset.sessionBalances = asset.sessionBalances.add(BigInteger.valueOf(sessionBalance.asset_balance));
                }
            }
        }

        HashMap<String, Asset> contractAssets = new HashMap<>();

        for (Asset asset : assets) {
            Asset existing = contractAssets.get(asset.address);
            if (existing == null) {
                contractAssets.put(asset.address, asset);
                continue;
            }

            assertEquals(existing.contractBalance, asset.contractBalance);
            existing.sessionBalances = existing.sessionBalances.add(asset.sessionBalances);
            existing.userBalances = existing.userBalances.add(asset.userBalances);
            existing.exchangeBalances = existing.exchangeBalances.add(asset.exchangeBalances);
        }

        for (Asset asset : contractAssets.values()) {
            BigInteger actual = asset.userBalances
                    .add(asset.exchangeBalances.multiply(asset.unitScale))
                    .add(asset.sessionBalances.multiply(asset.unitScale));

            assertEquals(asset.contractBalance, actual);
        }
    }
}
