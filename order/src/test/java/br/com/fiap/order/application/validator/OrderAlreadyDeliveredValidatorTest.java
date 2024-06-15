package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.enums.OrderStatus.ENTREGUE;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ALREADY_DELIVERED;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.shared.testdata.OrderTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderAlreadyDeliveredValidatorTest {

  @Spy
  private OrderAlreadyDeliveredValidator orderAlreadyDeliveredValidator;

  @Test
  void shouldValidateOrderNotPaid() {
    var order = OrderTestData.createOrder();

    assertThatCode(() -> orderAlreadyDeliveredValidator.validate(order))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenOrderAlreadyDelivered() {
    var order = OrderTestData.createOrder();
    order.setOrderStatus(ENTREGUE);

    assertThatThrownBy(() -> orderAlreadyDeliveredValidator.validate(order))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_ALREADY_DELIVERED.formatted(order.getId()));
  }
}
