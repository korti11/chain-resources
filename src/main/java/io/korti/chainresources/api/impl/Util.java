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

import io.korti.chainresources.api.blockchain.ITransaction;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class Util {

    public static String toMD5(String input) {
        return toHash(input, "MD5");
    }

    public static String toSHA256(String input) {
        return toHash(input, "SHA-256");
    }

    public static String toHash(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xFF & hash[i]);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMerkelRoot(List<ITransaction> transactions) {
        int count = transactions.size();
        List<String> prevTreeLayer = new ArrayList<>(count);

        for (ITransaction transaction : transactions) {
            prevTreeLayer.add(transaction.getId());
        }

        List<String> treeLayer = prevTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>(prevTreeLayer.size() - 1);

            for (int i = 1; i < prevTreeLayer.size(); i++) {
                treeLayer.add(toSHA256(prevTreeLayer.get(i - 1) + prevTreeLayer.get(i)));
            }

            count = treeLayer.size();
            prevTreeLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
