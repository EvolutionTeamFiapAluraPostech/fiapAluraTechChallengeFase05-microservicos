package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteOrderUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;

  public DeleteOrderUseCase(OrderService orderService, UuidValidator uuidValidator) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
  }

  @Transactional
  public void execute(String id) {
    uuidValidator.validate(id);
    var order = orderService.findByIdRequired(UUID.fromString(id));
    order.setDeleted(true);
    order.getOrderItems().forEach(orderItem -> orderItem.setDeleted(true));
    orderService.save(order);
  }
}
