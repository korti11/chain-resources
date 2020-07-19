package io.korti.chainresources.api;

import io.korti.chainresources.TestUtil;
import io.korti.chainresources.api.blockchain.ITransactionOutput;
import io.korti.chainresources.api.impl.TransactionOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionOutputTest {

    private static PublicKey walletKey;

    @BeforeAll
    public static void init() {
        TransactionOutputTest.walletKey = TestUtil.generateKeyPair().getPublic();
    }

    @Test
    @DisplayName("The UTXO id gets set in the constructor.")
    public void idGetsSetOnConstruction() {
        final ITransactionOutput utxo = new TransactionOutput(walletKey, 0, "ABC");

        assertNotEquals("", utxo.getID(), "The utxo id should not be empty.");
    }

    @Test
    @DisplayName("The UTXO value gets set in the constructor and readable.")
    public void valueGetsSetOnConstructionAndRead() {
        final ITransactionOutput utxo = new TransactionOutput(walletKey, 4.34f, "");

        assertEquals(4.34f, utxo.getValue(), "The UTXO value is not equal.");
    }

    @Test
    @DisplayName("Check that the UTXO is mine.")
    public void checkIfItIsMine() {
        final ITransactionOutput utxo = new TransactionOutput(walletKey, 0, "");

        assertTrue(utxo.isMine(walletKey), "This UTXO should be mine.");
    }

    @Test
    @DisplayName("Check that the UTXO is not mine.")
    public void checkItIsNotMine() {
        final ITransactionOutput utxo = new TransactionOutput(walletKey, 0, "");
        final PublicKey secondWalletKey = TestUtil.generateKeyPair().getPublic();

        assertFalse(utxo.isMine(secondWalletKey), "This UTXO should not be mine.");
    }

}
