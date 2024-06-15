package br.com.fiap.company.application.usecase;

import static br.com.fiap.company.domain.enums.DocNumberType.CNPJ;
import static br.com.fiap.company.domain.enums.DocNumberType.CPF;
import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_DOC_NUMBER_FIELD;
import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE;
import static br.com.fiap.company.domain.messages.CompanyMessages.UUID_INVALID_MESSAGE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_LATITUDE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_LONGITUDE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.createCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.company.application.validator.DocNumberAlreadyExistsInOtherCompanyValidator;
import br.com.fiap.company.application.validator.DocNumberRequiredValidator;
import br.com.fiap.company.application.validator.DocNumberTypeValidator;
import br.com.fiap.company.application.validator.UuidValidator;
import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import br.com.fiap.company.infrastructure.httpclient.GetCoordinatesFromCepRequest;
import br.com.fiap.company.shared.testdata.CompanyTestData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class UpdateCompanyUseCaseTest {

  @Mock
  private CompanyService companyService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private DocNumberRequiredValidator docNumberRequiredValidator;
  @Mock
  private DocNumberTypeValidator docNumberTypeValidator;
  @Mock
  private DocNumberAlreadyExistsInOtherCompanyValidator docNumberAlreadyExistsInOtherCompanyValidator;
  @Mock
  private GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;
  @InjectMocks
  private UpdateCompanyUseCase updateCompanyUseCase;

  @Test
  void shouldUpdateCompany() {
    var company = createCompany();
    company.setName(CompanyTestData.ALTERNATIVE_COMPANY_NAME);
    company.setEmail(CompanyTestData.ALTERNATIVE_COMPANY_EMAIL);
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    when(companyService.save(company)).thenReturn(company);

    var companySaved = updateCompanyUseCase.execute(company.getId().toString(), company);

    assertThat(companySaved).isNotNull();
    assertThat(companySaved.getId()).isNotNull();
    assertThat(companySaved.getName()).isNotNull().isEqualTo(company.getName());
    assertThat(companySaved.getEmail()).isNotNull().isEqualTo(company.getEmail());
    verify(uuidValidator).validate(company.getId().toString());
    verify(docNumberRequiredValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberAlreadyExistsInOtherCompanyValidator).validate(company.getDocNumber(),
        company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
  }

  @Test
  void shouldCreateCompanyAndGetCoordinatesFromWeb() {
    var company = createCompany();
    company.setName(CompanyTestData.ALTERNATIVE_COMPANY_NAME);
    company.setEmail(CompanyTestData.ALTERNATIVE_COMPANY_EMAIL);
    company.setLongitude(null);
    company.setLongitude(null);
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    var coordinatesList = new ArrayList<Map<String, BigDecimal>>();
    var coordinates = new HashMap<String, BigDecimal>();
    coordinates.put("Latitude", DEFAULT_COMPANY_LATITUDE);
    coordinates.put("Longitude", DEFAULT_COMPANY_LONGITUDE);
    coordinatesList.add(coordinates);
    when(getCoordinatesFromCepRequest.request(company.getPostalCode())).thenReturn(coordinatesList);
    when(companyService.save(company)).thenReturn(company);

    var companySaved = updateCompanyUseCase.execute(company.getId().toString(), company);

    assertThat(companySaved).isNotNull();
    assertThat(companySaved.getId()).isNotNull();
    assertThat(companySaved.getName()).isNotNull().isEqualTo(company.getName());
    assertThat(companySaved.getEmail()).isNotNull().isEqualTo(company.getEmail());
    verify(uuidValidator).validate(company.getId().toString());
    verify(docNumberRequiredValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberAlreadyExistsInOtherCompanyValidator).validate(company.getDocNumber(),
        company.getId());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1Ab"})
  void shouldThrowExceptionWhenCompanyIdIsInvalid(String companyId) {
    var company = createCompany();
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
            UUID_INVALID_MESSAGE.formatted(companyId)))).when(uuidValidator)
        .validate(companyId);

    assertThatThrownBy(() -> updateCompanyUseCase.execute(companyId, company))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(UUID_INVALID_MESSAGE.formatted(companyId));

    verify(uuidValidator).validate(companyId);
    verify(companyService, never()).findByIdRequired(company.getId());
    verify(docNumberTypeValidator, never()).validate(company.getDocNumber(),
        company.getDocNumberType());
    verify(docNumberAlreadyExistsInOtherCompanyValidator, never()).validate(
        company.getDocNumber(), company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenCpfDocNumberWasNotFilled(String cpf) {
    var company = createCompany();
    company.setDocNumber(cpf);
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    doThrow(ValidatorException.class).when(docNumberRequiredValidator).validate(cpf, CPF);

    assertThatThrownBy(() -> updateCompanyUseCase.execute(company.getId().toString(), company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberTypeValidator, never()).validate(company.getDocNumber(),
        company.getDocNumberType());
    verify(docNumberAlreadyExistsInOtherCompanyValidator, never()).validate(
        company.getDocNumber(), company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenCnpjDocNumberWasNotFilled(String cnpj) {
    var company = createCompany();
    company.setDocNumber(cnpj);
    company.setDocNumberType(CNPJ);
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    doThrow(ValidatorException.class).when(docNumberRequiredValidator).validate(cnpj, CNPJ);

    assertThatThrownBy(() -> updateCompanyUseCase.execute(company.getId().toString(), company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberTypeValidator, never()).validate(company.getDocNumber(),
        company.getDocNumberType());
    verify(docNumberAlreadyExistsInOtherCompanyValidator, never()).validate(
        company.getDocNumber(), company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "12", "123", "1234", "12345", "123456", "1234567", "12345678",
      "123456789", "1234567890", "59059270000110"})
  void shouldThrowExceptionWhenCpfDocNumberIsInvalid(String docNumber) {
    var company = createCompany();
    company.setDocNumber(docNumber);
    company.setDocNumberType(CPF);
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    doThrow(ValidatorException.class)
        .when(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());

    assertThatThrownBy(() -> updateCompanyUseCase.execute(company.getId().toString(), company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberAlreadyExistsInOtherCompanyValidator, never()).validate(
        company.getDocNumber(), company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "12", "123", "1234", "12345", "123456", "1234567", "12345678",
      "123456789", "1234567890", "59059270000110"})
  void shouldThrowExceptionWhenCnpjDocNumberIsInvalid(String docNumber) {
    var company = createCompany();
    company.setDocNumber(docNumber);
    company.setDocNumberType(CNPJ);
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    doThrow(ValidatorException.class)
        .when(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());

    assertThatThrownBy(() -> updateCompanyUseCase.execute(company.getId().toString(), company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberAlreadyExistsInOtherCompanyValidator, never()).validate(
        company.getDocNumber(), company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @Test
  void shouldThrowExceptionWhenDocNumberAlreadyExists() {
    var company = createCompany();
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), COMPANY_DOC_NUMBER_FIELD,
            DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE.formatted(
                company.getDocNumber())))).when(docNumberAlreadyExistsInOtherCompanyValidator)
        .validate(company.getDocNumber(), company.getId());

    assertThatThrownBy(() -> updateCompanyUseCase.execute(company.getId().toString(), company))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE.formatted(
            company.getDocNumber()));

    verify(uuidValidator).validate(company.getId().toString());
    verify(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberAlreadyExistsInOtherCompanyValidator).validate(
        company.getDocNumber(), company.getId());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }
}
