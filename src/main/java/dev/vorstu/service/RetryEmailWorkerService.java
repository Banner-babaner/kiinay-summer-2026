package dev.vorstu.service;

import dev.vorstu.config.ExceptionOrbitr;
import dev.vorstu.dto.common.VeryPrimitiveMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class RetryEmailWorkerService {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final JavaMailSender mailSender;
    @Value("${app.mail.error-handler.worker.sleep-millis:10000}")
    private Long sleepMillis;
    @Value("${app.mail.error-handler.retry-count:1}")
    private Integer retryCount;

    public void retry(List<VeryPrimitiveMessage> messages){
        executor.submit(new RetryWorker(mailSender, messages, retryCount, sleepMillis));
    }
}

@Slf4j
class RetryWorker implements Runnable{
    private final JavaMailSender mailSender;
    private final int maxRetryCount;
    private final List<VeryPrimitiveMessage> toRetry;
    private final long delay;
    private int currentTry=0;

    public RetryWorker(@NonNull JavaMailSender mailSender,
                       @NonNull List<VeryPrimitiveMessage> toRetry,
                       int maxRetryCount,
                       long delay){
        if(maxRetryCount<1)
            throw new IllegalArgumentException("maxRetryCount must be > 0");
        if(delay<0)
            throw new IllegalArgumentException("delay must be >= 0");
        this.mailSender = mailSender;
        this.maxRetryCount=maxRetryCount;
        this.toRetry= new ArrayList<>(toRetry);
        this.delay=delay;
    }

    @Override
    public void run() {
        while (currentTry<maxRetryCount && !toRetry.isEmpty()){
            try {
                Thread.sleep(delay);
                resendAll();
            }
            catch (InterruptedException e){
                log.warn("Retry worker interrupted, stopping");
                Thread.currentThread().interrupt();
                break;
            }
        }
        if(!toRetry.isEmpty())
            log.warn("{} messages was not sent", toRetry.size());
    }

    private void resendAll(){
        currentTry++;
        List<VeryPrimitiveMessage> successes = new ArrayList<>();
        toRetry.forEach(
                message-> {
                    try{
                        sendSimpleMessage(message.getTo(), message.getSubject(), message.getText());
                        successes.add(message);
                        log.info("Message to {} successful resent to by retry {}/{}",
                                message.getTo(),
                                currentTry,
                                maxRetryCount);
                    }
                    catch (Exception e){
                        log.warn("Message for {} was not sent again in retry {}/{}",
                                message.getTo(),
                                currentTry,
                                maxRetryCount);
                    }
                }
        );
        toRetry.removeAll(successes);
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        ExceptionOrbitr.ask();
    }
}
