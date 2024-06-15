package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_DOC_NUMBER_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.DOCUMENT_NUMBER_ALREADY_EXISTS_MESSAGE;

import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class DocNumberExistsValidator {

  private final CompanyService companyService;

  public DocNumberExistsValidator(CompanyService companyService) {
    this.companyService = companyService;
  }

  public void validate(String docNumber) {
    var docNumberExists = companyService.isCompanyDocNumberAlreadyExists(docNumber);
    if (docNumberExists) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(),
          COMPANY_DOC_NUMBER_FIELD,
          DOCUMENT_NUMBER_ALREADY_EXISTS_MESSAGE.formatted(docNumber)));
    }
  }
}
