package dev.vorstu.exception.student;

public class StudentAlreadyHasAccountException extends RuntimeException {
    public StudentAlreadyHasAccountException(String message) {
        super(message);
    }
}
