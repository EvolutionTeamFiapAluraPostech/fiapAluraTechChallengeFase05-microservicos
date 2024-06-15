package br.com.fiap.company.application.usecase;

import br.com.fiap.company.application.validator.DocNumberExistsValidator;
import br.com.fiap.company.application.validator.DocNumberRequiredValidator;
import br.com.fiap.company.application.validator.DocNumberTypeValidator;
import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.service.CompanyService;
import br.com.fiap.company.infrastructure.httpclient.GetCoordinatesFromCepRequest;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateCompanyUseCase {

  public static final String LATITUDE = "Latitude";
  public static final String LONGITUDE = "Longitude";
  private final CompanyService companyService;
  private final DocNumberRequiredValidator docNumberRequiredValidator;
  private final DocNumberTypeValidator docNumberTypeValidator;
  private final DocNumberExistsValidator docNumberExistsValidator;
  private final GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;

  public CreateCompanyUseCase(CompanyService companyService,
      DocNumberRequiredValidator docNumberRequiredValidator,
      DocNumberTypeValidator docNumberTypeValidator,
      DocNumberExistsValidator docNumberExistsValidator,
      GetCoordinatesFromCepRequest getCoordinatesFromCepRequest) {
    this.companyService = companyService;
    this.docNumberRequiredValidator = docNumberRequiredValidator;
    this.docNumberTypeValidator = docNumberTypeValidator;
    this.docNumberExistsValidator = docNumberExistsValidator;
    this.getCoordinatesFromCepRequest = getCoordinatesFromCepRequest;
  }

  @Transactional
  public Company execute(Company company) {
    validateDocNumber(company);
    if (isNecessaryGettingCoordinates(company)) {
      getCoordinatesFromWebAndUpdateCompanyAddress(company);
    }
    return companyService.save(company);
  }

  private void getCoordinatesFromWebAndUpdateCompanyAddress(Company company) {
    var coordinates = getCoordinatesFromCepRequest.request(company.getPostalCode());
    if (!coordinates.isEmpty()) {
      coordinates.forEach(coordinate -> {
        if (coordinate.containsKey(LATITUDE)) {
          company.setLatitude(coordinate.get(LATITUDE));
        }
        if (coordinate.containsKey(LONGITUDE)) {
          company.setLongitude(coordinate.get(LONGITUDE));
        }
      });
    }
  }

  private boolean isNecessaryGettingCoordinates(Company company) {
    return StringUtils.hasLength(company.getPostalCode())
        && (company.getLatitude() == null || company.getLatitude().equals(BigDecimal.ZERO)
        || company.getLongitude() == null || company.getLongitude().equals(BigDecimal.ZERO));
  }

  private void validateDocNumber(Company company) {
    docNumberRequiredValidator.validate(company.getDocNumber(), company.getDocNumberType());
    docNumberTypeValidator.validate(company.getDocNumber(), company.getDocNumberType());
    docNumberExistsValidator.validate(company.getDocNumber());
  }
}
