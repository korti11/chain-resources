package io.korti.chainresources.api;

import io.korti.chainresources.api.blockchain.IBlockchain;
import io.korti.chainresources.api.blockchain.ITransaction;
import io.korti.chainresources.api.blockchain.ITransactionOutput;
import io.korti.chainresources.api.blockchain.IWallet;
import io.korti.chainresources.api.impl.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalletTest {

    @Test
    @DisplayName("Wallet has public key.")
    public void hasPublicKey() {
        final IWallet wallet = new Wallet(null);

        assertNotNull(wallet.getPublicKey(), "The wallet should have a public key.");
    }

    @Test
    @DisplayName("Wallet has private key.")
    public void hasPrivateKey() {
        final IWallet wallet = new Wallet(null);

        assertNotNull(wallet.getPrivateKey(), "The wallet should have a private key.");
    }

    @Test
    @DisplayName("Wallet has the right balance.")
    public void hasRightBalance() {
        final IBlockchain blockchain = mock(IBlockchain.class);

        final IWallet wallet = new Wallet(blockchain);

        final ITransactionOutput UTXO1 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO2 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO3 = mock(ITransactionOutput.class);

        final Map<String, ITransactionOutput> UTXOs = new HashMap<>(3);

        UTXOs.put("A", UTXO1);
        UTXOs.put("B", UTXO2);
        UTXOs.put("C", UTXO3);

        when(blockchain.getUTXOs()).thenReturn(UTXOs);

        when(UTXO1.isMine(wallet.getPublicKey())).thenReturn(true);
        when(UTXO2.isMine(wallet.getPublicKey())).thenReturn(true);
        when(UTXO3.isMine(wallet.getPublicKey())).thenReturn(false);

        when(UTXO1.getValue()).thenReturn(7f);
        when(UTXO2.getValue()).thenReturn(8f);

        float result = wallet.getBalance();

        assertEquals(15f, result, "The wallet balance is not right.");
    }

    @Test
    @DisplayName("Send more funds then the wallet has balance.")
    public void sendFundsWithNotEnoughBalance() {
        final IBlockchain blockchain = mock(IBlockchain.class);

        final IWallet senderWallet = new Wallet(blockchain);
        final IWallet receiverWallet = new Wallet(blockchain);

        final ITransactionOutput UTXO1 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO2 = mock(ITransactionOutput.class);

        final Map<String, ITransactionOutput> UTXOs = new HashMap<>(3);

        UTXOs.put("A", UTXO1);
        UTXOs.put("B", UTXO2);

        when(blockchain.getUTXOs()).thenReturn(UTXOs);

        when(UTXO1.isMine(senderWallet.getPublicKey())).thenReturn(true);
        when(UTXO2.isMine(senderWallet.getPublicKey())).thenReturn(true);

        when(UTXO1.getValue()).thenReturn(7f);
        when(UTXO2.getValue()).thenReturn(8f);

        ITransaction transaction = senderWallet.sendFunds(receiverWallet.getPublicKey(), 20f);

        assertNull(transaction, "The transaction should discard because the balance was to small.");
    }

    @Test
    @DisplayName("Send funds from one wallet to another one.")
    public void sendFunds() {
        final IBlockchain blockchain = mock(IBlockchain.class);

        final IWallet senderWallet = new Wallet(blockchain);
        final IWallet receiverWallet = new Wallet(blockchain);

        final ITransactionOutput UTXO1 = mock(ITransactionOutput.class);
        final ITransactionOutput UTXO2 = mock(ITransactionOutput.class);

        final Map<String, ITransactionOutput> UTXOs = new HashMap<>(3);

        UTXOs.put("A", UTXO1);
        UTXOs.put("B", UTXO2);

        when(blockchain.getUTXOs()).thenReturn(UTXOs);

        when(UTXO1.isMine(senderWallet.getPublicKey())).thenReturn(true);
        when(UTXO2.isMine(senderWallet.getPublicKey())).thenReturn(true);

        when(UTXO1.getValue()).thenReturn(7f);
        when(UTXO2.getValue()).thenReturn(8f);

        ITransaction transaction = senderWallet.sendFunds(receiverWallet.getPublicKey(), 12f);

        assertNotNull(transaction, "The transaction should discard because the balance was to small.");
    }

}
