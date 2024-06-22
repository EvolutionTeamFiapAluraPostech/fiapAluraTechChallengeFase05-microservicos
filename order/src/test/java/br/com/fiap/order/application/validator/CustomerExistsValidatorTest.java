package br.com.fiap.order.application.validator;

import static br.com.fiap.order.infrastructure.httpclient.user.fields.UserFields.USER_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.user.messages.UserMessages.USER_NOT_FOUND_WITH_ID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.httpclient.user.UserClient;
import br.com.fiap.order.infrastructure.httpclient.user.dto.UserDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class CustomerExistsValidatorTest {

  @Mock
  private UserClient userClient;
  @InjectMocks
  private CustomerExistsValidator customerExistsValidator;

  @Test
  void shouldValidateCompanyIdWhenCompanyExists() {
    var customerId = UUID.randomUUID().toString();
    var customerDtoResponseEntity = new ResponseEntity<UserDto>(HttpStatus.OK);
    when(userClient.getUserById(customerId)).thenReturn(customerDtoResponseEntity);

    assertThatCode(() -> customerExistsValidator.validate(customerId)).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowNoResultExceptionWhenCompanyWasNotFoundById() {
    var companyId = UUID.randomUUID().toString();
    when(userClient.getUserById(companyId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            USER_ID_FIELD, USER_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId))));

    assertThatCode(() -> customerExistsValidator.validate(companyId))
        .isInstanceOf(NoResultException.class)
        .hasMessage(USER_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId));
  }
}