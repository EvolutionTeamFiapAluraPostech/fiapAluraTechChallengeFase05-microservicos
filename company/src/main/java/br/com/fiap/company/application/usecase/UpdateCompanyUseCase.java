package br.com.fiap.company.application.usecase;

import br.com.fiap.company.application.validator.DocNumberAlreadyExistsInOtherCompanyValidator;
import br.com.fiap.company.application.validator.DocNumberRequiredValidator;
import br.com.fiap.company.application.validator.DocNumberTypeValidator;
import br.com.fiap.company.application.validator.UuidValidator;
import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.service.CompanyService;
import br.com.fiap.company.infrastructure.httpclient.GetCoordinatesFromCepRequest;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UpdateCompanyUseCase {

  public static final String LATITUDE = "Latitude";
  public static final String LONGITUDE = "Longitude";
  private final CompanyService companyService;
  private final UuidValidator uuidValidator;
  private final DocNumberRequiredValidator docNumberRequiredValidator;
  private final DocNumberTypeValidator docNumberTypeValidator;
  private final DocNumberAlreadyExistsInOtherCompanyValidator docNumberAlreadyExistsInOtherCompanyValidator;
  private final GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;

  public UpdateCompanyUseCase(CompanyService companyService, UuidValidator uuidValidator,
      DocNumberRequiredValidator docNumberRequiredValidator,
      DocNumberTypeValidator docNumberTypeValidator,
      DocNumberAlreadyExistsInOtherCompanyValidator docNumberAlreadyExistsInOtherCompanyValidator,
      GetCoordinatesFromCepRequest getCoordinatesFromCepRequest) {
    this.companyService = companyService;
    this.uuidValidator = uuidValidator;
    this.docNumberRequiredValidator = docNumberRequiredValidator;
    this.docNumberTypeValidator = docNumberTypeValidator;
    this.docNumberAlreadyExistsInOtherCompanyValidator = docNumberAlreadyExistsInOtherCompanyValidator;
    this.getCoordinatesFromCepRequest = getCoordinatesFromCepRequest;
  }

  @Transactional
  public Company execute(String id, Company company) {
    uuidValidator.validate(id);
    company.setId(UUID.fromString(id));
    var companyFound = companyService.findByIdRequired(UUID.fromString(id));
    validateDocNumber(company);
    if (isNecessaryGettingCoordinates(company)) {
      getCoordinatesFromWebAndUpdateCompanyAddress(company);
    }
    updateCompanyFoundAttributesToSave(companyFound, company);
    return companyService.save(companyFound);
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

  private void updateCompanyFoundAttributesToSave(Company companyFound, Company company) {
    companyFound.setName(company.getName());
    companyFound.setEmail(company.getEmail());
    companyFound.setDocNumber(company.getDocNumber());
    companyFound.setDocNumberType(company.getDocNumberType());
    companyFound.setStreet(company.getStreet());
    companyFound.setNumber(company.getNumber());
    companyFound.setNeighborhood(company.getNeighborhood());
    companyFound.setCity(company.getCity());
    companyFound.setState(company.getState());
    companyFound.setCountry(company.getCountry());
    companyFound.setPostalCode(company.getPostalCode());
    companyFound.setLatitude(company.getLatitude());
    companyFound.setLongitude(company.getLongitude());
  }

  private void validateDocNumber(Company company) {
    docNumberRequiredValidator.validate(company.getDocNumber(), company.getDocNumberType());
    docNumberTypeValidator.validate(company.getDocNumber(), company.getDocNumberType());
    docNumberAlreadyExistsInOtherCompanyValidator.validate(company.getDocNumber(),
        company.getId());
  }
}
