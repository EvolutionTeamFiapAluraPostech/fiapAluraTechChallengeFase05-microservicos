package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.enums.OrderStatus.ENTREGUE;
import static br.com.fiap.order.domain.fields.OrderFields.ORDER_STATUS;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ALREADY_DELIVERED;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderAlreadyDeliveredValidator {

  public void validate(Order order) {
    if (order.getOrderStatus().equals(ENTREGUE)) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_STATUS,
          ORDER_ALREADY_DELIVERED.formatted(order.getId())));
    }
  }
}
