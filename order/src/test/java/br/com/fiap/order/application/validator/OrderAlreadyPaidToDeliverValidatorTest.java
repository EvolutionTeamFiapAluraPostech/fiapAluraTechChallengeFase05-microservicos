package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_PAID_TO_DELIVER;
import static br.com.fiap.order.shared.testdata.OrderTestData.createOrder;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.domain.exception.ValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderAlreadyPaidToDeliverValidatorTest {

  @Spy
  private OrderAlreadyPaidToDeliverValidator orderAlreadyPaidToDeliverValidator;

  @Test
  void shouldValidateOrderWhenItWasAlreadyPaid() {
    var order = createOrder();
    order.setOrderStatus(OrderStatus.PAGO);

    assertThatCode(
        () -> orderAlreadyPaidToDeliverValidator.validate(order)).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenOrderWasNotPaidBeforeDeliver() {
    var order = createOrder();

    assertThatThrownBy(
        () -> orderAlreadyPaidToDeliverValidator.validate(order))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_NOT_PAID_TO_DELIVER.formatted(order.getId()));
  }
}