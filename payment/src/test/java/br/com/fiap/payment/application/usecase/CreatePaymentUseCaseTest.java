package br.com.fiap.payment.application.usecase;

import static br.com.fiap.payment.shared.fields.SharedFields.UUID;
import static br.com.fiap.payment.shared.messages.SharedMessages.UUID_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import br.com.fiap.payment.shared.validator.UuidValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest {

  @Mock
  private PaymentService paymentService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private CreatePaymentUseCase createPaymentUseCase;

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "aB1")
  void shouldThrowValidatorExceptionWhenOrderIdIsNullOrEmptyOrInvalid(String orderId) {
    var paymentInputDto = new PaymentInputDto(orderId, PaymentType.PIX.name());
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), UUID,
        UUID_INVALID.formatted(orderId)))).when(uuidValidator).validate(orderId);

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(UUID_INVALID.formatted(orderId));

    verify(paymentService, never()).save(any(Payment.class));
  }

}