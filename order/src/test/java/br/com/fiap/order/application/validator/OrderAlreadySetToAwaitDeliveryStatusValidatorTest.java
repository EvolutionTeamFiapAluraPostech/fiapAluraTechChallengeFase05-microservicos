package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.enums.OrderStatus.AGUARDANDO_ENTREGA;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ALREADY_AWAITING_DELIVERY;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.shared.testdata.OrderTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderAlreadySetToAwaitDeliveryStatusValidatorTest {

  @Spy
  private OrderAlreadySetToAwaitDeliveryStatusValidator orderAlreadySetToAwaitDeliveryStatusValidator;

  @Test
  void shouldValidateOrderIsNotAwaitingDelivery() {
    var order = OrderTestData.createOrder();

    assertThatCode(() -> orderAlreadySetToAwaitDeliveryStatusValidator.validate(order))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenOrderAlreadyIsAwaitingDelivery() {
    var order = OrderTestData.createOrder();
    order.setOrderStatus(AGUARDANDO_ENTREGA);

    assertThatThrownBy(() -> orderAlreadySetToAwaitDeliveryStatusValidator.validate(order))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_ALREADY_AWAITING_DELIVERY.formatted(order.getId()));
  }
}