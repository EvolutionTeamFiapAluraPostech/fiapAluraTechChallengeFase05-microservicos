package br.com.fiap.payment.presentation.api.dto;

import org.springframework.validation.FieldError;

public record ErrorDto(
    String field,
    String message) {

  public ErrorDto(FieldError fieldError) {
    this(fieldError.getField(), fieldError.getDefaultMessage());
  }
}
