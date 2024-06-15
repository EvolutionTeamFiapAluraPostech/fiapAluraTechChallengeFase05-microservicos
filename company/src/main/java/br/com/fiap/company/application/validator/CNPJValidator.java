package br.com.fiap.company.application.validator;

import br.com.fiap.company.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class CNPJValidator {

  private static final String CNPJ_FIELD = "CNPJ";
  private static final String CNPJ_INVALID_MESSAGE = "Invalid CNPJ. %s";

  public void validate(String docNumber) {
    if (docNumber == null || docNumber.isBlank()) {
      throwInvalidCnpjException(docNumber);
    }
    validateCnpjDocNumber(docNumber);
  }

  private void validateCnpjDocNumber(String cnpj) {
    if (cnpj.trim().equals("00000000000000")) {
      throwInvalidCnpjException(cnpj);
    }
    cnpj = cnpj.trim().replace(".", "").replace("-", "").replace("/", "");
    if (cnpj.length() != 14) {
      throwInvalidCnpjException(cnpj);
    }

    final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    int digito1 = calculateDigit(cnpj.substring(0, 12), pesoCNPJ);
    int digito2 = calculateDigit(cnpj.substring(0, 12) + digito1, pesoCNPJ);
    if (!cnpj.equals(cnpj.substring(0, 12) + digito1 + digito2)) {
      throwInvalidCnpjException(cnpj);
    }
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

  private void throwInvalidCnpjException(String cnpjValue) {
    throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), CNPJ_FIELD,
        CNPJ_INVALID_MESSAGE.formatted(cnpjValue)));
  }
}
