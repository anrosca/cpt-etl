package inc.evil.crypto.etl.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserializer implements Deserializer<JsonNode> {
    private String encoding = "UTF8";
    private final ObjectMapper mapper = new ObjectMapper();

    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("deserializer.encoding");
        }
        if (encodingValue instanceof String) {
            this.encoding = (String)encodingValue;
        }
    }

    @Override
    public JsonNode deserialize(String topic, byte[] payload) {
        try {
            String json = new String(payload, encoding);
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new KafkaDeserializationException(e);
        }
    }

    public static class KafkaDeserializationException extends RuntimeException {
        public KafkaDeserializationException(Throwable cause) {
            super(cause);
        }
    }
}
