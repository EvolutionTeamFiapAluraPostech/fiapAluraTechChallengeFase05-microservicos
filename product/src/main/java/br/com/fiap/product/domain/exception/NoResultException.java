package br.com.fiap.product.domain.exception;

import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class NoResultException extends RuntimeException {

  private final FieldError fieldError;

  public NoResultException(FieldError fieldError) {
    super(fieldError.getDefaultMessage());
    this.fieldError = fieldError;
  }
}
