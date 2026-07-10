package dev.vorstu.exception.invite;

import java.util.List;
import java.util.Objects;

public class DuplicateLoginException extends RuntimeException {
    public DuplicateLoginException(List<String> logins) {
        super(Objects.toString(logins));
    }
}
