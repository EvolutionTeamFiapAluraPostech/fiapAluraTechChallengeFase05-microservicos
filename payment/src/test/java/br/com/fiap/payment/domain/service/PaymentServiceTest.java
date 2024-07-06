package br.com.fiap.payment.domain.service;

import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE;
import static br.com.fiap.payment.shared.testdata.PaymentTestData.createNewPayment;
import static br.com.fiap.payment.shared.testdata.PaymentTestData.createPayment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.infrastructure.repository.PaymentRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;
  @InjectMocks
  private PaymentService paymentService;

  @Test
  void shouldSavePayment() {
    var payment = createNewPayment();
    var paymentWithId = createPayment();
    when(paymentRepository.save(payment)).thenReturn(paymentWithId);

    var paymentSaved = paymentService.save(payment);

    assertThat(paymentSaved).isNotNull();
    assertThat(paymentSaved).usingRecursiveComparison().ignoringFields("id")
        .isEqualTo(paymentSaved);
  }

  @Test
  void shouldFindPaymentByOrderId() {
    var payment = createPayment();
    when(paymentRepository.findByOrderId(payment.getOrderId())).thenReturn(
        Optional.of(payment));

    var paymentFound = paymentService.findByOrderId(payment.getOrderId());

    assertThat(paymentFound).isNotNull();
    assertThat(paymentFound).usingRecursiveComparison().isEqualTo(payment);
  }

  @Test
  void shouldThrowNoResultExceptionWhenPaymentNotExistWithOrderId() {
    var orderId = UUID.randomUUID();
    when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> paymentService.findByOrderId(orderId)).isInstanceOf(
            NoResultException.class)
        .hasMessage(PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE.formatted(orderId));
  }
}
