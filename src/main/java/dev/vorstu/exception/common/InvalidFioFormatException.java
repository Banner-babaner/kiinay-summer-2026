package dev.vorstu.exception.common;

public class InvalidFioFormatException extends RuntimeException {
    public InvalidFioFormatException(String message) {
        super(message);
    }
}
