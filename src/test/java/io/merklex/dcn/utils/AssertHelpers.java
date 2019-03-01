package io.merklex.dcn.utils;

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
                throw new AssertionError("Got revert: " + RevertCodeExtractor.Get(tx.getError()));
            }
            TransactionReceipt result = Accounts.getTx(0).waitForResult(tx);
            assertEquals("0x1", result.getStatus());
            return result;
        } catch (IOException | TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertRevert(String revertMessage, EthSendTransaction tx) {
        try {
            assertTrue(tx.hasError());
            assertEquals(revertMessage, RevertCodeExtractor.Get(tx.getError()));
            assertEquals("0x0", Accounts.getTx(0).waitForResult(tx).getStatus());
        } catch (IOException | TransactionException e) {
            throw new RuntimeException(e);
        }
    }
}
