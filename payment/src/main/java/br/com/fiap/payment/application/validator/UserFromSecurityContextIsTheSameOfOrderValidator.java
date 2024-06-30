package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_CUSTOMER_ID_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_CUSTOMER_ID_IS_DIFFERENT_OF_AUTHENTICATED_USER_MESSAGE;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.security.UserFromSecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UserFromSecurityContextIsTheSameOfOrderValidator {

  public final UserFromSecurityContext userFromSecurityContext;

  public UserFromSecurityContextIsTheSameOfOrderValidator(
      UserFromSecurityContext userFromSecurityContext) {
    this.userFromSecurityContext = userFromSecurityContext;
  }

  public void validate(OrderDto orderDto) {
    var user = userFromSecurityContext.getUser();
    if (!user.getId().equals(orderDto.customerId())) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), PAYMENT_ORDER_CUSTOMER_ID_FIELD,
              PAYMENT_ORDER_CUSTOMER_ID_IS_DIFFERENT_OF_AUTHENTICATED_USER_MESSAGE.formatted(
                  orderDto.id(), orderDto.customerId(), user.getId())));
    }
  }
}
