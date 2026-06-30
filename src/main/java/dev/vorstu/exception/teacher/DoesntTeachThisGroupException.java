package dev.vorstu.exception.teacher;

public class DoesntTeachThisGroupException extends RuntimeException {
    public DoesntTeachThisGroupException(String message) {
        super(message);
    }
}
