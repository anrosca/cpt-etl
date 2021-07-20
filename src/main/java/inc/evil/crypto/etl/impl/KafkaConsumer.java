package inc.evil.crypto.etl.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final EtlService etlService;

    public KafkaConsumer(EtlService etlService) {
        this.etlService = etlService;
    }

    @KafkaListener(topicPattern = "^((.+)\\.(.+)\\.(.+))$", id = "etl-consumer7")
    public void onRecord(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) JsonNode key, @Payload JsonNode message) {
        etlService.process(key, message);
    }
}
