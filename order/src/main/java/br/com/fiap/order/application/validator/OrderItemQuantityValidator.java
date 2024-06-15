package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ITEM_QUANTITY_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ITEM_QUANTITY_INVALID;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.presentation.api.dto.OrderItemInputDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderItemQuantityValidator {

  public void validate(List<OrderItemInputDto> orderItems) {
    for (OrderItemInputDto orderItem : orderItems) {
      if (orderItem.quantity().equals(BigDecimal.ZERO)) {
        throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_ITEM_QUANTITY_FIELD,
            ORDER_ITEM_QUANTITY_INVALID.formatted(orderItem.quantity())));
      }
      if (orderItem.quantity().compareTo(BigDecimal.ZERO) < 0) {
        throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_ITEM_QUANTITY_FIELD,
            ORDER_ITEM_QUANTITY_INVALID.formatted(orderItem.quantity())));
      }
    }
  }
}
