package br.com.fiap.company.application.usecase;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.company.domain.messages.CompanyMessages.UUID_INVALID_MESSAGE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.createCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.company.application.validator.UuidValidator;
import br.com.fiap.company.domain.exception.NoResultException;
import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
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
class GetCompanyByIdUseCaseTest {

  @Mock
  private CompanyService companyService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private GetCompanyByIdUseCase getCompanyByIdUseCase;

  @Test
  void shouldGetCompanyById() {
    var company = createCompany();
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);

    var companyFound = getCompanyByIdUseCase.execute(company.getId().toString());

    assertThat(companyFound).isNotNull();
    assertThat(companyFound.getId()).isNotNull().isEqualTo(company.getId());
    verify(uuidValidator).validate(company.getId().toString());
  }

  @Test
  void shouldThrowNoResultExceptionWhenCompanyWasNotFoundById() {
    var company = createCompany();
    when(companyService.findByIdRequired(company.getId())).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            COMPANY_ID_FIELD, COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(company.getId()))));

    assertThatThrownBy(() -> getCompanyByIdUseCase.execute(company.getId().toString()))
        .isInstanceOf(NoResultException.class)
        .hasMessage(COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(company.getId()));
    verify(uuidValidator).validate(company.getId().toString());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1AB"})
  void shouldThrowExceptionWhenCompanyIdIsInvalid(String invalidUuid) {
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
            UUID_INVALID_MESSAGE.formatted(invalidUuid))))
        .when(uuidValidator)
        .validate(invalidUuid);

    assertThatThrownBy(() -> getCompanyByIdUseCase.execute(invalidUuid))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(UUID_INVALID_MESSAGE.formatted(invalidUuid));
  }
}
