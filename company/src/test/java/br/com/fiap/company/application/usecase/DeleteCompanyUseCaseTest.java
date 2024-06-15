package br.com.fiap.company.application.usecase;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.company.domain.messages.CompanyMessages.UUID_INVALID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.company.application.validator.UuidValidator;
import br.com.fiap.company.domain.exception.NoResultException;
import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import br.com.fiap.company.shared.testdata.CompanyTestData;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class DeleteCompanyUseCaseTest {

  @Mock
  private CompanyService companyService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private DeleteCompanyUseCase deleteCompanyUseCase;

  @Test
  void shouldDeleteCompany() {
    var company = CompanyTestData.createCompany();
    when(companyService.findByIdRequired(company.getId())).thenReturn(company);

    assertThatCode(() -> deleteCompanyUseCase.execute(company.getId().toString()))
        .doesNotThrowAnyException();
    verify(uuidValidator).validate(company.getId().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1Ab", "ABC"})
  void shouldThrowExceptionWhenCompanyIdIsInvalid(String companyId) {
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
            UUID_INVALID_MESSAGE.formatted(companyId)))).when(uuidValidator).validate(companyId);

    assertThatThrownBy(() -> deleteCompanyUseCase.execute(companyId))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(UUID_INVALID_MESSAGE.formatted(companyId));
    verify(companyService, never()).findByIdRequired(any(UUID.class));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenCompanyWasNotFoundById() {
    var id = UUID.randomUUID();
    doThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
            COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(id.toString()))))
        .when(companyService).findByIdRequired(id);

    assertThatThrownBy(() -> deleteCompanyUseCase.execute(id.toString()))
        .isInstanceOf(NoResultException.class)
        .hasMessage(COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(id.toString()));
    verify(uuidValidator).validate(id.toString());
  }
}
