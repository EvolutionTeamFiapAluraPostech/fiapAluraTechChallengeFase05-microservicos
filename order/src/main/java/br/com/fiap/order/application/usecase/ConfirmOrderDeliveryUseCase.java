package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.enums.OrderStatus.ENTREGUE;

import br.com.fiap.order.application.validator.OrderAlreadyDeliveredValidator;
import br.com.fiap.order.application.validator.OrderAlreadyPaidToDeliverValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmOrderDeliveryUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;
  private final OrderAlreadyDeliveredValidator orderAlreadyDeliveredValidator;
  private final OrderAlreadyPaidToDeliverValidator orderAlreadyPaidToDeliverValidator;

  public ConfirmOrderDeliveryUseCase(OrderService orderService, UuidValidator uuidValidator,
      OrderAlreadyDeliveredValidator orderAlreadyDeliveredValidator,
      OrderAlreadyPaidToDeliverValidator orderAlreadyPaidToDeliverValidator) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
    this.orderAlreadyDeliveredValidator = orderAlreadyDeliveredValidator;
    this.orderAlreadyPaidToDeliverValidator = orderAlreadyPaidToDeliverValidator;
  }

  @Transactional
  public void execute(String id) {
    uuidValidator.validate(id);
    var order = orderService.findByIdRequired(UUID.fromString(id));
    orderAlreadyDeliveredValidator.validate(order);
    orderAlreadyPaidToDeliverValidator.validate(order);
    order.setOrderStatus(ENTREGUE);
    orderService.save(order);
  }
}
