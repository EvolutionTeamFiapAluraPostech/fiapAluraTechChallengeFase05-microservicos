package br.com.fiap.payment.shared.testdata;

import static br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus.AGUARDANDO_PAGAMENTO;

import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderItemDto;
import br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class OrderTestData {

  public static final UUID DEFAULT_ORDER_UUID = UUID.randomUUID();
  public static final String DEFAULT_ORDER_ID = DEFAULT_ORDER_UUID.toString();
  public static final UUID DEFAULT_ORDER_ITEM_UUID = UUID.randomUUID();
  public static final String DEFAULT_ORDER_ITEM_ID = DEFAULT_ORDER_ITEM_UUID.toString();
  public static final UUID DEFAULT_COMPANY_UUID = UUID.fromString(
      "dcd3398e-4988-4fba-b8c0-a649ae1ff677");
  public static final String DEFAULT_COMPANY_ID = DEFAULT_COMPANY_UUID.toString();
  public static final UUID DEFAULT_CUSTOMER_UUID = UUID.fromString(
      "64f6db0a-3d9a-429c-a7e6-04c4691f3be9");
  public static final String DEFAULT_CUSTOMER_ID = DEFAULT_CUSTOMER_UUID.toString();
  public static final UUID DEFAULT_PRODUCT_UUID = UUID.fromString(
      "cfa8315f-3f9a-4105-a2f2-f02a0a303b20");
  public static final String DEFAULT_PRODUCT_ID = DEFAULT_PRODUCT_UUID.toString();
  public static final BigDecimal DEFAULT_PRODUCT_QUANTITY = BigDecimal.TEN;
  public static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal("315");

  public static OrderDto createOrderDto() {
    var orderItemDto = new OrderItemDto(DEFAULT_ORDER_ITEM_ID, DEFAULT_PRODUCT_ID,
        DEFAULT_PRODUCT_QUANTITY, DEFAULT_PRODUCT_PRICE);
    return new OrderDto(DEFAULT_ORDER_ID, AGUARDANDO_PAGAMENTO.name(),
        DEFAULT_COMPANY_ID, DEFAULT_CUSTOMER_ID,
        List.of(orderItemDto));
  }

  public static OrderDto createOrderDtoWithoutItem() {
    return new OrderDto(DEFAULT_ORDER_ID, AGUARDANDO_PAGAMENTO.name(), DEFAULT_COMPANY_ID,
        DEFAULT_CUSTOMER_ID, Collections.emptyList());
  }

  public static OrderDto createOrderDtoWithItemWithInvalidTotalAmount() {
    var orderItemDto = new OrderItemDto(DEFAULT_ORDER_ITEM_ID, DEFAULT_PRODUCT_ID,
        BigDecimal.ONE, BigDecimal.ZERO);
    return new OrderDto(DEFAULT_ORDER_ID, AGUARDANDO_PAGAMENTO.name(), DEFAULT_COMPANY_ID,
        DEFAULT_CUSTOMER_ID, Collections.singletonList(orderItemDto));
  }

  public static OrderDto createOrderDto(OrderStatus orderStatus) {
    var orderItemDto = new OrderItemDto(DEFAULT_ORDER_ITEM_ID, DEFAULT_PRODUCT_ID,
        DEFAULT_PRODUCT_QUANTITY, DEFAULT_PRODUCT_PRICE);
    return new OrderDto(DEFAULT_ORDER_ID, orderStatus.name(),
        DEFAULT_COMPANY_ID, DEFAULT_CUSTOMER_ID,
        List.of(orderItemDto));
  }

  private OrderTestData() {
  }
}
