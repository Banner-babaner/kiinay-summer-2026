package dev.vorstu.exception.group;

public class TeacherAlreadyTeachesHereException extends RuntimeException {
    public TeacherAlreadyTeachesHereException(String message) {
        super(message);
    }
}
