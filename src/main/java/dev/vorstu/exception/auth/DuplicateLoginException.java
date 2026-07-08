package dev.vorstu.exception.auth;

import java.util.List;
import java.util.Objects;

public class DuplicateLoginException extends RuntimeException {
    public DuplicateLoginException(String message) {
        super(message);
    }
    public DuplicateLoginException(List<String> logins) {
        super(Objects.toString(logins));
    }
}
