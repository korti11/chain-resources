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

import io.korti.chainresources.api.blockchain.*;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Wallet implements IWallet {

    private final IBlockchain blockchain;
    private final Map<String, ITransactionOutput> UTXOs = new HashMap<>();

    private KeyPair keyPair;

    public Wallet(IBlockchain blockchain) {
        this.blockchain = blockchain;
        this.keyPair = generateKeyPair();
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    @Override
    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }

    @Override
    public float getBalance() {
        final List<ITransactionOutput> outputs = blockchain.getUTXOs().values().stream()
                .filter(UTXO -> UTXO.isMine(getPublicKey())).collect(Collectors.toList());
        float total = 0;
        for (ITransactionOutput output : outputs) {
            UTXOs.put(output.getID(), output);
            total += output.getValue();
        }
        return total;
    }

    @Override
    public ITransaction sendFunds(PublicKey receiver, float value) {
        if (getBalance() < value) {
            return null;    // Not enough funds to send transaction. Transaction discarded.
        }

        List<ITransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (ITransactionOutput output : UTXOs.values()) {
            total += output.getValue();
            inputs.add(new TransactionInput(output.getID()));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(this.blockchain, getPublicKey(), receiver, value, inputs);
        newTransaction.generateSignature(getPrivateKey());

        inputs.forEach(input -> UTXOs.remove(input.getTransactionOutputID()));

        return newTransaction;
    }

}
