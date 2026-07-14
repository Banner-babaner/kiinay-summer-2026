package dev.vorstu.config;

import dev.vorstu.exception.mail.OrbitrWantsAnException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
public class ExceptionOrbitr {
    private static final Random RANDOM = new Random();
    public static void ask(){
        if(RANDOM.nextBoolean()) {
            log.info("The great orbitr wants to throw an exception >_<");
            throw new OrbitrWantsAnException();
        }
    }
}
