package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.CompanyExistsValidator;
import br.com.fiap.order.application.validator.CustomerExistsValidator;
import br.com.fiap.order.application.validator.OrderItemPriceValidator;
import br.com.fiap.order.application.validator.OrderItemQuantityValidator;
import br.com.fiap.order.application.validator.ProductExistsValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import br.com.fiap.order.presentation.api.dto.OrderItemInputDto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderUseCase {

  private final OrderService orderService;
  private final CompanyExistsValidator companyExistsValidator;
  private final CustomerExistsValidator customerExistsValidator;
  private final ProductExistsValidator productExistsValidator;
  private final OrderItemQuantityValidator orderItemQuantityValidator;
  private final OrderItemPriceValidator orderItemPriceValidator;

  public CreateOrderUseCase(OrderService orderService,
      CompanyExistsValidator companyExistsValidator,
      CustomerExistsValidator customerExistsValidator,
      ProductExistsValidator productExistsValidator,
      OrderItemQuantityValidator orderItemQuantityValidator,
      OrderItemPriceValidator orderItemPriceValidator) {
    this.orderService = orderService;
    this.companyExistsValidator = companyExistsValidator;
    this.customerExistsValidator = customerExistsValidator;
    this.productExistsValidator = productExistsValidator;
    this.orderItemQuantityValidator = orderItemQuantityValidator;
    this.orderItemPriceValidator = orderItemPriceValidator;
  }

  @Transactional
  public Order execute(OrderInputDto orderInputDto) {
    companyExistsValidator.validate(orderInputDto.companyId());
    customerExistsValidator.validate(orderInputDto.customerId());
    var productsId = getProductsIdListFrom(orderInputDto);
    productExistsValidator.validate(productsId);
    orderItemQuantityValidator.validate(orderInputDto.orderItems());
    orderItemPriceValidator.validate(orderInputDto.orderItems());
    var order = updateOrderAttributes(orderInputDto);
    return orderService.save(order);
  }

  private List<String> getProductsIdListFrom(OrderInputDto orderInputDto) {
    return orderInputDto.orderItems().stream().map(OrderItemInputDto::productId).toList();
  }

  private Order updateOrderAttributes(OrderInputDto orderInputDto) {
    var order = orderInputDto.toOrder();
    order.calculateTotalAmountOrderItem();
    order.setOrderStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
    order.calculateTotalOrderAmout();
    return order;
  }
}
