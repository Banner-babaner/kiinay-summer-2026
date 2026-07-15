package dev.vorstu.config;

import dev.vorstu.exception.orbitr.OrbitrWantsAnException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Random;

@Slf4j
public class ExceptionOrbitr {
    private static final Random RANDOM = new Random();
    @Value("${app.exception-orbitr.enable:false}")
    private static boolean enable;

    public static void ask(){
        if(enable&&RANDOM.nextBoolean()) {
            log.info("The great orbitr wants to throw an exception >_<");
            throw new OrbitrWantsAnException();
        }
    }
}
