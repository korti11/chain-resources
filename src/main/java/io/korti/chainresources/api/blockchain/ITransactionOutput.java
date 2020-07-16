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

import java.security.PublicKey;

/**
 * The transaction output is used to determine who gets what of the transaction.
 *
 * Example:
 * Wallet A sends 5 coins to Wallet B. A new transaction gets created and x UTXOs gets collected for this transaction.
 * But the coin sum of this UTXOs is 6 coins, now 2 transaction outputs get generated. One for the transfer from
 * Wallet A to Wallet B over 5 coins and a second one over 1 coin for the back transfer in Wallet A.
 */
public interface ITransactionOutput {

    /**
     * Returns the ID of the unspent output transaction.
     * @return ID
     */
    String getID();

    /**
     * Returns the coin value of this unspent output transaction.
     * @return Coin value
     */
    float getValue();

    /**
     * Checks if this UTXO is part of the wallet with the public key.
     * @param key Public key of the wallet to check
     * @return True if this UTXO is part of the wallet otherwise false
     */
    boolean isMine(PublicKey key);
}
