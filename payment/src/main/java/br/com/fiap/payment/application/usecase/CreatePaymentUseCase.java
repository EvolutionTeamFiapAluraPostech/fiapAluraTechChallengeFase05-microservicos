package br.com.fiap.payment.application.usecase;

import static br.com.fiap.payment.domain.enums.PaymentStatus.REALIZADO;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import br.com.fiap.payment.shared.validator.UuidValidator;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentUseCase {

  private final PaymentService paymentService;
  private final UuidValidator uuidValidator;

  public CreatePaymentUseCase(PaymentService paymentService, UuidValidator uuidValidator) {
    this.paymentService = paymentService;
    this.uuidValidator = uuidValidator;
  }

  @Transactional
  public Payment execute(PaymentInputDto paymentInputDto) {
    uuidValidator.validate(paymentInputDto.orderId());
    var companyId = "";
    var companyName = "";
    var customerId = "";
    var customerName = "";
    var payment = createPayment(paymentInputDto, companyId, companyName, customerId, customerName);
    return paymentService.save(payment);
  }

  private static Payment createPayment(PaymentInputDto paymentInputDto, String companyId,
      String companyName, String customerId, String customerName) {
    return Payment.builder()
        .orderId(paymentInputDto.orderId())
        .companyId(companyId)
        .companyName(companyName)
        .customerId(customerId)
        .customerName(customerName)
        .paymentType(PaymentType.valueOf(paymentInputDto.paymentType()))
        .paymentStatus(REALIZADO)
        .paymentTotalAmount(BigDecimal.ZERO)
        .build();
  }
}
