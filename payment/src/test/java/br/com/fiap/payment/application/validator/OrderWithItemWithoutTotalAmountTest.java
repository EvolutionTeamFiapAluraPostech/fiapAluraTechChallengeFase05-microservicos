package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDto;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDtoWithItemWithInvalidTotalAmount;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.payment.domain.exception.ValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderWithItemWithoutTotalAmountTest {

  @Spy
  private OrderWithItemWithoutTotalAmount orderWithItemWithoutTotalAmount;

  @Test
  void shouldValidateOrder() {
    var order = createOrderDto();

    assertThatCode(
        () -> orderWithItemWithoutTotalAmount.validate(order)).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowValidateExceptionWhenOrderItemHasNoPrice() {
    var order = createOrderDtoWithItemWithInvalidTotalAmount();

    assertThatThrownBy(
        () -> orderWithItemWithoutTotalAmount.validate(order))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE.formatted(order.id()));
  }
}
