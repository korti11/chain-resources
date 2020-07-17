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

import java.util.Map;

/**
 * The blockchain is used to store the mined blocks in a list as well as the unspent output transactions.
 */
public interface IBlockchain {

    /**
     * Adds a new block to the chain.
     * @param block Block to add.
     * @return True if the block got successfully added otherwise false.
     */
    boolean addBlock(IBlock block);

    /**
     * Adds a new unspent output transaction.
     * @param utxo UTXO to add.
     */
    void addUTXO(ITransactionOutput utxo);

    /**
     * Remove a unspent output transaction.
     * @param id ID of the UTXO.
     */
    void removeUTXO(String id);

    /**
     * Returns the current difficulty of this blockchain.
     * @return Difficulty
     */
    int getDifficulty();

    /**
     * Returns the min value for a transaction.
     * @return Min value for a transaction
     */
    float getMinTransactionValue();

    /**
     * Returns all UTXOs in a map where the UTXO ID is mapped to the UTXO.
     * @implNote The returned map should be immutable. Use {@link java.util.Collections#unmodifiableMap(Map)}.
     * @return A map with all UTXOs with the ID as the key.
     */
    Map<String, ITransactionOutput> getUTXOs();

    /**
     * Returns the last block of the chain.
     * @return Last block of the chain.
     */
    IBlock lastBlock();

    /**
     * Validates the whole chain.
     * @implNote The result should be cached to only validate if a new block got added.
     * @return True if the chain is valid otherwise false.
     */
    boolean validate();

    /**
     * Checks if the chain got validated and is valid.
     * @return True if chain is valid otherwise false.
     */
    boolean isValid();
}
