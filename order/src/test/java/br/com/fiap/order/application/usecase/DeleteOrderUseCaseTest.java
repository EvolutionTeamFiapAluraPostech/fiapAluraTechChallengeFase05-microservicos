package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_WITH_INVALID_UUID_MESSAGE;
import static br.com.fiap.order.shared.testdata.OrderTestData.createOrder;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class DeleteOrderUseCaseTest {

  @Mock
  private OrderService orderService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private DeleteOrderUseCase deleteOrderUseCase;

  @Test
  void shouldDeleteOrder() {
    var order = createOrder();
    when(orderService.findByIdRequired(order.getId())).thenReturn(order);

    assertThatCode(() -> deleteOrderUseCase.execute(order.getId().toString()))
        .doesNotThrowAnyException();

    verify(uuidValidator).validate(order.getId().toString());
  }

  @Test
  void shouldThrowExceptionWhenOrderIdIsInvalid() {
    var orderId = "1aB";
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(),
            ORDER_ID_FIELD, ORDER_WITH_INVALID_UUID_MESSAGE.formatted(orderId)))).when(uuidValidator)
        .validate(orderId);

    assertThatThrownBy(() -> deleteOrderUseCase.execute(orderId))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_WITH_INVALID_UUID_MESSAGE.formatted(orderId));

    verify(uuidValidator).validate(orderId);
  }

  @Test
  void shouldThrowExceptionWhenOrderDoesNotExist() {
    var orderId = UUID.randomUUID();
    when(orderService.findByIdRequired(orderId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            ORDER_ID_FIELD, ORDER_NOT_FOUND_WITH_ID.formatted(orderId))));

    assertThatThrownBy(() -> deleteOrderUseCase.execute(orderId.toString()))
        .isInstanceOf(NoResultException.class)
        .hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(orderId));

    verify(uuidValidator).validate(orderId.toString());
  }
}
