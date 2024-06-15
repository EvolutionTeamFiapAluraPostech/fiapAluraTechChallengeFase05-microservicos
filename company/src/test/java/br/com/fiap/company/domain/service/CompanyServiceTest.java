package br.com.fiap.company.domain.service;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.createCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.exception.NoResultException;
import br.com.fiap.company.infrastructure.repository.CompanyRepository;
import br.com.fiap.company.shared.testdata.CompanyTestData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;
  @Mock
  private CompanyRepository companyRepository;
  @InjectMocks
  private CompanyService companyService;

  @Test
  void shouldSaveCompany() {
    var company = CompanyTestData.createNewCompany();
    var companyWithId = CompanyTestData.createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    when(companyRepository.save(company)).thenReturn(companyWithId);

    var companySaved = companyService.save(company);

    assertThat(companySaved).isNotNull();
    assertThat(companySaved.getId()).isNotNull();
  }

  @Test
  void shouldFindCompanyByDocNumber() {
    var companyWithId = CompanyTestData.createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    when(companyRepository.findByDocNumber(companyWithId.getDocNumber())).thenReturn(
        Optional.of(companyWithId));

    var company = companyService.findByDocNumber(companyWithId.getDocNumber());

    assertThat(company).isPresent();
    assertThat(company.get().getId()).isNotNull().isEqualTo(companyWithId.getId());
  }

  @Test
  void shouldReturnEmptyWhenNotFindCompanyByDocNumber() {
    var companyWithId = CompanyTestData.createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    when(companyRepository.findByDocNumber(companyWithId.getDocNumber())).thenReturn(
        Optional.empty());

    var company = companyService.findByDocNumber(companyWithId.getDocNumber());

    assertThat(company).isNotPresent();
  }

  @Test
  void shouldReturnTrueWhenCompanyDocNumberAlreadyExists() {
    var companyWithId = CompanyTestData.createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    when(companyRepository.findByDocNumber(companyWithId.getDocNumber())).thenReturn(
        Optional.of(companyWithId));

    var isCompanyAlreadyExists = companyService.isCompanyDocNumberAlreadyExists(
        companyWithId.getDocNumber());

    assertThat(isCompanyAlreadyExists).isTrue();
  }

  @Test
  void shouldReturnFalseWhenCompanyDocNumberDoesNotAlreadyExist() {
    var companyWithId = CompanyTestData.createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    when(companyRepository.findByDocNumber(companyWithId.getDocNumber())).thenReturn(
        Optional.empty());

    var isCompanyAlreadyExists = companyService.isCompanyDocNumberAlreadyExists(
        companyWithId.getDocNumber());

    assertThat(isCompanyAlreadyExists).isFalse();
  }

  @Test
  void shouldFindCompanyById() {
    var company = CompanyTestData.createNewCompany();
    company.setId(UUID.randomUUID());
    when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));

    var companyFound = companyService.findByIdRequired(company.getId());

    assertThat(companyFound).isNotNull();
    assertThat(companyFound.getId()).isNotNull().isEqualTo(company.getId());
  }

  @Test
  void shouldThrowExceptionWhenCompanyWasNotFoundById() {
    var company = CompanyTestData.createNewCompany();
    company.setId(UUID.randomUUID());
    when(companyRepository.findById(company.getId())).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
            COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(company.getId()))));

    assertThatThrownBy(() -> companyService.findByIdRequired(company.getId()))
        .isInstanceOf(NoResultException.class)
        .hasMessage(COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(company.getId()));
  }

  @Test
  void shouldFindCompanyByNameAndEmailWhenCompanyExists() {
    var company = createCompany();
    var companyName = company.getName();
    var companyEmail = company.getEmail();
    var companies = List.of(company);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = companies.size();
    var page = new PageImpl<>(companies, pageable, size);

    when(companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName,
        companyEmail, pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindCompanyByNameWhenCompanyExists() {
    var company = createCompany();
    var companyName = company.getName();
    var companyEmail = "";
    var companies = List.of(company);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = companies.size();
    var page = new PageImpl<>(companies, pageable, size);

    when(companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName,
        companyEmail, pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindCompanyByEmailWhenCompanyExists() {
    var company = createCompany();
    var companyName = "";
    var companyEmail = company.getEmail();
    var companies = List.of(company);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = companies.size();
    var page = new PageImpl<>(companies, pageable, size);

    when(companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName,
        companyEmail, pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetNothingWhenCompanyWasNotFoundByName() {
    var company = createCompany();
    var companyName = company.getName();
    var companyEmail = "";
    var companies = new ArrayList<Company>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(companies, pageable, size);

    when(companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName,
        companyEmail, pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetNothingWhenCompanyWasNotFoundByEmail() {
    var company = createCompany();
    var companyName = "";
    var companyEmail = company.getEmail();
    var companies = new ArrayList<Company>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(companies, pageable, size);

    when(companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName,
        companyEmail, pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetNothingWhenCompanyWasNotFoundByNameAndEmail() {
    var company = createCompany();
    var companyName = company.getName();
    var companyEmail = company.getEmail();
    var companies = new ArrayList<Company>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(companies, pageable, size);

    when(companyRepository.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName,
        companyEmail, pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }
}
