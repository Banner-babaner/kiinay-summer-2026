package dev.vorstu.exception;

import dev.vorstu.exception.auth.InvalidTokenException;
import dev.vorstu.exception.auth.InvalidTokenTypeException;
import dev.vorstu.exception.group.GroupNotFoundException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.teacher.TeacherNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class Advice {
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(StudentNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeacherNotFound(TeacherNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGroupNotFound(GroupNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenType(InvalidTokenTypeException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
    }

    public record ErrorResponse(String message, int status, LocalDateTime timestamp) {}
}
