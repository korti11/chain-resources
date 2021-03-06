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

/**
 * The transaction input is used to calculate the funds for a transaction.
 */
public interface ITransactionInput {

    /**
     * Returns the ID of the transaction output.
     * @return Transaction output ID
     */
    String getTransactionOutputID();

    /**
     * Get the unspent output transaction.
     * @return UTXO
     */
    ITransactionOutput getUTXO();

    /**
     * Setts the unspent output transaction.
     * @param utxo UTXO
     */
    void setUTXO(ITransactionOutput utxo);
}
