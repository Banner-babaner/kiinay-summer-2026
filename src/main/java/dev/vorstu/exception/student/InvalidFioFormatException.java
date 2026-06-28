package dev.vorstu.exception.student;

public class InvalidFioFormatException extends RuntimeException {
    public InvalidFioFormatException(String message) {
        super(message);
    }
}
