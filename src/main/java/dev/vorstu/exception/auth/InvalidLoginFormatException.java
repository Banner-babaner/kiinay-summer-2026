package dev.vorstu.exception.auth;

public class InvalidLoginFormatException extends RuntimeException {
    public InvalidLoginFormatException(String message) {
        super(message);
    }
}
