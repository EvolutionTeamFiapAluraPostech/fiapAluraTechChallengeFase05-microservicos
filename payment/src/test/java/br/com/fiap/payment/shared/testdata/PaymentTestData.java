package br.com.fiap.payment.shared.testdata;

import static br.com.fiap.payment.domain.enums.PaymentStatus.REALIZADO;
import static br.com.fiap.payment.domain.enums.PaymentType.PIX;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.infrastructure.httpclient.company.dto.CompanyDto;
import br.com.fiap.payment.infrastructure.httpclient.customer.dto.CustomerDto;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import java.math.BigDecimal;
import java.util.UUID;

public final class PaymentTestData {

  public static final UUID PAYMENT_ORDER_UUID = UUID.randomUUID();
  public static final String PAYMENT_ORDER_ID = PAYMENT_ORDER_UUID.toString();
  public static final UUID PAYMENT_COMPANY_UUID = UUID.randomUUID();
  public static final String PAYMENT_COMPANY_ID = PAYMENT_COMPANY_UUID.toString();
  public static final String PAYMENT_COMPANY_NAME = "Matrix Company";
  public static final UUID PAYMENT_CUSTOMER_UUID = UUID.randomUUID();
  public static final String PAYMENT_CUSTOMER_ID = PAYMENT_CUSTOMER_UUID.toString();
  public static final String PAYMENT_CUSTOMER_NAME = "Thomas Anderson";

  private PaymentTestData() {
  }

  public static Payment createNewPayment() {
    return Payment.builder()
        .orderId(PAYMENT_ORDER_ID)
        .companyId(PAYMENT_COMPANY_ID)
        .companyName(PAYMENT_COMPANY_NAME)
        .customerId(PAYMENT_CUSTOMER_ID)
        .customerName(PAYMENT_CUSTOMER_NAME)
        .paymentType(PIX)
        .paymentStatus(REALIZADO)
        .paymentTotalAmount(BigDecimal.TEN)
        .build();
  }

  public static Payment createPayment() {
    var payment = createNewPayment();
    payment.setId(UUID.randomUUID());
    return payment;
  }

  public static Payment createPaymentFrom(PaymentInputDto paymentInputDto, OrderDto orderDto,
      CompanyDto companyDto, CustomerDto customerDto) {
    return Payment.builder()
        .orderId(paymentInputDto.orderId())
        .companyId(orderDto.companyId())
        .companyName(companyDto.name())
        .customerId(orderDto.customerId())
        .customerName(customerDto.name())
        .paymentType(PaymentType.valueOf(paymentInputDto.paymentType()))
        .paymentStatus(REALIZADO)
        .paymentTotalAmount(orderDto.calculateTotalAmount())
        .build();
  }

  public static Payment createPaymentSavedFrom(PaymentInputDto paymentInputDto, OrderDto orderDto,
      CompanyDto companyDto, CustomerDto customerDto) {
    var payment = createPaymentFrom(paymentInputDto, orderDto, companyDto, customerDto);
    payment.setId(UUID.randomUUID());
    return payment;
  }
}
