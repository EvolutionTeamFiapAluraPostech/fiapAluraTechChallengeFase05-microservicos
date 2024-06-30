package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_ITEMS_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderWithItemWithoutTotalAmount {

  public void validate(OrderDto orderDto) {
    var orderItems = orderDto.orderItems();
    if (!orderItems.isEmpty() && orderItems.stream()
        .anyMatch(orderItemDto -> orderItemDto.totalAmout().equals(BigDecimal.ZERO))) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), PAYMENT_ORDER_ORDER_ITEMS_FIELD,
              PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE.formatted(orderDto.id())));
    }

  }
}
