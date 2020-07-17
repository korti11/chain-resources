package io.korti.chainresources.api;

import io.korti.chainresources.api.blockchain.IBlock;
import io.korti.chainresources.api.blockchain.IBlockchain;
import io.korti.chainresources.api.blockchain.ITransaction;
import io.korti.chainresources.api.blockchain.ITransactionOutput;
import io.korti.chainresources.api.impl.MineCoinBlockchain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MineCoinBlockchainTest {

    @Test
    @DisplayName("Start with difficulty.")
    public void startDifficulty() {
        final IBlockchain blockchain = new MineCoinBlockchain(5);

        assertEquals(5, blockchain.getDifficulty(), "The blockchain should start with the difficulty of 5.");
    }

    @Test
    @DisplayName("Custom min transaction value.")
    public void customMinTransactionValue() {
        final IBlockchain blockchain = new MineCoinBlockchain(0.1f);

        assertEquals(0.1f, blockchain.getMinTransactionValue(), "The blockchain should have a min transaction value of 0.1");
    }

    @Test
    @DisplayName("Add a not mined block to the blockchain.")
    public void addNotMinedBlockToBlockchain() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock notMinedBlock = mock(IBlock.class);

        when(notMinedBlock.isMined(2)).thenReturn(false);

        boolean result = blockchain.addBlock(notMinedBlock);

        assertFalse(result, "The block should not be added to the blockchain because it's not mined.");
    }

    @Test
    @DisplayName("Add a mined block to the blockchain.")
    public void addMineBlockToBlockchain() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock minedBlock = mock(IBlock.class);

        when(minedBlock.isMined(2)).thenReturn(true);

        boolean result = blockchain.addBlock(minedBlock);

        assertTrue(result, "The block should be added to the blockchain because it's mined.");
    }

    @Test
    @DisplayName("Get last block.")
    public void getLastBlock() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock block = mock(IBlock.class);

        when(block.isMined(2)).thenReturn(true);

        blockchain.addBlock(block);

        assertEquals(block, blockchain.lastBlock(), "The last block should equals the added one.");
    }

    @Test
    @DisplayName("Get last block from empty chain.")
    public void getLastBlockFromEmptyChain() {
        final IBlockchain blockchain = new MineCoinBlockchain();

        assertNull(blockchain.lastBlock(), "The last block should be null if the blockchain is empty.");
    }

    @Test
    @DisplayName("Validate a blockchain with a block that has not a equal block hash.")
    public void validateNotEqualBlockHash() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock block = mock(IBlock.class);

        when(block.isMined(2)).thenReturn(true);
        when(block.getHash()).thenReturn("A");
        when(block.calculateHash()).thenReturn("B");

        blockchain.addBlock(block);
        boolean result = blockchain.validate();

        assertFalse(result, "The blockchain should not be valid with a block that has not a equal hash.");
    }

    @Test
    @DisplayName("Validate a blockchain with a block that has not a equal prev block hash.")
    public void validateNotEqualPrevBlockHash() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock blockOne = mock(IBlock.class);
        final IBlock blockTwo = mock(IBlock.class);

        when(blockOne.isMined(2)).thenReturn(true);
        when(blockOne.getHash()).thenReturn("A");
        when(blockTwo.isMined(2)).thenReturn(true);
        when(blockTwo.getPreviousHash()).thenReturn("B");
        when(blockTwo.getHash()).thenReturn("C");
        when(blockTwo.calculateHash()).thenReturn("C");

        blockchain.addBlock(blockOne);
        blockchain.addBlock(blockTwo);

        boolean result = blockchain.validate();

        assertFalse(result, "The blockchain should not be valid with a block that has not a equal prev hash.");
    }

    @Test
    @DisplayName("Validate a blockchain.")
    public void validateBlockchain() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock blockOne = mock(IBlock.class);
        final IBlock blockTwo = mock(IBlock.class);

        when(blockOne.isMined(2)).thenReturn(true);
        when(blockOne.getHash()).thenReturn("A");
        when(blockTwo.isMined(2)).thenReturn(true);
        when(blockTwo.getPreviousHash()).thenReturn("A");
        when(blockTwo.getHash()).thenReturn("B");
        when(blockTwo.calculateHash()).thenReturn("B");

        blockchain.addBlock(blockOne);
        blockchain.addBlock(blockTwo);

        boolean result = blockchain.validate();

        assertTrue(result, "The blockchain should be valid.");
    }

    @Test
    @DisplayName("Validate blockchain with one block.")
    public void validateBlockchainWithOneBlock() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock block = mock(IBlock.class);

        when(block.isMined(2)).thenReturn(true);
        when(block.getHash()).thenReturn("A");
        when(block.calculateHash()).thenReturn("A");
        when(block.getPreviousHash()).thenReturn("0");

        blockchain.addBlock(block);
        boolean result = blockchain.validate();

        assertTrue(result, "The blockchain should be valid.");
    }

    @Test
    @DisplayName("Blockchain not valid beginning.")
    public void blockchainIsNotValid() {
        final IBlockchain blockchain = new MineCoinBlockchain();

        assertFalse(blockchain.isValid(), "The blockchain should not be valid from the beginning.");
    }

    @Test
    @DisplayName("Blockchain is valid.")
    public void blockchainIsValid() {
        final IBlockchain blockchain = new MineCoinBlockchain(2);
        final IBlock blockOne = mock(IBlock.class);
        final IBlock blockTwo = mock(IBlock.class);

        when(blockOne.isMined(2)).thenReturn(true);
        when(blockOne.getHash()).thenReturn("A");
        when(blockTwo.isMined(2)).thenReturn(true);
        when(blockTwo.getPreviousHash()).thenReturn("A");
        when(blockTwo.getHash()).thenReturn("B");
        when(blockTwo.calculateHash()).thenReturn("B");

        blockchain.addBlock(blockOne);
        blockchain.addBlock(blockTwo);
        blockchain.validate();

        assertTrue(blockchain.isValid(), "The blockchain should be valid.");
    }

    @Test
    @DisplayName("Add a UTXO to the blockchain.")
    public void addUTXOToBlockchain() {
        final IBlockchain blockchain = new MineCoinBlockchain();
        final ITransactionOutput utxo = mock(ITransactionOutput.class);

        when(utxo.getID()).thenReturn("A");

        blockchain.addUTXO(utxo);

        final Map<String, ITransactionOutput> UTXOs = blockchain.getUTXOs();

        assertTrue(UTXOs.containsKey("A"), "The UTXO should be added.");
        assertEquals(utxo, UTXOs.get("A"), "Wrong UTXO added.");
    }

    @Test
    @DisplayName("Get all UTXOs from the blockchain.")
    public void getUTXOsFromBlockchain() {
        final IBlockchain blockchain = new MineCoinBlockchain();
        final ITransactionOutput utxoOne = mock(ITransactionOutput.class);
        final ITransactionOutput utxoTwo = mock(ITransactionOutput.class);

        when(utxoOne.getID()).thenReturn("A");
        when(utxoTwo.getID()).thenReturn("B");

        blockchain.addUTXO(utxoOne);
        blockchain.addUTXO(utxoTwo);

        final Map<String, ITransactionOutput> UTXOs = blockchain.getUTXOs();

        assertTrue(UTXOs.containsKey("A"), "The UTXO with the ID 'A' should be in the map.");
        assertEquals(utxoOne, UTXOs.get("A"), "The UTXO with the ID 'A' is not equal.");
        assertTrue(UTXOs.containsKey("B"), "The UTXO with the ID 'B' should be in the map.");
        assertEquals(utxoTwo, UTXOs.get("B"), "The UTXO with the ID 'B' is not equal.");
    }

    @Test
    @DisplayName("UTXOs map should be immutable.")
    public void utxosMapShouldBeImmutable() {
        final IBlockchain blockchain = new MineCoinBlockchain();

        final Map<String, ITransactionOutput> UTXOs = blockchain.getUTXOs();

        assertThrows(UnsupportedOperationException.class, () -> UTXOs.put("", null));
    }

    @Test
    @DisplayName("Remove a UTXO from the blockchain.")
    public void removeUTXOFromBlockchain() {
        final IBlockchain blockchain = new MineCoinBlockchain();
        final ITransactionOutput utxo = mock(ITransactionOutput.class);

        when(utxo.getID()).thenReturn("A");

        blockchain.addUTXO(utxo);
        blockchain.removeUTXO("A");

        final Map<String, ITransactionOutput> UTXOs = blockchain.getUTXOs();

        assertFalse(UTXOs.containsKey("A"), "The UTXO should be removed.");
    }
}
