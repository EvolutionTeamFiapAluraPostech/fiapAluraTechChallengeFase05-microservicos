package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_STATUS;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_PAID_TO_DELIVER;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderAlreadyPaidToDeliverValidator {

  public void validate(Order order) {
    if (!order.getOrderStatus().equals(OrderStatus.PAGO) && !order.getOrderStatus()
        .equals(OrderStatus.AGUARDANDO_ENTREGA)) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_STATUS,
          ORDER_NOT_PAID_TO_DELIVER.formatted(order.getId())));
    }
  }
}
