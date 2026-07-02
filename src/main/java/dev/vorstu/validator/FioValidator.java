package dev.vorstu.validator;


import dev.vorstu.exception.common.InvalidFioFormatException;

public class FioValidator {
    public static void validate(String fio){
        if(fio==null) throw new InvalidFioFormatException("Fio must not be null");
        if(fio.length()>64) throw new InvalidFioFormatException("Too long fio");
    }
}
