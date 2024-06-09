package br.com.users.user.domain.valueobject;

import br.com.users.user.domain.exception.ValidatorException;
import org.springframework.validation.FieldError;

public class CnpjNumber {

  private static final String CNPJ_FIELD = "CNPJ";
  private static final String CNPJ_INVALID = "Invalid CNPJ %s.";

  private final String cnpjValue;

  public CnpjNumber(String cnpjValue) {
    validateCnpjIsNullOrEmpty(cnpjValue);
    isValidCNPJ(cnpjValue);
    this.cnpjValue = cnpjValue.trim();
  }

  public String getCnpjValue() {
    return cnpjValue;
  }

  private void validateCnpjIsNullOrEmpty(String cnpjValue) {
    if ((cnpjValue == null) || (cnpjValue.trim().isEmpty())) {
      throwInvalidCnpjException(cnpjValue);
    }
  }

  private void isValidCNPJ(String cnpj) {
    if (cnpj.trim().equals("00000000000000")) {
      throwInvalidCnpjException(cnpj);
    }
    final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    cnpj = cnpj.trim().replace(".", "").replace("-", "").replace("/", "");
    if (cnpj.length() != 14) {
      throwInvalidCnpjException(cnpj);
    }

    int digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ);
    int digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ);
    if (!cnpj.equals(cnpj.substring(0, 12) + digito1 + digito2)) {
      throwInvalidCnpjException(cnpj);
    }
  }

  private int calcularDigito(String str, int[] peso) {
    int soma = 0;
    for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
      digito = Integer.parseInt(str.substring(indice, indice + 1));
      soma += digito * peso[peso.length - str.length() + indice];
    }
    soma = 11 - soma % 11;
    return soma > 9 ? 0 : soma;
  }

  private void throwInvalidCnpjException(String cnpjValue) {
    throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), CNPJ_FIELD,
        CNPJ_INVALID.formatted(cnpjValue)));
  }
}
