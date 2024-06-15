package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.service.OrderService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetOrdersByCompanyIdOrCustomerIdUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;

  public GetOrdersByCompanyIdOrCustomerIdUseCase(OrderService orderService,
      UuidValidator uuidValidator) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
  }

  public Page<Order> execute(String companyId, String customerId, Pageable pageable) {
    UUID companyIdParam = null;
    if (companyId != null && !companyId.isBlank()) {
      uuidValidator.validate(companyId);
      companyIdParam = UUID.fromString(companyId);
    }
    UUID customerIdParam = null;
    if (customerId != null && !customerId.isBlank()) {
      uuidValidator.validate(customerId);
      customerIdParam = UUID.fromString(customerId);
    }
    return orderService.findOrderByCompanyIdOrCustomerId(companyIdParam, customerIdParam, pageable);
  }
}
