package br.com.fiap.company.application.validator;

import br.com.fiap.company.domain.enums.DocNumberType;
import org.springframework.stereotype.Component;

@Component
public class DocNumberTypeValidator {

  private final CPFValidator cpfValidator;
  private final CNPJValidator cnpjValidator;

  public DocNumberTypeValidator(CPFValidator cpfValidator, CNPJValidator cnpjValidator) {
    this.cpfValidator = cpfValidator;
    this.cnpjValidator = cnpjValidator;
  }

  public void validate(String docNumber, DocNumberType docNumberType) {
    if (DocNumberType.CNPJ.equals(docNumberType)) {
      cnpjValidator.validate(docNumber);
    } else {
      cpfValidator.validate(docNumber);
    }
  }
}
