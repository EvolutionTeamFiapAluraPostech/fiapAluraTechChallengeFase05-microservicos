package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_CUSTOMER_ID_IS_DIFFERENT_OF_AUTHENTICATED_USER_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.payment.domain.entity.User;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.security.UserFromSecurityContext;
import br.com.fiap.payment.shared.testdata.OrderTestData;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

@ExtendWith(MockitoExtension.class)
class UserFromSecurityContextIsTheSameOfOrderValidatorTest {

  @Mock
  public UserFromSecurityContext userFromSecurityContext;
  @InjectMocks
  private UserFromSecurityContextIsTheSameOfOrderValidator userFromSecurityContextIsTheSameOfOrderValidator;

  @Test
  void shouldValidateWhenOrderCustomerIdIsEqualToUserFromSecurityContextId() {
    var orderDto = OrderTestData.createOrderDto();
    var user = new User(orderDto.customerId(), "Name", "sub", "iss", 1L,
        new ArrayList<GrantedAuthority>());
    when(userFromSecurityContext.getUser()).thenReturn(user);

    assertThatCode(() -> userFromSecurityContextIsTheSameOfOrderValidator.validate(orderDto))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderCustomerIdIsDifferentOfUserFromSecurityContextId() {
    var orderDto = OrderTestData.createOrderDto();
    var user = new User(UUID.randomUUID().toString(), "Name", "sub", "iss", 1L,
        new ArrayList<GrantedAuthority>());
    when(userFromSecurityContext.getUser()).thenReturn(user);

    assertThatThrownBy(() -> userFromSecurityContextIsTheSameOfOrderValidator.validate(orderDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PAYMENT_ORDER_CUSTOMER_ID_IS_DIFFERENT_OF_AUTHENTICATED_USER_MESSAGE.formatted(
            orderDto.id(), orderDto.customerId(), user.getId()));
  }
}
