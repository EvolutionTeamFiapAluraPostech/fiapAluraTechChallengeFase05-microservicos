package br.com.fiap.company.presentation.api;

import br.com.fiap.company.application.usecase.CreateCompanyUseCase;
import br.com.fiap.company.application.usecase.DeleteCompanyUseCase;
import br.com.fiap.company.application.usecase.GetCompanyByIdUseCase;
import br.com.fiap.company.application.usecase.GetCompanyByNameOrEmailUseCase;
import br.com.fiap.company.application.usecase.UpdateCompanyUseCase;
import br.com.fiap.company.presentation.api.dto.CompanyFilter;
import br.com.fiap.company.presentation.api.dto.CompanyInputDto;
import br.com.fiap.company.presentation.api.dto.CompanyOutputDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompaniesController implements CompaniesApi {

  private final CreateCompanyUseCase createCompanyUseCase;
  private final GetCompanyByIdUseCase getCompanyByIdUseCase;
  private final UpdateCompanyUseCase updateCompanyUseCase;
  private final DeleteCompanyUseCase deleteCompanyUseCase;
  private final GetCompanyByNameOrEmailUseCase getCompanyByNameOrEmailUseCase;

  public CompaniesController(CreateCompanyUseCase createCompanyUseCase,
      GetCompanyByIdUseCase getCompanyByIdUseCase, UpdateCompanyUseCase updateCompanyUseCase,
      DeleteCompanyUseCase deleteCompanyUseCase,
      GetCompanyByNameOrEmailUseCase getCompanyByNameOrEmailUseCase) {
    this.createCompanyUseCase = createCompanyUseCase;
    this.getCompanyByIdUseCase = getCompanyByIdUseCase;
    this.updateCompanyUseCase = updateCompanyUseCase;
    this.deleteCompanyUseCase = deleteCompanyUseCase;
    this.getCompanyByNameOrEmailUseCase = getCompanyByNameOrEmailUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public CompanyOutputDto postCompany(@RequestBody @Valid CompanyInputDto companyInputDto) {
    var company = companyInputDto.from(companyInputDto);
    var companySaved = createCompanyUseCase.execute(company);
    return CompanyOutputDto.toCompanyOutputDtoFrom(companySaved);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public CompanyOutputDto getCompanyById(@PathVariable String id) {
    var company = getCompanyByIdUseCase.execute(id);
    return CompanyOutputDto.toCompanyOutputDtoFrom(company);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Override
  public CompanyOutputDto putCompany(@PathVariable String id,
      @RequestBody @Valid CompanyInputDto companyInputDto) {
    var company = companyInputDto.from(companyInputDto);
    var companySaved = updateCompanyUseCase.execute(id, company);
    return CompanyOutputDto.toCompanyOutputDtoFrom(companySaved);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void deleteCompany(@PathVariable String id) {
    deleteCompanyUseCase.execute(id);
  }

  @GetMapping("/name-email")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public Page<CompanyOutputDto> getCompaniesByNameOrEmail(CompanyFilter companyFilter,
      @PageableDefault(sort = {"name"}) Pageable pageable) {
    var companiesPage = getCompanyByNameOrEmailUseCase.execute(companyFilter.name(),
        companyFilter.email(), pageable);
    return !companiesPage.getContent().isEmpty() ? CompanyOutputDto.toPage(companiesPage)
        : Page.empty();
  }
}
