package dev.vorstu.exception.auth;

public class DuplicateLoginException extends RuntimeException {
    public DuplicateLoginException(String message) {
        super(message);
    }
}
