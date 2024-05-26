package br.com.users.user.presentation.exception;

import br.com.users.user.domain.exception.DuplicatedException;
import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.exception.ValidatorException;
import br.com.users.user.presentation.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(DuplicatedException.class)
  public ResponseEntity<?> handlerDuplicatedException(DuplicatedException exception) {
    var error = exception.getFieldError();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(error));
  }

  @ExceptionHandler(ValidatorException.class)
  public ResponseEntity<?> handlerValidatorException(ValidatorException exception) {
    var error = exception.getFieldError();
    return ResponseEntity.badRequest().body(new ErrorDto(error));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException methodArgumentNotValidException) {
    var errors = methodArgumentNotValidException.getFieldErrors();
    return ResponseEntity.badRequest().body(errors.stream().map(ErrorDto::new).toList());
  }

  @ExceptionHandler(NoResultException.class)
  public ResponseEntity<?> handlerNoResultException(NoResultException exception) {
    var error = exception.getFieldError();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(error));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handlerBadRequest(HttpMessageNotReadableException exception) {
    var error = new FieldError(HttpMessageNotReadableException.class.getSimpleName(), "",
        exception.getMessage());
    return ResponseEntity.badRequest().body(new ErrorDto(error));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handlerUnauthorized(AuthenticationException exception) {
    var error = new FieldError(AuthenticationException.class.getSimpleName(), "Authentication",
        exception.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(error));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handlerForbbiden(AccessDeniedException exception) {
    var error = new FieldError(AccessDeniedException.class.getSimpleName(), "",
        exception.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(error));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handlerInternalServerError(Exception ex) {
    var error = ex.getMessage();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
