package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_STATUS;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ALREADY_PAID;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderPaymentConfirmationValidator {

  public void validate(Order order) {
    if (order.getOrderStatus().equals(OrderStatus.PAGO)) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_STATUS,
          ORDER_ALREADY_PAID.formatted(order.getId())));
    }
  }
}
