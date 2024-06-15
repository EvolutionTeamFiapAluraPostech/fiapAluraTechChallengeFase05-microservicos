package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetOrderByIdUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;

  public GetOrderByIdUseCase(OrderService orderService, UuidValidator uuidValidator) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
  }

  public Order execute(String id) {
    uuidValidator.validate(id);
    return orderService.findByIdRequired(UUID.fromString(id));
  }
}
