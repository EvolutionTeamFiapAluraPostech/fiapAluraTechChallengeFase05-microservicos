package br.com.fiap.payment.domain.service;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.infrastructure.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public Payment save(Payment payment) {
    return paymentRepository.save(payment);
  }
}
