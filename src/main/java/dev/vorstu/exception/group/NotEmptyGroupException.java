package dev.vorstu.exception.group;

public class NotEmptyGroupException extends RuntimeException {
    public NotEmptyGroupException(String message) {
        super(message);
    }
}
