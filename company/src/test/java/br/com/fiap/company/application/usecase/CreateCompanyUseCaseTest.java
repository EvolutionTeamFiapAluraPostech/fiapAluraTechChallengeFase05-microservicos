package br.com.fiap.company.application.usecase;

import static br.com.fiap.company.domain.enums.DocNumberType.CNPJ;
import static br.com.fiap.company.domain.enums.DocNumberType.CPF;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CNPJ_DOC_NUMBER;
import static br.com.fiap.company.shared.testdata.CompanyTestData.createNewCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.company.application.validator.DocNumberExistsValidator;
import br.com.fiap.company.application.validator.DocNumberRequiredValidator;
import br.com.fiap.company.application.validator.DocNumberTypeValidator;
import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import br.com.fiap.company.infrastructure.httpclient.GetCoordinatesFromCepRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCompanyUseCaseTest {

  @Mock
  private CompanyService companyService;
  @Mock
  private DocNumberRequiredValidator docNumberRequiredValidator;
  @Mock
  private DocNumberTypeValidator docNumberTypeValidator;
  @Mock
  private DocNumberExistsValidator docNumberExistsValidator;
  @Mock
  private GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;
  @InjectMocks
  private CreateCompanyUseCase createCompanyUseCase;

  @Test
  void shouldCreateCompany() {
    var company = createNewCompany();
    var companyWithId = createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    when(companyService.save(company)).thenReturn(companyWithId);

    var companySaved = createCompanyUseCase.execute(company);

    assertThat(companySaved).isNotNull();
    assertThat(companySaved.getId()).isNotNull();
    verify(docNumberRequiredValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberExistsValidator).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
  }

  @Test
  void shouldCreateCompanyAndGetCoordinatesFromWeb() {
    var company = createNewCompany();
    company.setLatitude(null);
    company.setLongitude(null);
    var companyWithId = createNewCompany();
    companyWithId.setId(UUID.randomUUID());
    var coordinatesList = new ArrayList<Map<String, BigDecimal>>();
    var coordinates = new HashMap<String, BigDecimal>();
    coordinates.put("Latitude", companyWithId.getLatitude());
    coordinates.put("Longitude", companyWithId.getLongitude());
    coordinatesList.add(coordinates);
    when(getCoordinatesFromCepRequest.request(company.getPostalCode())).thenReturn(coordinatesList);
    when(companyService.save(company)).thenReturn(companyWithId);

    var companySaved = createCompanyUseCase.execute(company);

    assertThat(companySaved).isNotNull();
    assertThat(companySaved.getId()).isNotNull();
    verify(docNumberRequiredValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberExistsValidator).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest).request(anyString());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenCpfDocNumberWasNotFilled(String cpf) {
    var company = createNewCompany();
    company.setDocNumber(cpf);
    doThrow(ValidatorException.class).when(docNumberRequiredValidator).validate(cpf, CPF);

    assertThatThrownBy(() -> createCompanyUseCase.execute(company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberTypeValidator, never()).validate(company.getDocNumber(),
        company.getDocNumberType());
    verify(docNumberExistsValidator, never()).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenCnpjDocNumberWasNotFilled(String cnpj) {
    var company = createNewCompany();
    company.setDocNumber(cnpj);
    company.setDocNumberType(CNPJ);
    doThrow(ValidatorException.class).when(docNumberRequiredValidator).validate(cnpj, CNPJ);

    assertThatThrownBy(() -> createCompanyUseCase.execute(company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberTypeValidator, never()).validate(company.getDocNumber(),
        company.getDocNumberType());
    verify(docNumberExistsValidator, never()).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "12", "123", "1234", "12345", "123456", "1234567", "12345678",
      "123456789", "1234567890", "59059270000110"})
  void shouldThrowExceptionWhenCpfDocNumberIsInvalid(String docNumber) {
    var company = createNewCompany();
    company.setDocNumber(docNumber);
    company.setDocNumberType(CPF);
    doThrow(ValidatorException.class)
        .when(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());

    assertThatThrownBy(() -> createCompanyUseCase.execute(company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberExistsValidator, never()).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "12", "123", "1234", "12345", "123456", "1234567", "12345678",
      "123456789", "1234567890", "59059270000110"})
  void shouldThrowExceptionWhenCnpjDocNumberIsInvalid(String docNumber) {
    var company = createNewCompany();
    company.setDocNumber(docNumber);
    company.setDocNumberType(CNPJ);
    doThrow(ValidatorException.class)
        .when(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());

    assertThatThrownBy(() -> createCompanyUseCase.execute(company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberExistsValidator, never()).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }

  @Test
  void shouldThrowExceptionWhenDocNumberAlreadyExists() {
    var company = createNewCompany();
    company.setDocNumber(DEFAULT_COMPANY_CNPJ_DOC_NUMBER);
    company.setDocNumberType(CNPJ);
    doThrow(ValidatorException.class).when(docNumberExistsValidator).validate(
        company.getDocNumber());

    assertThatThrownBy(() -> createCompanyUseCase.execute(company))
        .isInstanceOf(ValidatorException.class);

    verify(docNumberTypeValidator).validate(company.getDocNumber(), company.getDocNumberType());
    verify(docNumberExistsValidator).validate(company.getDocNumber());
    verify(getCoordinatesFromCepRequest, never()).request(anyString());
    verify(companyService, never()).save(company);
  }
}
