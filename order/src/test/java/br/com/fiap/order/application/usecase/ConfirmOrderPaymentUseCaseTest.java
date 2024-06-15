package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_WITH_INVALID_UUID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.OrderPaymentConfirmationValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.infrastructure.httpclient.logistics.request.PostOrderLogisticsHttpRequest;
import br.com.fiap.order.shared.testdata.OrderTestData;
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
class ConfirmOrderPaymentUseCaseTest {

  @Mock
  private OrderService orderService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private OrderPaymentConfirmationValidator orderPaymentConfirmationValidator;
  @Mock
  private PostOrderLogisticsHttpRequest postOrderLogisticsHttpRequest;
  @InjectMocks
  private ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;

  @Test
  void shouldConfirmOrderPayment() {
    var order = OrderTestData.createOrder();
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);

    assertThatCode(
        () -> confirmOrderPaymentUseCase.execute(orderId.toString())).doesNotThrowAnyException();
    verify(uuidValidator).validate(orderId.toString());
    verify(orderPaymentConfirmationValidator).validate(order);
    verify(postOrderLogisticsHttpRequest).request(order);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1aB"})
  void shouldThrowExceptionWhenOrderIdIsInvalid(String id) {
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
        ORDER_WITH_INVALID_UUID_MESSAGE.formatted(id)))).when(uuidValidator).validate(id);

    assertThatThrownBy(() -> confirmOrderPaymentUseCase.execute(id)).isInstanceOf(
        ValidatorException.class).hasMessage(ORDER_WITH_INVALID_UUID_MESSAGE.formatted(id));
    verify(orderPaymentConfirmationValidator, never()).validate(any(Order.class));
    verify(postOrderLogisticsHttpRequest, never()).request(any(Order.class));
  }

  @Test
  void shouldThrowExceptionWhenOrderDoesNotExist() {
    var id = UUID.randomUUID();
    when(orderService.findByIdRequired(id)).thenThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
            ORDER_NOT_FOUND_WITH_ID.formatted(id))));

    assertThatThrownBy(() -> confirmOrderPaymentUseCase.execute(id.toString())).isInstanceOf(
        NoResultException.class).hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(id));
    verify(orderPaymentConfirmationValidator, never()).validate(any(Order.class));
    verify(postOrderLogisticsHttpRequest, never()).request(any(Order.class));
  }
}
