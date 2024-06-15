package br.com.fiap.company.application.usecase;

import static br.com.fiap.company.shared.testdata.CompanyTestData.createCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.service.CompanyService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GetCompanyByNameOrEmailUseCaseTest {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;
  @Mock
  private CompanyService companyService;
  @InjectMocks
  private GetCompanyByNameOrEmailUseCase getCompanyByNameOrEmailUseCase;

  @Test
  void shouldGetCompanyByName() {
    var company = createCompany();
    var companyName = company.getName();
    var companyEmail = "";
    var companies = List.of(company);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = companies.size();
    var page = new PageImpl<>(companies, pageable, size);

    when(companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = getCompanyByNameOrEmailUseCase.execute(companyName, companyEmail,
        pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetCompanyByEmail() {
    var company = createCompany();
    var companyName = "";
    var companyEmail = company.getEmail();
    var companies = List.of(company);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = companies.size();
    var page = new PageImpl<>(companies, pageable, size);

    when(companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = getCompanyByNameOrEmailUseCase.execute(companyName, companyEmail,
        pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetCompanyByNameAndEmail() {
    var company = createCompany();
    var companyName = company.getName();
    var companyEmail = company.getEmail();
    var companies = List.of(company);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = companies.size();
    var page = new PageImpl<>(companies, pageable, size);

    when(companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = getCompanyByNameOrEmailUseCase.execute(companyName, companyEmail,
        pageable);

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

    when(companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = getCompanyByNameOrEmailUseCase.execute(companyName, companyEmail,
        pageable);

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

    when(companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = getCompanyByNameOrEmailUseCase.execute(companyName, companyEmail,
        pageable);

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

    when(companyService.queryCompaniesByNameLikeIgnoreCaseOrEmail(companyName, companyEmail,
        pageable)).thenReturn(page);
    var companiesFound = getCompanyByNameOrEmailUseCase.execute(companyName, companyEmail,
        pageable);

    assertThat(companiesFound).isNotNull();
    assertThat(companiesFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(companiesFound.getTotalPages()).isEqualTo(size);
    assertThat(companiesFound.getTotalElements()).isEqualTo(size);
  }
}
