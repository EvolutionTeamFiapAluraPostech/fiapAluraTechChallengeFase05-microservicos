package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_WITH_INVALID_UUID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.shared.testdata.OrderTestData;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class GetOrderByIdUseCaseTest {

  @Mock
  private OrderService orderService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private GetOrderByIdUseCase getOrderByIdUseCase;

  @Test
  void shouldGetOrderById() {
    var order = OrderTestData.createOrder();
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);

    var orderFound = getOrderByIdUseCase.execute(orderId.toString());

    assertThat(orderFound).isNotNull();
    assertThat(orderFound.getId()).isNotNull().isEqualTo(orderId);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "a", "1a#"})
  void shouldThrowExceptionWhenOrderIdIsInvalid(String orderId) {
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
        ORDER_WITH_INVALID_UUID_MESSAGE.formatted(orderId))))
        .when(uuidValidator).validate(orderId);

    assertThatThrownBy(() -> getOrderByIdUseCase.execute(orderId))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_WITH_INVALID_UUID_MESSAGE.formatted(orderId));
  }

  @Test
  void shouldThrowExceptionWhenOrderNotFoundById() {
    var orderId = UUID.randomUUID();
    when(orderService.findByIdRequired(orderId)).thenThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
            ORDER_NOT_FOUND_WITH_ID.formatted(orderId))));

    assertThatThrownBy(() -> getOrderByIdUseCase.execute(orderId.toString()))
        .isInstanceOf(NoResultException.class)
        .hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(orderId));
  }
}
