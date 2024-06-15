package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.OrderIsAbleToUpdateValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.infrastructure.httpclient.cep.GetCoordinatesFromCepRequest;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import br.com.fiap.order.presentation.api.dto.OrderItemDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UpdateOrderUseCase {

  public static final String LATITUDE = "Latitude";
  public static final String LONGITUDE = "Longitude";
  private final OrderService orderService;
  private final UuidValidator uuidValidator;
  private final OrderIsAbleToUpdateValidator orderIsAbleToUpdateValidator;
  private final GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;

  public UpdateOrderUseCase(OrderService orderService, UuidValidator uuidValidator,
      OrderIsAbleToUpdateValidator orderAlreadyDeliveredValidator,
      GetCoordinatesFromCepRequest getCoordinatesFromCepRequest) {
    this.orderService = orderService;
    this.uuidValidator = uuidValidator;
    this.orderIsAbleToUpdateValidator = orderAlreadyDeliveredValidator;
    this.getCoordinatesFromCepRequest = getCoordinatesFromCepRequest;
  }

  @Transactional
  public Order execute(String id, OrderDto orderDto) {
    uuidValidator.validate(id);
    var order = orderService.findByIdRequired(UUID.fromString(id));
    orderIsAbleToUpdateValidator.validate(order);
    updateOrderAttributesToSave(order, orderDto);
    return orderService.save(order);
  }

  private void updateOrderAttributesToSave(Order order, OrderDto orderDto) {
    order.setCompanyId(UUID.fromString(orderDto.companyId()));
    order.setCustomerId(UUID.fromString(orderDto.customerId()));
    if (isNecessaryGettingCompanyCoordinates(order)) {
      getCoordinatesFromWebAndUpdateCompanyAddress(order);
    }
    if (isNecessaryGettingCustomerCoordinates(order)) {
      getCoordinatesFromWebAndUpdateCustomerAddress(order);
    }
    updateOrderItemAttributesToSave(order.getOrderItems(), orderDto.orderItems());
  }

  private void updateOrderItemAttributesToSave(List<OrderItem> orderItems,
      List<OrderItemDto> orderItemsDto) {
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

  private void getCoordinatesFromWebAndUpdateCustomerAddress(Order order) {
    var coordinates = getCoordinatesFromCepRequest.request(order.getCustomerPostalCode());
    if (!coordinates.isEmpty()) {
      coordinates.forEach(coordinate -> {
        if (coordinate.containsKey(LATITUDE)) {
          order.setCustomerLatitude(coordinate.get(LATITUDE));
        }
        if (coordinate.containsKey(LONGITUDE)) {
          order.setCustomerLongitude(coordinate.get(LONGITUDE));
        }
      });
    }
  }

  private boolean isNecessaryGettingCustomerCoordinates(Order order) {
    return StringUtils.hasLength(order.getCompanyPostalCode())
        && (order.getCustomerLatitude() == null || order.getCustomerLatitude()
        .equals(BigDecimal.ZERO)
        || order.getCustomerLongitude() == null || order.getCustomerLongitude()
        .equals(BigDecimal.ZERO));
  }

  private void getCoordinatesFromWebAndUpdateCompanyAddress(Order order) {
    var coordinates = getCoordinatesFromCepRequest.request(order.getCompanyPostalCode());
    if (!coordinates.isEmpty()) {
      coordinates.forEach(coordinate -> {
        if (coordinate.containsKey(LATITUDE)) {
          order.setCompanyLatitude(coordinate.get(LATITUDE));
        }
        if (coordinate.containsKey(LONGITUDE)) {
          order.setCompanyLongitude(coordinate.get(LONGITUDE));
        }
      });
    }
  }

  private boolean isNecessaryGettingCompanyCoordinates(Order order) {
    return StringUtils.hasLength(order.getCompanyPostalCode())
        && (order.getCompanyLatitude() == null || order.getCompanyLatitude().equals(BigDecimal.ZERO)
        || order.getCompanyLongitude() == null || order.getCompanyLongitude()
        .equals(BigDecimal.ZERO));
  }
}
