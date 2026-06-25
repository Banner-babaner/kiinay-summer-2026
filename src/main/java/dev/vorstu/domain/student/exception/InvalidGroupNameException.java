package dev.vorstu.domain.student.exception;

public class InvalidGroupNameException extends RuntimeException {
    public InvalidGroupNameException(String message) {
        super(message);
    }
}
