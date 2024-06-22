package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.CompanyExistsValidator;
import br.com.fiap.order.application.validator.OrderItemPriceValidator;
import br.com.fiap.order.application.validator.OrderItemQuantityValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderUseCase {

  private final OrderService orderService;
  private final CompanyExistsValidator companyExistsValidator;
  private final OrderItemQuantityValidator orderItemQuantityValidator;
  private final OrderItemPriceValidator orderItemPriceValidator;

  public CreateOrderUseCase(OrderService orderService,
      CompanyExistsValidator companyExistsValidator,
      OrderItemQuantityValidator orderItemQuantityValidator,
      OrderItemPriceValidator orderItemPriceValidator) {
    this.orderService = orderService;
    this.companyExistsValidator = companyExistsValidator;
    this.orderItemQuantityValidator = orderItemQuantityValidator;
    this.orderItemPriceValidator = orderItemPriceValidator;
  }

  @Transactional
  public Order execute(OrderInputDto orderInputDto) {
    companyExistsValidator.validate(orderInputDto.companyId());
    orderItemQuantityValidator.validate(orderInputDto.orderItems());
    orderItemPriceValidator.validate(orderInputDto.orderItems());
    var order = updateOrderAttributes(orderInputDto);
    return orderService.save(order);
  }

  private Order updateOrderAttributes(OrderInputDto orderInputDto) {
    var order = orderInputDto.toOrder();
    calculateTotalAmountOrderItem(order);
    order.setOrderStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
    order.calculateTotalOrderAmout();
    return order;
  }

  private void calculateTotalAmountOrderItem(Order order) {
    for (OrderItem orderItem : order.getOrderItems()) {
      orderItem.setOrder(order);
      orderItem.calculateTotalItemAmount();
    }
  }
}
