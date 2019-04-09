package io.merklex.dcn.utils;

import io.merklex.web3.RevertCodeExtractor;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class AssertHelpers {
    public static TransactionReceipt assertSuccess(EthSendTransaction tx) {
        try {
            if (tx.hasError()) {
                Response.Error error = tx.getError();

                String get;
                try {
                    get = RevertCodeExtractor.GetRevert(error);
                } catch (Exception e) {
                    throw new AssertionError(
                            error.getCode() + " : " + error.getMessage() + " : " + error.getData());
                }

                throw new AssertionError("Got revert: " + get);
            }
            TransactionReceipt result = Accounts.getTx(0).waitForResult(tx);
            assertEquals("0x1", result.getStatus());
            return result;
        } catch (IOException | TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRevert(EthSendTransaction tx) {
        try {
            assertTrue(tx.hasError());
            return RevertCodeExtractor.GetRevert(tx.getError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertRevert(String revertMessage, EthSendTransaction tx) {
        try {
            assertTrue(tx.hasError());
            assertEquals(revertMessage, RevertCodeExtractor.GetRevert(tx.getError()));
            assertEquals("0x0", Accounts.getTx(0).waitForResult(tx).getStatus());
        } catch (IOException | TransactionException e) {
            throw new RuntimeException(e);
        }
    }
}
