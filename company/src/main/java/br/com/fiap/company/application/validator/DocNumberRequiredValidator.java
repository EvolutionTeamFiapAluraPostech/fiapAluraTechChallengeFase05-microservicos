package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_DOC_NUMBER_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.ENTER_DOCUMENT_NUMBER_MESSAGE;

import br.com.fiap.company.domain.enums.DocNumberType;
import br.com.fiap.company.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class DocNumberRequiredValidator {

  public void validate(String docNumber, DocNumberType docNumberType) {
    if (docNumber == null || docNumber.isBlank() || docNumberType == null) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(),
          COMPANY_DOC_NUMBER_FIELD,
          ENTER_DOCUMENT_NUMBER_MESSAGE));
    }
  }
}
