package io.merklex.web3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.protocol.core.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RevertCodeExtractor {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String Get(Response.Error error) throws IOException {
        String data = error.getData();
        JsonNode jsonNode = MAPPER.readTree(data);
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().startsWith("0x")) {
                return field.getValue().get("return").asText();
            }
        }
        throw new IllegalArgumentException("Does not have an error code");
    }
}
