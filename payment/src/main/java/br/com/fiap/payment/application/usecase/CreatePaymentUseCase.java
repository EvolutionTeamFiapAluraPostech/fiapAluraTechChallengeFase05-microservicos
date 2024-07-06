package br.com.fiap.payment.application.usecase;

import static br.com.fiap.payment.domain.enums.PaymentStatus.REALIZADO;

import br.com.fiap.payment.application.validator.OrderStatusValidator;
import br.com.fiap.payment.application.validator.OrderWithItemValidator;
import br.com.fiap.payment.application.validator.OrderWithItemWithoutTotalAmount;
import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.infrastructure.httpclient.company.dto.CompanyDto;
import br.com.fiap.payment.infrastructure.httpclient.company.request.GetCompanyByIdHttpRequest;
import br.com.fiap.payment.infrastructure.httpclient.customer.dto.CustomerDto;
import br.com.fiap.payment.infrastructure.httpclient.customer.request.GetCustomerByIdRequest;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.httpclient.order.request.GetOrderByIdHttpRequest;
import br.com.fiap.payment.infrastructure.httpclient.order.request.PatchOrderPaymentByIdHttpRequest;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import br.com.fiap.payment.shared.validator.UuidValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentUseCase {

  private final PaymentService paymentService;
  private final UuidValidator uuidValidator;
  private final GetOrderByIdHttpRequest getOrderByIdHttpRequest;
  private final GetCompanyByIdHttpRequest getCompanyByIdHttpRequest;
  private final GetCustomerByIdRequest getCustomerByIdRequest;
  private final OrderWithItemValidator orderWithItemValidator;
  private final OrderWithItemWithoutTotalAmount orderWithItemWithoutTotalAmount;
  private final OrderStatusValidator orderStatusValidator;
  private final PatchOrderPaymentByIdHttpRequest patchOrderPaymentByIdHttpRequest;

  public CreatePaymentUseCase(PaymentService paymentService, UuidValidator uuidValidator,
      GetOrderByIdHttpRequest getOrderByIdHttpRequest,
      GetCompanyByIdHttpRequest getCompanyByIdHttpRequest,
      GetCustomerByIdRequest getCustomerByIdRequest,
      OrderWithItemValidator orderWithItemValidator,
      OrderWithItemWithoutTotalAmount orderWithItemWithoutTotalAmount,
      OrderStatusValidator orderStatusValidator,
      PatchOrderPaymentByIdHttpRequest patchOrderPaymentByIdHttpRequest) {
    this.paymentService = paymentService;
    this.uuidValidator = uuidValidator;
    this.getOrderByIdHttpRequest = getOrderByIdHttpRequest;
    this.getCompanyByIdHttpRequest = getCompanyByIdHttpRequest;
    this.getCustomerByIdRequest = getCustomerByIdRequest;
    this.orderWithItemValidator = orderWithItemValidator;
    this.orderWithItemWithoutTotalAmount = orderWithItemWithoutTotalAmount;
    this.orderStatusValidator = orderStatusValidator;
    this.patchOrderPaymentByIdHttpRequest = patchOrderPaymentByIdHttpRequest;
  }

  @Transactional
  public Payment execute(PaymentInputDto paymentInputDto) {
    uuidValidator.validate(paymentInputDto.orderId());
    var orderDto = getOrderByIdHttpRequest.request(paymentInputDto.orderId());
    orderStatusValidator.validate(orderDto);
    var companyDto = getCompanyByIdHttpRequest.request(orderDto.companyId());
    var customerDto = getCustomerByIdRequest.request(orderDto.customerId());
    orderWithItemValidator.validate(orderDto);
    orderWithItemWithoutTotalAmount.validate(orderDto);
    var payment = createPayment(paymentInputDto, orderDto, companyDto, customerDto);
    var paymentSaved = paymentService.save(payment);
    if (paymentSaved != null && paymentSaved.isPaymentSaved()) {
      patchOrderPaymentByIdHttpRequest.request(orderDto.id());
      return paymentSaved;
    }
    return null;
  }

  private static Payment createPayment(PaymentInputDto paymentInputDto, OrderDto orderDto,
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
}
