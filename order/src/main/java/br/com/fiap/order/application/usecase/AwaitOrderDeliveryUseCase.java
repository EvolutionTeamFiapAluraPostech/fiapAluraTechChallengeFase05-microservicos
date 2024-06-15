package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.enums.OrderStatus.AGUARDANDO_ENTREGA;

import br.com.fiap.order.application.validator.OrderAlreadyPaidToDeliverValidator;
import br.com.fiap.order.application.validator.OrderAlreadySetToAwaitDeliveryStatusValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AwaitOrderDeliveryUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;
  private final OrderAlreadySetToAwaitDeliveryStatusValidator orderAlreadySetToAwaitDeliveryStatusValidator;
  private final OrderAlreadyPaidToDeliverValidator orderAlreadyPaidToDeliverValidator;

  public AwaitOrderDeliveryUseCase(OrderService orderService, UuidValidator uuidValidator,
      OrderAlreadySetToAwaitDeliveryStatusValidator orderAlreadySetToAwaitDeliveryStatusValidator,
      OrderAlreadyPaidToDeliverValidator orderAlreadyPaidToDeliverValidator) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
    this.orderAlreadySetToAwaitDeliveryStatusValidator = orderAlreadySetToAwaitDeliveryStatusValidator;
    this.orderAlreadyPaidToDeliverValidator = orderAlreadyPaidToDeliverValidator;
  }

  @Transactional
  public void execute(String id) {
    uuidValidator.validate(id);
    var order = orderService.findByIdRequired(UUID.fromString(id));
    orderAlreadyPaidToDeliverValidator.validate(order);
    orderAlreadySetToAwaitDeliveryStatusValidator.validate(order);
    order.setOrderStatus(AGUARDANDO_ENTREGA);
    orderService.save(order);
  }
}
