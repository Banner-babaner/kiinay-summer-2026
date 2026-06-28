package dev.vorstu.exception.auth;

public class NotFoundedLoginOrPasswordException extends RuntimeException {
    public NotFoundedLoginOrPasswordException(String message) {
        super(message);
    }
}
