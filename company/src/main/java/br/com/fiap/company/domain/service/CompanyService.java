package br.com.fiap.company.domain.service;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.exception.NoResultException;
import br.com.fiap.company.infrastructure.repository.CompanyRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public Company save(Company company) {
    return companyRepository.save(company);
  }

  public Optional<Company> findByDocNumber(String docNumber) {
    return companyRepository.findByDocNumber(docNumber);
  }

  public boolean isCompanyDocNumberAlreadyExists(String docNumber) {
    return this.findByDocNumber(docNumber).isPresent();
  }

  public Company findByIdRequired(UUID uuid) {
    return companyRepository.findById(uuid).orElseThrow(
        () -> new NoResultException(
            new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
                COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(uuid.toString()))));
  }

  public Page<Company> queryCompaniesByNameLikeIgnoreCaseOrEmail(String name, String email,
      Pageable pageable) {
    return companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(name, email, pageable);
  }
}
