package io.merklex.settlement.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Keys {
    private static final HashMap<String, Credentials> keys = new HashMap<>();

    public static Credentials get(String name) {
        return keys.get(name);
    }

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(new File("src/test/resources/test_net/keys.json"));

            jsonNode.fields().forEachRemaining(entry -> {
                keys.put(entry.getKey(), Credentials.create(entry.getValue().get("private_key").asText()));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
