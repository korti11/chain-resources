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

import io.korti.chainresources.api.blockchain.ITransactionInput;
import io.korti.chainresources.api.blockchain.ITransactionOutput;

public class TransactionInput implements ITransactionInput {

    private final String transactionOutputID;
    private ITransactionOutput UTXO;

    public TransactionInput(String transactionOutputID) {
        this.transactionOutputID = transactionOutputID;
    }

    @Override
    public String getTransactionOutputID() {
        return this.transactionOutputID;
    }

    @Override
    public ITransactionOutput getUTXO() {
        return this.UTXO;
    }

    @Override
    public void setUTXO(ITransactionOutput utxo) {
        this.UTXO = utxo;
    }

}
