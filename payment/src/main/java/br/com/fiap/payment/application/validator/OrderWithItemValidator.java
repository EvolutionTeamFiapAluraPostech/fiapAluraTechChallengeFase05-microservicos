package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_ITEMS_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderWithItemValidator {

  public void validate(OrderDto order) {
    if (order.orderItems().isEmpty()) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(),
          PAYMENT_ORDER_ORDER_ITEMS_FIELD, PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE.formatted(order.id())));
    }
  }
}
