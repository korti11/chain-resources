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

import java.security.PrivateKey;

/**
 * The transaction interface is used to transfer coins from one {@link IWallet} to another one.
 */
public interface ITransaction {

    /**
     * Returns the transaction id.
     * @return ID of the transaction
     */
    String getId();

    /**
     * Process the transaction. Transfer the coins from one wallet to another one.
     * @return True if the transaction was successfully processed otherwise false
     */
    boolean processTransaction();

    /**
     * Sign the transaction with the private key of the sender wallet.
     * @param key Private key of the sender wallet
     */
    void generateSignature(PrivateKey key);

    /**
     * Validates that the transaction got signed by the sender.
     * @return True if the transaction is signed by the sender otherwise false
     */
    boolean verifySignature();
}
