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

import io.korti.chainresources.api.blockchain.ITransactionOutput;

import java.security.PublicKey;

public class TransactionOutput implements ITransactionOutput {

    private final String id;
    private final PublicKey receiver;
    private final float value;

    public TransactionOutput(PublicKey receiver, float value, String parentTransactionID) {
        this.receiver = receiver;
        this.value = value;

        this.id = Util.toSHA256(Util.keyToString(this.receiver) + parentTransactionID);
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public boolean isMine(PublicKey key) {
        return receiver.equals(key);
    }

}
