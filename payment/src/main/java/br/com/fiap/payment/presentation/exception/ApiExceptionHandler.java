package br.com.fiap.payment.presentation.exception;

import br.com.fiap.payment.domain.exception.DuplicatedException;
import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.presentation.api.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handlerInternalServerError(Exception ex) {
    var error = ex.getMessage();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
