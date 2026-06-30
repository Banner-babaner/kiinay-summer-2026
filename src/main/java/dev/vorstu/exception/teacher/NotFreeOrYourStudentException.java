package dev.vorstu.exception.teacher;

public class NotFreeOrYourStudentException extends RuntimeException {
    public NotFreeOrYourStudentException(String message) {
        super(message);
    }
}
