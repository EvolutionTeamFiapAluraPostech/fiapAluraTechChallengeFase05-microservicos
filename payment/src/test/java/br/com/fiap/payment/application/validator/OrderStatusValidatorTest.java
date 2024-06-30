package br.com.fiap.payment.application.validator;

import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDto;
import static org.assertj.core.api.Assertions.assertThatCode;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderStatusValidatorTest {

  @Spy
  private OrderStatusValidator orderStatusValidator;

  @Test
  void shouldValidateOrderStatus() {
    var orderDto = createOrderDto();

    assertThatCode(() -> orderStatusValidator.validate(orderDto)).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenOrderStatusIsInvalid() {
    var orderDto = createOrderDto(OrderStatus.PAGO);

    assertThatCode(() -> orderStatusValidator.validate(orderDto))
        .isInstanceOf(ValidatorException.class);
  }
}
