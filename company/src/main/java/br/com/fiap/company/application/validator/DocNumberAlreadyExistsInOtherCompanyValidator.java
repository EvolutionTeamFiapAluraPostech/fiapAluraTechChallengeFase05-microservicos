package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_DOC_NUMBER_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.*;

import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class DocNumberAlreadyExistsInOtherCompanyValidator {

  private final CompanyService companyService;

  public DocNumberAlreadyExistsInOtherCompanyValidator(CompanyService companyService) {
    this.companyService = companyService;
  }

  public void validate(String docNumber, UUID companyId) {
    var optionalCompany = companyService.findByDocNumber(docNumber);
    if (optionalCompany.isPresent()) {
      var company = optionalCompany.get();
      if (!company.getId().equals(companyId)) {
        throw new ValidatorException(
            new FieldError(this.getClass().getSimpleName(), COMPANY_DOC_NUMBER_FIELD,
                DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE.formatted(docNumber)));
      }
    }
  }
}
