package io.korti.chainresources.api;

import io.korti.chainresources.api.blockchain.ITransactionInput;
import io.korti.chainresources.api.blockchain.ITransactionOutput;
import io.korti.chainresources.api.impl.TransactionInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class TransactionInputTest {

    @Test
    @DisplayName("The transaction output ID gets set in the constructor.")
    public void transactionOutputIDSet() {
        final ITransactionInput input = new TransactionInput("Test");

        assertEquals("Test", input.getTransactionOutputID(), "The transaction output ID didn't get set correctly.");
    }

    @Test
    @DisplayName("The UTXO gets set and read.")
    public void transactionOutputSetAndGet() {
        final ITransactionInput input = new TransactionInput("");
        final ITransactionOutput UTXO = mock(ITransactionOutput.class);

        input.setUTXO(UTXO);

        assertEquals(UTXO, input.getUTXO(), "The UTXO did not get set or read correctly");
    }

}
