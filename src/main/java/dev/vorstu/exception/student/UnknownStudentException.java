package dev.vorstu.exception.student;

public class UnknownStudentException extends RuntimeException {
    public UnknownStudentException(String message) {
        super(message);
    }
}
