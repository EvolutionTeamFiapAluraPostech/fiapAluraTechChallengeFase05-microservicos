package br.com.fiap.payment.application.usecase;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.shared.validator.UuidValidator;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetPaymentOrderByOrderIdUseCase {

  private final PaymentService paymentService;
  private final UuidValidator uuidValidator;

  public GetPaymentOrderByOrderIdUseCase(PaymentService paymentService, UuidValidator uuidValidator) {
    this.paymentService = paymentService;
    this.uuidValidator = uuidValidator;
  }

  public Payment execute(String id) {
    uuidValidator.validate(id);
    return paymentService.findByOrderId(UUID.fromString(id));
  }
}
