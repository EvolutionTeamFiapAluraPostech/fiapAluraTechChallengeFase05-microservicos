package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.enums.OrderStatus.AGUARDANDO_PAGAMENTO;
import static br.com.fiap.order.domain.enums.OrderStatus.ENTREGUE;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_IS_NOT_VALID_FOR_UPDATE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.shared.testdata.OrderTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderIsAbleToUpdateValidatorTest {

  @Spy
  OrderIsAbleToUpdateValidator orderIsAbleToUpdateValidator;

  @Test
  void shouldValidateWhenOrderIsValidForUpdate() {
    var order = OrderTestData.createOrder();

    assertThatCode(() -> orderIsAbleToUpdateValidator.validate(order))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenOrderIsInvalidForUpdate() {
    var order = OrderTestData.createOrder();
    order.setOrderStatus(ENTREGUE);

    assertThatThrownBy(() -> orderIsAbleToUpdateValidator.validate(order))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_IS_NOT_VALID_FOR_UPDATE.formatted(AGUARDANDO_PAGAMENTO,
            order.getOrderStatus().name(),
            order.getId()));
  }
}
