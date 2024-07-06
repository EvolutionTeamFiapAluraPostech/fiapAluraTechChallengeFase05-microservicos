package br.com.fiap.payment.domain.service;

import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_ID_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.infrastructure.repository.PaymentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public Payment save(Payment payment) {
    return paymentRepository.save(payment);
  }

  public Payment findByOrderId(UUID id) {
    return paymentRepository.findByOrderId(id)
        .orElseThrow(() -> new NoResultException(new FieldError(this.getClass().getSimpleName(),
            PAYMENT_ORDER_ORDER_ID_FIELD,
            PAYMENT_ORDER_NOT_FOUND_BY_ORDER_ID_MESSAGE.formatted(id))));
  }
}
