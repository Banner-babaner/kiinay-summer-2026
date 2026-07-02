package dev.vorstu.validator;

import dev.vorstu.exception.common.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

public class PhoneValidator {
    private static final Pattern PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    public static void validate(String phoneNumber){
        if(phoneNumber==null) return;
        if(!PATTERN.matcher(phoneNumber).matches())
            throw new InvalidPhoneNumberException("PhoneNumber must match pattern +?[0-9]{10,15}");
    }

}
