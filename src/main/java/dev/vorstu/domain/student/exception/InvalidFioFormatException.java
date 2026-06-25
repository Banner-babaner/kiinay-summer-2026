package dev.vorstu.domain.student.exception;

public class InvalidFioFormatException extends RuntimeException {
  public InvalidFioFormatException(String message) {
    super(message);
  }
}
