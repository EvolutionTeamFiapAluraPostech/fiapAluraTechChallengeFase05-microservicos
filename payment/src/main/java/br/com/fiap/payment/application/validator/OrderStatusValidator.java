package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_STATUS_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITH_INVALID_STATUS_MESSAGE;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class OrderStatusValidator {

  public void validate(OrderDto orderDto) {
    if (!orderDto.orderStatus().equals(OrderStatus.AGUARDANDO_PAGAMENTO.name())) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(),
          PAYMENT_ORDER_ORDER_STATUS_FIELD,
          PAYMENT_ORDER_WITH_INVALID_STATUS_MESSAGE.formatted(orderDto.id(), orderDto.orderStatus(),
              OrderStatus.AGUARDANDO_PAGAMENTO.name())));
    }
  }
}
