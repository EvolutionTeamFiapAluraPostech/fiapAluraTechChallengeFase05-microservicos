package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.enums.OrderStatus.PAGO;
import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_PAID_TO_DELIVER;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_WITH_INVALID_UUID_MESSAGE;
import static br.com.fiap.order.shared.testdata.OrderTestData.createOrder;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.OrderAlreadyPaidToDeliverValidator;
import br.com.fiap.order.application.validator.OrderAlreadySetToAwaitDeliveryStatusValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class AwaitOrderDeliveryUseCaseTest {

  @Mock
  private OrderService orderService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private OrderAlreadySetToAwaitDeliveryStatusValidator orderAlreadySetToAwaitDeliveryStatusValidator;
  @Mock
  private OrderAlreadyPaidToDeliverValidator orderAlreadyPaidToDeliverValidator;
  @InjectMocks
  private AwaitOrderDeliveryUseCase awaitOrderDeliveryUseCase;

  @Test
  void shouldSetAwaitDeliveryOrderStatusInOrder() {
    var order = createOrder();
    order.setOrderStatus(PAGO);
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);

    assertThatCode(
        () -> awaitOrderDeliveryUseCase.execute(orderId.toString())).doesNotThrowAnyException();
    verify(uuidValidator).validate(orderId.toString());
    verify(orderAlreadyPaidToDeliverValidator).validate(order);
    verify(orderAlreadySetToAwaitDeliveryStatusValidator).validate(order);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1aB"})
  void shouldThrowExceptionWhenOrderIdIsInvalid(String id) {
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
        ORDER_WITH_INVALID_UUID_MESSAGE.formatted(id)))).when(uuidValidator).validate(id);

    assertThatThrownBy(() -> awaitOrderDeliveryUseCase.execute(id)).isInstanceOf(
        ValidatorException.class).hasMessage(ORDER_WITH_INVALID_UUID_MESSAGE.formatted(id));
    verify(orderAlreadySetToAwaitDeliveryStatusValidator, never()).validate(any(Order.class));
  }

  @Test
  void shouldThrowExceptionWhenOrderDoesNotExist() {
    var id = UUID.randomUUID();
    when(orderService.findByIdRequired(id)).thenThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
            ORDER_NOT_FOUND_WITH_ID.formatted(id))));

    assertThatThrownBy(() -> awaitOrderDeliveryUseCase.execute(id.toString())).isInstanceOf(
        NoResultException.class).hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(id));
    verify(orderAlreadySetToAwaitDeliveryStatusValidator, never()).validate(any(Order.class));
  }

  @Test
  void shouldThrowExceptionWhenOrderIsAwaitingPayment() {
    var order = createOrder();
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
        ORDER_NOT_PAID_TO_DELIVER.formatted(orderId)))).when(orderAlreadyPaidToDeliverValidator)
        .validate(order);

    assertThatThrownBy(() -> awaitOrderDeliveryUseCase.execute(orderId.toString()))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_NOT_PAID_TO_DELIVER.formatted(orderId.toString()));
    verify(orderAlreadySetToAwaitDeliveryStatusValidator, never()).validate(any(Order.class));
  }
}
