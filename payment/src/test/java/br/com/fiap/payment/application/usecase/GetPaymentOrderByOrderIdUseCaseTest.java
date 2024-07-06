package br.com.fiap.payment.application.usecase;


import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_ID_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE;
import static br.com.fiap.payment.shared.fields.SharedFields.UUID_FIELD;
import static br.com.fiap.payment.shared.messages.SharedMessages.UUID_INVALID;
import static br.com.fiap.payment.shared.testdata.PaymentTestData.createPayment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.shared.validator.UuidValidator;
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
class GetPaymentOrderByOrderIdUseCaseTest {

  @Mock
  private PaymentService paymentService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private GetPaymentOrderByOrderIdUseCase getPaymentOrderByOrderIdUseCase;

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "1Ab")
  void shouldReturnValidatorExceptionWhenOrderIdIsInvalid(String id) {
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
            UUID_INVALID.formatted(id)))).when(uuidValidator).validate(id);

    assertThatThrownBy(() -> getPaymentOrderByOrderIdUseCase.execute(id)).isInstanceOf(
        ValidatorException.class).hasMessage(UUID_INVALID.formatted(id));
  }

  @Test
  void shouldReturnNoResultExceptionWhenOrderWasNotFoundByOrderId() {
    var orderId = UUID.randomUUID();
    when(paymentService.findByOrderId(orderId)).thenThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), PAYMENT_ORDER_ORDER_ID_FIELD,
            PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE.formatted(orderId))));

    assertThatThrownBy(
        () -> getPaymentOrderByOrderIdUseCase.execute(orderId.toString())).isInstanceOf(
            NoResultException.class)
        .hasMessage(PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE.formatted(orderId));
  }

  @Test
  void shouldReturnPaymentWhenGetPaymentByOrderId() {
    var payment = createPayment();
    when(paymentService.findByOrderId(payment.getOrderId())).thenReturn(payment);

    var paymentFound = getPaymentOrderByOrderIdUseCase.execute(payment.getOrderId().toString());

    assertThat(paymentFound).isNotNull();
    assertThat(paymentFound).usingRecursiveComparison().isEqualTo(payment);
  }
}
