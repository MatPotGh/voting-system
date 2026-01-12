package com.example.voting.controller;

import com.example.voting.service.exceptions.EntityNotFoundException;
import com.example.voting.service.exceptions.InvalidOperationException;
import com.example.voting.service.exceptions.PeselAlreadyExistsException;
import com.example.voting.service.exceptions.VoterAlreadyVotedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(PeselAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handlePeselDuplicate(PeselAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(VoterAlreadyVotedException.class)
  public ResponseEntity<ErrorResponse> handleAlreadyVoted(VoterAlreadyVotedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(InvalidOperationException.class)
  public ResponseEntity<ErrorResponse> handleInvalid(InvalidOperationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String errorMsg = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .findFirst()
        .orElse("Validation failed");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(errorMsg));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    String errorMsg = ex.getConstraintViolations().stream()
        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
        .findFirst()
        .orElse("Validation failed");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(errorMsg));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("Unexpected exception: " + ex.getMessage()));
  }

  public record ErrorResponse(String message) {
  }
}
