package dev.vorstu.service;

import dev.vorstu.dto.event.EmailErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailErrorProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "email-errors";

    public void sendError(String to, String subject, String message, Exception error) {
        sendError(new EmailErrorEvent(
                to,
                subject,
                message,
                error.getMessage(),
                LocalDateTime.now(),
                0
        ));
    }

    public void sendError(EmailErrorEvent event){
        kafkaTemplate.send(TOPIC, event);
        log.info("Email error sent to Kafka");
    }
}
