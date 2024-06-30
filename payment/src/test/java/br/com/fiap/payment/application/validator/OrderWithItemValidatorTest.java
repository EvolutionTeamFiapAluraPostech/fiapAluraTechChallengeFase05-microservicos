package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.shared.testdata.OrderTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderWithItemValidatorTest {

  @Spy
  private OrderWithItemValidator orderWithItemValidator;

  @Test
  void shouldValidatePaymentOrderItem() {
    var orderDto = OrderTestData.createOrderDto();

    assertThatCode(() -> orderWithItemValidator.validate(orderDto)).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderItemHasNoItem() {
    var orderDto = OrderTestData.createOrderDtoWithoutItem();

    assertThatCode(() -> orderWithItemValidator.validate(orderDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE.formatted(orderDto.id()));
  }
}
