package dev.vorstu.domain.student;

import dev.vorstu.domain.student.exception.InvalidFioFormatException;
import dev.vorstu.domain.student.exception.InvalidGroupNameException;
import dev.vorstu.domain.student.exception.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;

@Component
public class StudentValidator {
    public void validateFio(String fio){
        if(fio==null)
            throw new NullPointerException("fio is null");
        if(fio.isBlank() || fio.length()>64)
            throw new InvalidFioFormatException(fio);
    }

    public void validateGroupName(String groupName){
        if(groupName!=null && (groupName.isBlank() || groupName.length()>64))
            throw new InvalidGroupNameException(groupName);
    }

    public void validatePhoneNumber(String phoneNumber){
        if(phoneNumber != null && (phoneNumber.isBlank() || phoneNumber.length()>24))
            throw new InvalidPhoneNumberException(phoneNumber);
    }
}
