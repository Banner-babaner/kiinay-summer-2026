package dev.vorstu.exception.teacher;

public class TeacherAlreadyHasAccountException extends RuntimeException {
    public TeacherAlreadyHasAccountException(String message) {
        super(message);
    }
}
