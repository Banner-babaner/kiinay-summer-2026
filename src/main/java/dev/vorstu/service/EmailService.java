package dev.vorstu.service;

import dev.vorstu.config.ExceptionOrbitr;
import dev.vorstu.dto.common.VeryPrimitiveMessage;
import dev.vorstu.dto.event.EmailErrorEvent;
import dev.vorstu.exception.mail.InvalidErrorHandlerMode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Set<String> ALLOWED_ERROR_HANDLER_MODES = Set.of("kafka", "worker", "none");
    private final JavaMailSender mailSender;
    private final EmailErrorProducer producer;
    private final RetryEmailWorkerService workerService;
    @Value("${app.mail.error-handler:none}")
    private String errorHandlerMode;


    public void sendSimpleMessage(VeryPrimitiveMessage messageDto) {
        SimpleMailMessage message = fromPrimitiveMessage(messageDto);
        try{
            mailSender.send(message);
            ExceptionOrbitr.ask();
        }
        catch (Exception e){
            switch (errorHandlerMode){
                case "kafka":
                    producer.sendError(messageDto.getTo(), messageDto.getSubject(), messageDto.getText(), e);
                    break;
                case "worker":
                    workerService.retry(List.of(new VeryPrimitiveMessage(messageDto.getTo(), messageDto.getSubject(), messageDto.getText())));
                    break;
                case "none":
                    break;
                default:
                    throw new InvalidErrorHandlerMode(errorHandlerMode);
            }
            log.warn("Can not send mail to {}", messageDto.getTo());
        }
    }


    public void sendSimpleMessages(List<VeryPrimitiveMessage> dtos) {
        List<SimpleMailMessage> messages = dtos.stream().map(this::fromPrimitiveMessage).toList();
        List<EmailErrorEvent> events = sendWithFailsReturn(messages);
        switch (errorHandlerMode){
            case "kafka":
                events.forEach(producer::sendError);
                break;
            case "worker":
                workerService.retry(events.stream().map(event -> new VeryPrimitiveMessage(
                        event.to(),
                        event.subject(),
                        event.text()
                )).toList());
                break;
            case "none":
                break;
            default:
                throw new InvalidErrorHandlerMode(errorHandlerMode);

        }
        if(!events.isEmpty())
            log.warn("Can not send {} emails", events.size());
    }

    private List<EmailErrorEvent> sendWithFailsReturn(List<SimpleMailMessage> messages){
        List<EmailErrorEvent> fails = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        messages.forEach(message -> {
                    try{
                        mailSender.send(message);
                        ExceptionOrbitr.ask();
                    }
                    catch (Exception e){
                        fails.add(new EmailErrorEvent(message.getTo()[0],
                                message.getSubject(),
                                message.getText(),
                                e.getMessage(),
                                now,
                                0));
                    }
                });
        log.info("Success sending {}/{} messages", messages.size()- fails.size(),messages.size());
        return fails;
    }


    @PostConstruct
    void init(){
        if(!ALLOWED_ERROR_HANDLER_MODES.contains(errorHandlerMode))
            throw new InvalidErrorHandlerMode(errorHandlerMode);
    }

    private SimpleMailMessage fromPrimitiveMessage(VeryPrimitiveMessage source){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(source.getTo());
        message.setText(source.getText());
        message.setSubject(source.getSubject());
        return message;
    }




}