package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.CompanyExistsValidator;
import br.com.fiap.order.application.validator.CustomerExistsValidator;
import br.com.fiap.order.application.validator.OrderIsAbleToUpdateValidator;
import br.com.fiap.order.application.validator.ProductExistsValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import br.com.fiap.order.presentation.api.dto.OrderItemDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOrderUseCase {

  private final OrderService orderService;
  private final UuidValidator uuidValidator;
  private final CompanyExistsValidator companyExistsValidator;
  private final CustomerExistsValidator customerExistsValidator;
  private final ProductExistsValidator productExistsValidator;
  private final OrderIsAbleToUpdateValidator orderIsAbleToUpdateValidator;

  public UpdateOrderUseCase(OrderService orderService, UuidValidator uuidValidator,
      CompanyExistsValidator companyExistsValidator,
      CustomerExistsValidator customerExistsValidator,
      ProductExistsValidator productExistsValidator,
      OrderIsAbleToUpdateValidator orderAlreadyDeliveredValidator) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
    this.companyExistsValidator = companyExistsValidator;
    this.customerExistsValidator = customerExistsValidator;
    this.productExistsValidator = productExistsValidator;
    this.orderIsAbleToUpdateValidator = orderAlreadyDeliveredValidator;
  }

  @Transactional
  public Order execute(String id, OrderDto orderDto) {
    uuidValidator.validate(id);
    var order = orderService.findByIdRequired(UUID.fromString(id));
    companyExistsValidator.validate(orderDto.companyId());
    customerExistsValidator.validate(orderDto.customerId());
    orderIsAbleToUpdateValidator.validate(order);
    updateOrderAttributesToSave(order, orderDto);
    return orderService.save(order);
  }

  private void updateOrderAttributesToSave(Order order, OrderDto orderDto) {
    order.setCompanyId(UUID.fromString(orderDto.companyId()));
    order.setCustomerId(UUID.fromString(orderDto.customerId()));
    updateOrderItemAttributesToSave(order.getOrderItems(), orderDto.orderItems());
  }

  private void updateOrderItemAttributesToSave(List<OrderItem> orderItems,
      List<OrderItemDto> orderItemsDto) {
    var productsId = orderItemsDto.stream().map(OrderItemDto::productId).toList();
    productExistsValidator.validate(productsId);
    removeSavedOrderItemsThatWasRemovedInOrderItemsDto(orderItems, orderItemsDto);
    updateAttributesFromExistentOrderItem(orderItems, orderItemsDto);
    insertNewProductsToOrderItems(orderItems, orderItemsDto);
  }

  private void insertNewProductsToOrderItems(List<OrderItem> orderItems,
      List<OrderItemDto> orderItemsDto) {
    var orderOptional = orderItems.stream().map(OrderItem::getOrder).findFirst();
    if (orderOptional.isPresent()) {
      var order = orderOptional.get();
      var orderItemToInsert = orderItemsDto.stream()
          .filter(orderItemDto -> orderItemDto.id() == null && orderItemDto.productId() != null)
          .toList();
      for (OrderItemDto orderItemDto : orderItemToInsert) {
        var orderItem = OrderItem.builder()
            .order(order)
            .productId(UUID.fromString(orderItemDto.productId()))
            .quantity(orderItemDto.quantity())
            .price(orderItemDto.price()).build();
        orderItems.add(orderItem);
      }
    }
  }

  private void updateAttributesFromExistentOrderItem(List<OrderItem> orderItems,
      List<OrderItemDto> orderItemsDto) {
    var orderItemsToUpdate = orderItems.stream()
        .filter(
            orderItem -> (orderItemsDto.stream().filter(orderItemDto -> orderItemDto.id() != null)
                .anyMatch(
                    orderItemDto -> UUID.fromString(orderItemDto.id()).equals(orderItem.getId()))))
        .toList();
    for (OrderItem orderItem : orderItems) {
      var orderItemToUpdate = orderItemsToUpdate.stream()
          .filter(orderItem1 -> orderItem1.equals(orderItem)).findFirst();
      if (orderItemToUpdate.isPresent()) {
        orderItem.setProductId(orderItemToUpdate.get().getProductId());
        orderItem.setQuantity(orderItemToUpdate.get().getQuantity());
        orderItem.setPrice(orderItemToUpdate.get().getPrice());
      }
    }
  }

  private void removeSavedOrderItemsThatWasRemovedInOrderItemsDto(List<OrderItem> orderItems,
      List<OrderItemDto> orderItemsDto) {
    var orderItemToRemove = orderItems.stream()
        .filter(
            orderItem -> (orderItemsDto.stream().filter(orderItemDto -> orderItemDto.id() != null)
                .noneMatch(
                    orderItemDto -> UUID.fromString(orderItemDto.id()).equals(orderItem.getId()))))
        .toList();
    orderItems.removeAll(orderItemToRemove);
  }
}
