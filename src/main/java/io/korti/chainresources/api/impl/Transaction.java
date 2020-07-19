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

import io.korti.chainresources.api.blockchain.IBlockchain;
import io.korti.chainresources.api.blockchain.ITransaction;
import io.korti.chainresources.api.blockchain.ITransactionInput;
import io.korti.chainresources.api.blockchain.ITransactionOutput;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements ITransaction {

    private String id = "";
    private PublicKey sender;
    private PublicKey receiver;
    private float value;
    private byte[] signature = new byte[256];

    private List<ITransactionInput> inputs = new ArrayList<>();
    private List<ITransactionOutput> outputs = new ArrayList<>();

    private IBlockchain blockchain;
    private long timestamp;

    public Transaction(IBlockchain blockchain, PublicKey sender, PublicKey receiver, float value,
                       List<ITransactionInput> inputs) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.inputs = inputs;

        this.blockchain = blockchain;
        this.timestamp = LocalDateTime.now().getLong(ChronoField.EPOCH_DAY);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean processTransaction() {
        if (!verifySignature()) {
            return false;   // Transaction signature failed to verify.
        }

        this.inputs.forEach(i -> i.setUTXO(blockchain.getUTXOs().get(i.getTransactionOutputID())));

        float funds = calculateFunds();
        if (funds < blockchain.getMinTransactionValue()) {
            return false;   // Funds value to small for a transaction.
        }

        float fundsLeft = funds - this.value;
        this.id = calculateHash();
        this.outputs.add(new TransactionOutput(this.receiver, value, this.id));
        this.outputs.add(new TransactionOutput(this.sender, fundsLeft, this.id));

        this.outputs.forEach(blockchain::addUTXO);

        this.inputs.stream().filter(i -> i.getUTXO() != null).forEach(i -> blockchain.removeUTXO(i.getUTXO().getID()));

        return true;
    }

    @Override
    public void generateSignature(PrivateKey key) {
        String data = Util.keyToString(this.sender) + Util.keyToString(this.receiver) + value;
        this.signature = Util.applySHA256RSASig(key, data);
    }

    @Override
    public boolean verifySignature() {
        String data = Util.keyToString(this.sender) + Util.keyToString(this.receiver) + value;
        return Util.verifySHA256RSASig(this.sender, data, this.signature);
    }

    private String calculateHash() {
        return Util.toMD5(Util.keyToString(this.sender) + Util.keyToString(this.receiver) + value + timestamp);
    }

    private float calculateFunds() {
        return this.inputs.stream().filter(i -> i.getUTXO() != null)
                .map(i -> i.getUTXO().getValue()).reduce(0f, (total, value) -> total += value);
    }

}
