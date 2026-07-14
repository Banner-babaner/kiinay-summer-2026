package dev.vorstu.service;

import dev.vorstu.config.ExceptionOrbitr;
import dev.vorstu.dto.event.EmailErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailErrorConsumer {
    @Value("${app.mail.error-handler.retry-count:1}")
    private int retryCount;
    private final JavaMailSender mailSender;
    private final EmailErrorProducer producer;

    @KafkaListener(topics = "email-errors", groupId = "email-error-group")
    public void listen(EmailErrorEvent event) {
        log.info("Received email error from Kafka:");
        log.info("  To: {}", event.to());
        log.info("  Subject: {}", event.subject());
        log.info("  Error: {}", event.errorMessage());
        log.info("  At: {}", event.timestamp());
        log.info("  Attempt: {}", event.retryNumber());
        if(event.retryNumber()>=retryCount){
            log.info("Message to {} expired all retries and was forgotten", event.to());
        }
        else{
            resend(event);
        }
    }

    private void resend(EmailErrorEvent event){
        int retryNumber = event.retryNumber()+1;
        log.info("Retrying to send mail to {}, {}/{}", event.to(), retryNumber, retryCount);
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.to());
            message.setSubject(event.subject());
            message.setText(event.text());
            mailSender.send(message);
            ExceptionOrbitr.ask();
            log.info("Message for {} successful resent", event.to());
        }
        catch (Exception e){
            producer.sendError(new EmailErrorEvent(event.to(), event.subject(), event.text(), e.getMessage(), LocalDateTime.now(), retryNumber));
        }
    }
}
