package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_CPF_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.CPF_INVALID_MESSAGE;

import br.com.fiap.company.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class CPFValidator {

  public void validate(String docNumber) {
    validateCpfDocNumber(docNumber);
  }

  private void validateCpfDocNumber(String cpf) {
    validateCpfNull(cpf);
    cpf = cpf.trim().replace(".", "").replace("-", "");
    validateCpfLength(cpf);
    validateCpfAllNumbersEquals(cpf);

    final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    var digito1 = calculateDigit(cpf.substring(0, 9), pesoCPF);
    var digito2 = calculateDigit(cpf.substring(0, 9) + digito1, pesoCPF);
    if (!cpf.equals(cpf.substring(0, 9) + digito1 + digito2)) {
      throwInvalidCpfException(cpf);
    }
  }

  private void validateCpfAllNumbersEquals(String cpf) {
    if (cpf.matches("^(0{11}|1{11}|2{11}|3{11}|4{11}|5{11}|6{11}|7{11}|8{11}|9{11})$")) {
      throwInvalidCpfException(cpf);
    }
  }

  private void validateCpfNull(String cpf) {
    if (cpf == null || cpf.isBlank()) {
      throwInvalidCpfException("null or empty");
    }
  }

  private void validateCpfLength(String cpf) {
    if (cpf.length() != 11) {
      throwInvalidCpfException(cpf);
    }
  }

  private void throwInvalidCpfException(String cnpjValue) {
    throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), COMPANY_CPF_FIELD,
        CPF_INVALID_MESSAGE.formatted(cnpjValue)));
  }

  private int calculateDigit(String str, int[] weight) {
    var sum = 0;
    for (int index = str.length() - 1, digit; index >= 0; index--) {
      digit = Integer.parseInt(str.substring(index, index + 1));
      sum += digit * weight[weight.length - str.length() + index];
    }
    sum = 11 - sum % 11;
    return sum > 9 ? 0 : sum;
  }
}
