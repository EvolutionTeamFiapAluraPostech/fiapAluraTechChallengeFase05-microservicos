package br.com.fiap.payment.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.fiap.payment.infrastructure.repository.PaymentRepository;
import br.com.fiap.payment.shared.testdata.PaymentTestData;
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
    var payment = PaymentTestData.createNewPayment();
    var paymentWithId = PaymentTestData.createPayment();
    when(paymentRepository.save(payment)).thenReturn(paymentWithId);

    var paymentSaved = paymentService.save(payment);

    assertThat(paymentSaved).isNotNull();
    assertThat(paymentSaved).usingRecursiveComparison().ignoringFields("id")
        .isEqualTo(paymentSaved);
  }
}
