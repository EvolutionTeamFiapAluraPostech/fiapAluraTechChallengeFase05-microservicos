package br.com.fiap.company.application.usecase;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetCompanyByNameOrEmailUseCase {

  private final CompanyService companyService;

  public GetCompanyByNameOrEmailUseCase(CompanyService companyService) {
    this.companyService = companyService;
  }

  public Page<Company> execute(String name, String email, Pageable pageable) {
    return companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(name, email, pageable);
  }
}
