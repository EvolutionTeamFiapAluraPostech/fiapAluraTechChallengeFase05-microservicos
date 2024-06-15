package br.com.fiap.company.application.usecase;

import br.com.fiap.company.application.validator.UuidValidator;
import br.com.fiap.company.domain.service.CompanyService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCompanyUseCase {

  private final CompanyService companyService;
  private final UuidValidator uuidValidator;

  public DeleteCompanyUseCase(CompanyService companyService, UuidValidator uuidValidator) {
    this.companyService = companyService;
    this.uuidValidator = uuidValidator;
  }

  @Transactional
  public void execute(String uuid) {
    uuidValidator.validate(uuid);
    var company = companyService.findByIdRequired(UUID.fromString(uuid));
    company.setDeleted(true);
    companyService.save(company);
  }
}
