package dev.vorstu.exception.group;

public class DuplicateGroupNameException extends RuntimeException {
    public DuplicateGroupNameException(String message) {
        super(message);
    }
}
