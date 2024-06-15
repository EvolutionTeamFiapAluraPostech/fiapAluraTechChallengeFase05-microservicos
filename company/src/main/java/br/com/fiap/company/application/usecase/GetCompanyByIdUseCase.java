package br.com.fiap.company.application.usecase;

import br.com.fiap.company.application.validator.UuidValidator;
import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.service.CompanyService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCompanyByIdUseCase {

  private final CompanyService companyService;
  private final UuidValidator uuidValidator;

  public GetCompanyByIdUseCase(CompanyService companyService, UuidValidator uuidValidator) {
    this.companyService = companyService;
    this.uuidValidator = uuidValidator;
  }

  public Company execute(String id) {
    uuidValidator.validate(id);
    return companyService.findByIdRequired(UUID.fromString(id));
  }
}
