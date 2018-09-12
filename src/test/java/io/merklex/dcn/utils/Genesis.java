package io.merklex.dcn.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Genesis {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonNode genesisConfig;
    private static final HashMap<String, Credentials> keys = new HashMap<>();

    public static Credentials GetKey(String name) {
        return keys.get(name);
    }

    public static Set<String> KeyNames() {
        return keys.keySet();
    }

    public static String GetBalance(String name) {
        String address = GetKey(name).getAddress();
        return genesisConfig.get("alloc").get(address).get("balance").asText();
    }

    public static String getGasLimit() {
        return genesisConfig.get("gasLimit").asText();
    }

    static {
        try {
            genesisConfig = mapper.readTree(new File("src/test/resources/test_net/genesis.json"));
            JsonNode keyConfig = mapper.readTree(new File("src/test/resources/test_net/keys.json"));
            keyConfig.fields().forEachRemaining(entry ->
                    keys.put(entry.getKey(), Credentials.create(entry.getValue().get("private_key").asText())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
