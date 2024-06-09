package br.com.users.user.domain.valueobject;

import br.com.users.user.domain.exception.ValidatorException;
import org.springframework.validation.FieldError;

public class CpfNumber {

  private static final String CPF_FIELD = "CPF";
  private static final String CPF_INVALID = "Invalid CPF %s.";
  private final String cpfNumber;

  public CpfNumber(String cpfNumber) {
    this.validate(cpfNumber);
    this.cpfNumber = cpfNumber;
  }

  public String getCpfNumber() {
    return cpfNumber;
  }

  private void validate(String rawCpf) {
    if (rawCpf == null || rawCpf.isEmpty()) {
      throwCpfInvalidException(rawCpf);
    }
    String cpf = removeNonDigits(rawCpf);
    if (isInvalidLength(cpf)) {
      throwCpfInvalidException(cpf);
    }
    if (hasAllDigitsEqual(cpf)) {
      throwCpfInvalidException(cpf);
    }
    int digit1 = calculateDigit(cpf, 10);
    int digit2 = calculateDigit(cpf, 11);
    if (!extractDigit(cpf).equals(String.valueOf(digit1) + digit2)) {
      throwCpfInvalidException(cpf);
    }
  }

  private void throwCpfInvalidException(String cpf) {
    throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), CPF_FIELD,
        CPF_INVALID.formatted(cpf)));
  }

  private static String removeNonDigits(String cpf) {
    return cpf.replaceAll("\\D", "");
  }

  private static boolean isInvalidLength(String cpf) {
    final int CPF_LENGTH = 11;
    return cpf.length() != CPF_LENGTH;
  }

  private static boolean hasAllDigitsEqual(String cpf) {
    char firstCpfDigit = cpf.charAt(0);
    return cpf.chars().allMatch(digit -> digit == firstCpfDigit);
  }

  private static int calculateDigit(String cpf, int factor) {
    int total = 0;
    for (char digit : cpf.toCharArray()) {
      if (factor > 1) {
        total += Character.getNumericValue(digit) * factor--;
      }
    }
    int rest = total % 11;
    return (rest < 2) ? 0 : 11 - rest;
  }

  private static String extractDigit(String cpf) {
    return cpf.substring(9);
  }
}
