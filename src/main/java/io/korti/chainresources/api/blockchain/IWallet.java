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
import java.security.PublicKey;

/**
 * The wallet is used to store the UTXOs for this wallet as well as the public and private key.
 */
public interface IWallet {

    /**
     * Returns the private key of the wallet.
     * @return Private key
     */
    PrivateKey getPrivateKey();

    /**
     * Returns the public key of the wallet.
     * @return Public key
     */
    PublicKey getPublicKey();

    /**
     * Returns the balance of the wallet.
     * @return Balance
     */
    float getBalance();

    /**
     * Creates a new transaction.
     * @param receiver Public key of the receiver wallet.
     * @param value The value that gets transferred from this wallet to the receiver wallet.
     * @return Newly created transaction.
     */
    ITransaction sendFunds(PublicKey receiver, float value);

}
