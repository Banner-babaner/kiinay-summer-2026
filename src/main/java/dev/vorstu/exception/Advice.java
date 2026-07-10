package dev.vorstu.exception;

import dev.vorstu.exception.auth.*;
import dev.vorstu.exception.common.InvalidFioFormatException;
import dev.vorstu.exception.common.InvalidPhoneNumberException;
import dev.vorstu.exception.group.*;
import dev.vorstu.exception.invite.IllegalInviteRoleException;
import dev.vorstu.exception.student.StudentAlreadyHasAccountException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.teacher.TeacherAlreadyHasAccountException;
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

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPhoneNumber(InvalidPhoneNumberException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFioFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFioFormat(InvalidFioFormatException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEmptyGroupException.class)
    public ResponseEntity<ErrorResponse> handleNotEmptyGroup(NotEmptyGroupException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(StudentAlreadyHasAccountException.class)
    public ResponseEntity<ErrorResponse> handleStudentAlreadyHasAccount(StudentAlreadyHasAccountException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StudentAlreadyPresentsException.class)
    public ResponseEntity<ErrorResponse> handleStudentAlreadyPresents(StudentAlreadyPresentsException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TeacherAlreadyHasAccountException.class)
    public ResponseEntity<ErrorResponse> handleTeacherAlreadyHasAccount(TeacherAlreadyHasAccountException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateLoginException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLogin(DuplicateLoginException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateGroupNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateGroupName(DuplicateGroupNameException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPasswordFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordFormat(InvalidPasswordFormatException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLoginFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLoginFormat(InvalidLoginFormatException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(dev.vorstu.exception.invite.DuplicateLoginException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLogin(dev.vorstu.exception.invite.DuplicateLoginException e){
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(TeacherAlreadyTeachesHereException.class)
    public ResponseEntity<ErrorResponse> handleTeacherAlreadyTeachesHere(TeacherAlreadyTeachesHereException e){
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    // DEFAULT
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleDefault(RuntimeException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
    }

    public record ErrorResponse(String message, int status, LocalDateTime timestamp) {}
}
