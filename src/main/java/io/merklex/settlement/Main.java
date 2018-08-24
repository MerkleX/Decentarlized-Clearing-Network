package io.merklex.settlement;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import static org.web3j.crypto.Hash.sha256;

public class Main {
    public static void main(String[] args) {
        ECKeyPair privateKey = ECKeyPair.create(sha256("contract".getBytes()));
        System.out.println(Keys.getAddress(privateKey));
        System.out.println(privateKey.getPrivateKey().toString(16));
    }
}
