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

package io.korti.chainresources.api.blockchain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The block is used to calculate and store the hash value for given block as well as to execute the mining operation.
 */
public interface IBlock {

    /**
     * Returns the last calculated hash for this block.
     * @return Last calculated hash.
     */
    String getHash();

    /**
     * Returns the valid hash of the previous block in the chain.
     * @return Previous block hash.
     */
    String getPreviousHash();

    /**
     * Calculates the hash and returns it.
     * The hash should be calculated of
     * - the previous block hash,
     * - the timestamp of the block creation,
     * - a variable number (nonce) and
     * - the transactions that are stored on this block.
     * @return Calculated hash.
     */
    String calculateHash();

    /**
     * Tries to mine the block. At each try the variable number (nonce) should be changed in someway.
     * @param difficulty The difficulty is used to determine with how many 0s the hash should start or end.
     * @return True if the block got mined or is already mined otherwise false.
     */
    boolean mineBlock(int difficulty);

    /**
     * Checks if the block is already mined.
     * @param difficulty The difficulty is used to determine with how many 0s the hash should start or end.
     * @return True if the block is mined otherwise false.
     */
    boolean isMined(int difficulty);

    /**
     * Adds a new transaction to store on this block.
     * @param transaction New transaction.
     * @return True if the transaction got successfully added to this block otherwise false.
     */
    boolean addTransaction(ITransaction transaction);

    /**
     * Adds all new transactions to store on this block.
     * @param transactions List of new transactions.
     * @return A map who maps the transaction to a boolean if the transaction got successfully added to this block.
     */
    default Map<ITransaction, Boolean> addTransactions(List<ITransaction> transactions) {
        return transactions.stream().collect(Collectors.toMap(trans -> trans, this::addTransaction));
    }

}
