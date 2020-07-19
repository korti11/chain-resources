package io.korti.chainresources.api;

import io.korti.chainresources.TestUtil;
import io.korti.chainresources.api.blockchain.IBlockchain;
import io.korti.chainresources.api.blockchain.ITransaction;
import io.korti.chainresources.api.blockchain.ITransactionInput;
import io.korti.chainresources.api.blockchain.ITransactionOutput;
import io.korti.chainresources.api.impl.Transaction;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionTest {

    private static KeyPair senderWallet;
    private static KeyPair receiverWallet;

    @BeforeAll
    public static void init() {
        TransactionTest.senderWallet = TestUtil.generateKeyPair();
        TransactionTest.receiverWallet = TestUtil.generateKeyPair();
    }

    @Test
    @DisplayName("Sign and verify a transaction with the sender keys.")
    public void signAndVerifyTransaction() {
        final ITransaction transaction = new Transaction(null,
                senderWallet.getPublic(), receiverWallet.getPublic(), 10f, null);

        transaction.generateSignature(senderWallet.getPrivate());

        assertTrue(transaction.verifySignature(), "The transaction should be verified.");
    }

    @Test
    @DisplayName("Sign with the receiver key and verify with the sender key.")
    public void signAndVerifyTransactionWithDifferentKeys() {
        final ITransaction transaction = new Transaction(null,
                senderWallet.getPublic(), receiverWallet.getPublic(), 10f, null);

        transaction.generateSignature(receiverWallet.getPrivate());

        assertFalse(transaction.verifySignature(), "The transaction should not be verified.");
    }

    @Test
    @DisplayName("Verify a not signed transaction.")
    public void verifyNotSignedTransaction() {
        final ITransaction transaction = new Transaction(null,
                senderWallet.getPublic(), receiverWallet.getPublic(), 10f, null);

        assertFalse(transaction.verifySignature(), "The transaction should not be verified.");
    }

    @Test
    @DisplayName("Process a not signed transaction.")
    public void processNotSignedTransaction() {
        final ITransaction transaction = new Transaction(null,
                senderWallet.getPublic(), receiverWallet.getPublic(), 10f, null);

        assertFalse(transaction.processTransaction(), "The transaction should not process as it is not signed.");
    }

    @Test
    @DisplayName("Process a transaction with to low funds.")
    public void processToLowFundsTransaction() {
        //region Mock init
        final IBlockchain blockchain = mock(IBlockchain.class);

        final ITransactionOutput UTXO1 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO2 = mock(ITransactionOutput.class);

        when(UTXO1.getValue()).thenReturn(0.00001f);
        when(UTXO2.getValue()).thenReturn(0.00002f);

        final Map<String, ITransactionOutput> UTXOs = new HashMap<>(2);
        UTXOs.put("A", UTXO1);
        UTXOs.put("B", UTXO2);

        when(blockchain.getUTXOs()).thenReturn(UTXOs);

        final ITransactionInput input1 = mock(ITransactionInput.class);
        final ITransactionInput input2 = mock(ITransactionInput.class);

        when(input1.getTransactionOutputID()).thenReturn("A");
        when(input2.getTransactionOutputID()).thenReturn("B");

        doNothing().when(input1).setUTXO(UTXO1);
        doNothing().when(input2).setUTXO(UTXO2);

        when(input1.getUTXO()).thenReturn(UTXO1);
        when(input2.getUTXO()).thenReturn(UTXO2);

        when(blockchain.getMinTransactionValue()).thenReturn(0.01f);
        //endregion

        final ITransaction transaction = new Transaction(blockchain, senderWallet.getPublic(),
                receiverWallet.getPublic(), 10f, Arrays.asList(input1, input2));

        transaction.generateSignature(senderWallet.getPrivate());

        assertFalse(transaction.processTransaction(),
                "This transaction should not be processed as the funds are to low for a min transaction.");
    }

    @Test
    @DisplayName("Process a transaction.")
    public void processTransaction() {
        //region Mock init
        final IBlockchain blockchain = mock(IBlockchain.class);

        final ITransactionOutput UTXO1 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO2 = mock(ITransactionOutput.class);

        when(UTXO1.getID()).thenReturn("A");
        when(UTXO2.getID()).thenReturn("B");

        when(UTXO1.getValue()).thenReturn(9f);
        when(UTXO2.getValue()).thenReturn(12f);

        final Map<String, ITransactionOutput> UTXOs = new HashMap<>(2);
        UTXOs.put("A", UTXO1);
        UTXOs.put("B", UTXO2);

        when(blockchain.getUTXOs()).thenReturn(UTXOs);

        final ITransactionInput input1 = mock(ITransactionInput.class);
        final ITransactionInput input2 = mock(ITransactionInput.class);

        when(input1.getTransactionOutputID()).thenReturn("A");
        when(input2.getTransactionOutputID()).thenReturn("B");

        doNothing().when(input1).setUTXO(UTXO1);
        doNothing().when(input2).setUTXO(UTXO2);

        when(input1.getUTXO()).thenReturn(UTXO1);
        when(input2.getUTXO()).thenReturn(UTXO2);

        when(blockchain.getMinTransactionValue()).thenReturn(0.01f);
        doAnswer(invocation -> {
            Object argument = invocation.getArgument(0);

            if(argument instanceof ITransactionOutput) {
                ITransactionOutput utxo = (ITransactionOutput) argument;
                UTXOs.put(utxo.getID(), utxo);
            }
            return null;
        }).when(blockchain).addUTXO(any());
        doAnswer(invocation -> {
            Object argument = invocation.getArgument(0);
            UTXOs.remove(argument);
            return null;
        }).when(blockchain).removeUTXO(anyString());
        //endregion

        final ITransaction transaction = new Transaction(blockchain, senderWallet.getPublic(),
                receiverWallet.getPublic(), 10f, Arrays.asList(input1, input2));

        transaction.generateSignature(senderWallet.getPrivate());

        assertTrue(transaction.processTransaction(), "This transaction should be successful processed.");
        assertFalse(UTXOs.containsKey("A") || UTXOs.containsValue(UTXO1),
                "The UTXOs should not contain the first old UTXO anymore.");
        assertFalse(UTXOs.containsKey("B") || UTXOs.containsValue(UTXO2),
                "The UTXOs should not contain the second old UTXO anymore.");
        assertEquals(2, UTXOs.size(), "The UTXOs should again contain 2 UTXOs.");
    }

    @Test
    @DisplayName("Check if the transaction id is set.")
    public void isTransactionIDSet() {
        //region Mock init
        final IBlockchain blockchain = mock(IBlockchain.class);

        final ITransactionOutput UTXO1 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO2 = mock(ITransactionOutput.class);

        when(UTXO1.getID()).thenReturn("A");
        when(UTXO2.getID()).thenReturn("B");

        when(UTXO1.getValue()).thenReturn(9f);
        when(UTXO2.getValue()).thenReturn(12f);

        final Map<String, ITransactionOutput> UTXOs = new HashMap<>(2);
        UTXOs.put("A", UTXO1);
        UTXOs.put("B", UTXO2);

        when(blockchain.getUTXOs()).thenReturn(UTXOs);

        final ITransactionInput input1 = mock(ITransactionInput.class);
        final ITransactionInput input2 = mock(ITransactionInput.class);

        when(input1.getTransactionOutputID()).thenReturn("A");
        when(input2.getTransactionOutputID()).thenReturn("B");

        doNothing().when(input1).setUTXO(UTXO1);
        doNothing().when(input2).setUTXO(UTXO2);

        when(input1.getUTXO()).thenReturn(UTXO1);
        when(input2.getUTXO()).thenReturn(UTXO2);

        when(blockchain.getMinTransactionValue()).thenReturn(0.01f);
        doAnswer(invocation -> {
            Object argument = invocation.getArgument(0);

            if(argument instanceof ITransactionOutput) {
                ITransactionOutput utxo = (ITransactionOutput) argument;
                UTXOs.put(utxo.getID(), utxo);
            }
            return null;
        }).when(blockchain).addUTXO(any());
        doAnswer(invocation -> {
            Object argument = invocation.getArgument(0);
            UTXOs.remove(argument);
            return null;
        }).when(blockchain).removeUTXO(anyString());
        //endregion

        final ITransaction transaction = new Transaction(blockchain, senderWallet.getPublic(),
                receiverWallet.getPublic(), 10f, Arrays.asList(input1, input2));

        transaction.generateSignature(senderWallet.getPrivate());
        transaction.processTransaction();

        assertNotEquals("", transaction.getId(), "The transaction ID should be set after processing a transaction.");
    }

    @Test
    @DisplayName("Check that the transaction id is not set before processing.")
    public void transactionIDShouldNotBeSetBeforeProcessing() {
        final ITransaction transaction = new Transaction(null,
                senderWallet.getPublic(), receiverWallet.getPublic(), 10f, null);

        assertEquals("", transaction.getId(), "The transaction ID should not be set before processing a transaction.");
    }

}
