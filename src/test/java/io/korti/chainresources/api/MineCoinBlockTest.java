package io.korti.chainresources.api;


import io.korti.chainresources.api.blockchain.IBlock;
import io.korti.chainresources.api.blockchain.ITransaction;
import io.korti.chainresources.api.impl.MineCoinBlock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MineCoinBlockTest {

    @Test
    @DisplayName("Add null transaction to block.")
    public void addNullTransaction() {
        final IBlock block = new MineCoinBlock("0");

        boolean result = block.addTransaction(null);

        assertFalse(result, "On adding a null transaction the result should be false.");
    }

    @Test
    @DisplayName("Add not processable transaction to block.")
    public void addNotProcessableTransaction() {
        final IBlock block = new MineCoinBlock("ABC");
        final ITransaction mockedTransaction = mock(ITransaction.class);

        when(mockedTransaction.processTransaction()).thenReturn(false);

        boolean result = block.addTransaction(mockedTransaction);

        assertFalse(result, "On adding a not processable transaction the result should be false.");
    }

    @Test
    @DisplayName("Add processable transaction to block.")
    public void addProcessableTransaction() {
        final IBlock block = new MineCoinBlock("");
        final ITransaction mockedTransaction = mock(ITransaction.class);

        when(mockedTransaction.processTransaction()).thenReturn(true);

        boolean result = block.addTransaction(mockedTransaction);

        assertTrue(result, "On adding a processable transaction the result should be true.");
    }

    @Test
    @DisplayName("Add a list of processable and not processable transactions to block.")
    public void addListOfProcessableAndNotProcessableTransactions() {
        final IBlock block = new MineCoinBlock("");
        final ITransaction mockedTransactionOne = mock(ITransaction.class);
        final ITransaction mockedTransactionTwo = mock(ITransaction.class);
        final ITransaction mockedTransactionThree = mock(ITransaction.class);

        when(mockedTransactionOne.processTransaction()).thenReturn(true);
        when(mockedTransactionTwo.processTransaction()).thenReturn(false);
        when(mockedTransactionThree.processTransaction()).thenReturn(true);

        final List<ITransaction> transactions = new ArrayList<>(3);
        transactions.add(mockedTransactionOne);
        transactions.add(mockedTransactionTwo);
        transactions.add(mockedTransactionThree);

        Map<ITransaction, Boolean> result =  block.addTransactions(transactions);

        assertTrue(result.get(mockedTransactionOne), "The first added transaction was processable.");
        assertFalse(result.get(mockedTransactionTwo), "The second added transaction was not processable.");
        assertTrue(result.get(mockedTransactionThree), "The third added transaction was processable.");
    }

}
