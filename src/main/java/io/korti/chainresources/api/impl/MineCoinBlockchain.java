/*
 *    Copyright 2020 Korti
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.korti.chainresources.api.impl;

import io.korti.chainresources.api.blockchain.IBlock;
import io.korti.chainresources.api.blockchain.IBlockchain;
import io.korti.chainresources.api.blockchain.ITransactionOutput;

import java.util.*;

public class MineCoinBlockchain implements IBlockchain {

    private final List<IBlock> blocks = new LinkedList<>();
    private final Map<String, ITransactionOutput> UTXOs = new HashMap<>();

    private final float minTransactionValue;
    private int difficulty;     // The difficulty should only be changed after a block got successfully added.
    private boolean needsValidation = true;

    public MineCoinBlockchain() {
        this(0.01f, 2);
    }

    public MineCoinBlockchain(int startDifficulty) {
        this(0.01f, startDifficulty);
    }

    public MineCoinBlockchain(float minTransactionValue) {
        this(minTransactionValue, 2);
    }

    public MineCoinBlockchain(float minTransactionValue, int startDifficulty) {
        this.minTransactionValue = minTransactionValue;
        this.difficulty = startDifficulty;
    }

    @Override
    public boolean addBlock(IBlock block) {
        if(!block.isMined(difficulty)) {
            return false;
        }
        this.blocks.add(block);
        this.needsValidation = true;
        return true;
    }

    @Override
    public void addUTXO(ITransactionOutput utxo) {
        this.UTXOs.put(utxo.getID(), utxo);
    }

    @Override
    public void removeUTXO(String id) {
        this.UTXOs.remove(id);
    }

    @Override
    public int getDifficulty() {
        return this.difficulty;
    }

    @Override
    public float getMinTransactionValue() {
        return this.minTransactionValue;
    }

    @Override
    public Map<String, ITransactionOutput> getUTXOs() {
        return Collections.unmodifiableMap(this.UTXOs);
    }

    @Override
    public IBlock lastBlock() {
        if(this.blocks.isEmpty()) {
            return null;
        }
        return this.blocks.get(this.blocks.size() - 1);
    }

    @Override
    public boolean validate() {
        if(this.blocks.size() == 1) { // Validate only genesis block.
            IBlock genesisBlock = this.blocks.get(0);
            if(!genesisBlock.getHash().equals(genesisBlock.calculateHash())) {
                return false;   // Genesis block hash is not equal.
            }
            if(!genesisBlock.getPreviousHash().equals("0")) {
                return false;   // Previous has of genesis block should be "0".
            }
            if(!genesisBlock.isMined(difficulty)) {
                return false;   // Genesis block hasn't been mined.
            }
        }

        IBlock curBlock;
        IBlock prevBlock;

        for(int i = 1; i < this.blocks.size(); i++) {
            curBlock = this.blocks.get(i);
            prevBlock = this.blocks.get(i - 1);

            if(!curBlock.getHash().equals(curBlock.calculateHash())) {
                return false; // Current block hash is not equal.
            }
            if(!prevBlock.getHash().equals(curBlock.getPreviousHash())) {
                return false; // Previous block hash is not equal.
            }
            if(!curBlock.isMined(difficulty)) {     // TODO: This will break if difficulty will be variable.
                return false; // Current block hasn't been mined. This should never happen as this validation is already done on adding a block.
            }
        }

        this.needsValidation = false;
        return true;
    }

    @Override
    public boolean isValid() {
        return !this.needsValidation;
    }

}
