package dev.vorstu.exception.mail;

public class InvalidErrorHandlerMode extends RuntimeException {
    public InvalidErrorHandlerMode(String message) {
        super(message);
    }
}
