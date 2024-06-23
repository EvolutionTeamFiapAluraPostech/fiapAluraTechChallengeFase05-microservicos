package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.enums.OrderStatus.AGUARDANDO_PAGAMENTO;
import static br.com.fiap.order.domain.fields.OrderFields.ORDER_STATUS;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_IS_NOT_VALID_FOR_UPDATE;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderIsAbleToUpdateValidator {

  public void validate(Order order) {
    if (!order.getOrderStatus().equals(AGUARDANDO_PAGAMENTO)) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_STATUS,
          ORDER_IS_NOT_VALID_FOR_UPDATE.formatted(AGUARDANDO_PAGAMENTO,
              order.getOrderStatus().name(), order.getId())));
    }
  }
}
