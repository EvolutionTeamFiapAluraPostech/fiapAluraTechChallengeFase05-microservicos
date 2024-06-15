package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.enums.OrderStatus.PAGO;

import br.com.fiap.order.application.validator.OrderPaymentConfirmationValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.infrastructure.httpclient.logistics.request.PostOrderLogisticsHttpRequest;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmOrderPaymentUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;
  private final OrderPaymentConfirmationValidator orderPaymentConfirmationValidator;
  private final PostOrderLogisticsHttpRequest postOrderLogisticsHttpRequest;

  public ConfirmOrderPaymentUseCase(OrderService orderService, UuidValidator uuidValidator,
      OrderPaymentConfirmationValidator orderPaymentConfirmationValidator,
      PostOrderLogisticsHttpRequest postOrderLogisticsHttpRequest) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
    this.orderPaymentConfirmationValidator = orderPaymentConfirmationValidator;
    this.postOrderLogisticsHttpRequest = postOrderLogisticsHttpRequest;
  }

  @Transactional
  public void execute(String id) {
    uuidValidator.validate(id);
    var order = orderService.findByIdRequired(UUID.fromString(id));
    orderPaymentConfirmationValidator.validate(order);
    order.setOrderStatus(PAGO);
    orderService.save(order);
    postOrderLogisticsHttpRequest.request(order);
  }
}
