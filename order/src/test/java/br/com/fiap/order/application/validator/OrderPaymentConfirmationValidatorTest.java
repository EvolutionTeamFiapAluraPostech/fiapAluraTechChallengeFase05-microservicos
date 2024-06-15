package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.enums.OrderStatus.PAGO;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ALREADY_PAID;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.shared.testdata.OrderTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderPaymentConfirmationValidatorTest {

  @Spy
  private OrderPaymentConfirmationValidator orderPaymentConfirmationValidator;

  @Test
  void shouldValidateOrderNotPaid() {
    var order = OrderTestData.createOrder();

    assertThatCode(() -> orderPaymentConfirmationValidator.validate(order))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenOrderAlreadyPaid() {
    var order = OrderTestData.createOrder();
    order.setOrderStatus(PAGO);

    assertThatThrownBy(() -> orderPaymentConfirmationValidator.validate(order))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_ALREADY_PAID.formatted(order.getId()));
  }
}
