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
import io.korti.chainresources.api.blockchain.ITransaction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class MineCoinBlock implements IBlock {

    private final String prevHash;
    private final long timestamp;

    private final List<ITransaction> transactions = new ArrayList<>();

    private String hash;
    private int nonce;
    private boolean updateMerkel = false;

    private String merkelRoot = "";
    private String checkCache = "";

    public MineCoinBlock(String prevHash) {
        this.prevHash = prevHash;
        this.timestamp = LocalDateTime.now().getLong(ChronoField.EPOCH_DAY);

        this.hash = this.calculateHash();   // This should always be the last statement.
    }

    @Override
    public String getHash() {
        return this.hash;
    }

    @Override
    public String getPreviousHash() {
        return this.prevHash;
    }

    @Override
    public String calculateHash() {
        if (merkelRoot.isEmpty() || updateMerkel) {
            merkelRoot = Util.getMerkelRoot(this.transactions);
            updateMerkel = false;
        }

        return Util.toMD5(prevHash + timestamp + nonce + merkelRoot);
    }

    @Override
    public boolean mineBlock(int difficulty) {
        if (isMined(difficulty)) {
            return true;    // Block already minded. It should not be mined again if it's already mined valid mined for once.
        }

        nonce++;
        this.hash = this.calculateHash();
        return isMined(difficulty);
    }

    @Override
    public boolean isMined(int difficulty) {
        if (checkCache.length() != difficulty) {
            checkCache = new String(new char[difficulty]).replace("\0", "0");
        }

        // Block is mined if the hash starts with {difficulty} 0s.
        return this.hash.substring(0, difficulty).equals(checkCache);
    }

    @Override
    public boolean addTransaction(ITransaction transaction) {
        if (transaction == null) {
            return false;
        }

        if(!prevHash.equals("0") && !transaction.processTransaction()) {
            return false;
        }

        this.transactions.add(transaction);
        this.updateMerkel = true;

        return true;
    }
}
