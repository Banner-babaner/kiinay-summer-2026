package dev.vorstu.exception.group;

public class StudentAlreadyPresentsException extends RuntimeException {
    public StudentAlreadyPresentsException(String message) {
        super(message);
    }
}
