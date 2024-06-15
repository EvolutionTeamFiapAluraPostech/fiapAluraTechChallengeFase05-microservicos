package br.com.fiap.order.application.usecase;

import br.com.fiap.order.application.validator.OrderItemPriceValidator;
import br.com.fiap.order.application.validator.OrderItemQuantityValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.infrastructure.httpclient.cep.GetCoordinatesFromCepRequest;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateOrderUseCase {

  public static final String LATITUDE = "Latitude";
  public static final String LONGITUDE = "Longitude";
  private final OrderService orderService;
  private final OrderItemQuantityValidator orderItemQuantityValidator;
  private final OrderItemPriceValidator orderItemPriceValidator;
  private final GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;

  public CreateOrderUseCase(OrderService orderService,
      OrderItemQuantityValidator orderItemQuantityValidator,
      OrderItemPriceValidator orderItemPriceValidator,
      GetCoordinatesFromCepRequest getCoordinatesFromCepRequest) {
    this.orderService = orderService;
    this.orderItemQuantityValidator = orderItemQuantityValidator;
    this.orderItemPriceValidator = orderItemPriceValidator;
    this.getCoordinatesFromCepRequest = getCoordinatesFromCepRequest;
  }

  @Transactional
  public Order execute(OrderInputDto orderInputDto) {
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
    if (isNecessaryGettingCompanyCoordinates(order)) {
      getCoordinatesFromWebAndUpdateCompanyAddress(order);
    }
    if (isNecessaryGettingCustomerCoordinates(order)) {
      getCoordinatesFromWebAndUpdateCustomerAddress(order);
    }
    return order;
  }

  private void calculateTotalAmountOrderItem(Order order) {
    for (OrderItem orderItem : order.getOrderItems()) {
      orderItem.setOrder(order);
      orderItem.calculateTotalItemAmount();
    }
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
