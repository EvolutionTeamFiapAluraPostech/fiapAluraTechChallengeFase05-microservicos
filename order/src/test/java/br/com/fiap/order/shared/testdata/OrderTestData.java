package br.com.fiap.order.shared.testdata;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.enums.OrderStatus;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import br.com.fiap.order.presentation.api.dto.OrderItemInputDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OrderTestData {

  public static final UUID DEFAULT_COMPANY_UUID = UUID.fromString(
      "dcd3398e-4988-4fba-b8c0-a649ae1ff677");
  public static final String DEFAULT_COMPANY_ID = DEFAULT_COMPANY_UUID.toString();
  public static final UUID DEFAULT_CUSTOMER_UUID = UUID.fromString(
      "64f6db0a-3d9a-429c-a7e6-04c4691f3be9");
  public static final String DEFAULT_CUSTOMER_ID = DEFAULT_CUSTOMER_UUID.toString();
  public static final UUID DEFAULT_PRODUCT_UUID = UUID.fromString(
      "cfa8315f-3f9a-4105-a2f2-f02a0a303b20");
  public static final String DEFAULT_PRODUCT_ID = DEFAULT_PRODUCT_UUID.toString();
  public static final String DEFAULT_PRODUCT_SKU = "Key/BR-/Erg/Bla";
  public static final String DEFAULT_PRODUCT_DESCRIPTION = "Keyboard Ergonomic Black";

  public static final BigDecimal DEFAULT_PRODUCT_QUANTITY = BigDecimal.TEN;
  public static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal("315");
  public static final UUID ALTERNATIVE_COMPANY_UUID = UUID.randomUUID();
  public static final UUID ALTERNATIVE_CUSTOMER_UUID = UUID.randomUUID();
  public static final UUID ALTERNATIVE_PRODUCT_UUID = UUID.randomUUID();
  public static final BigDecimal ALTERNATIVE_PRODUCT_QUANTITY = BigDecimal.ONE;
  public static final BigDecimal ALTERNATIVE_PRODUCT_PRICE = new BigDecimal("1750.00");
  public static final UUID OTHER_PRODUCT_UUID = UUID.randomUUID();
  public static final BigDecimal DEFAULT_COMPANY_LATITUDE = new BigDecimal("-23.56388");
  public static final BigDecimal DEFAULT_COMPANY_LONGITUDE = new BigDecimal("-46.65241");
  public static final String DEFAULT_CUSTOMER_NAME = "Thomas Anderson";
  public static final String DEFAULT_CUSTOMER_EMAIL = "thomas.anderson@itcompany.com";
  public static final String DEFAULT_CUSTOMER_CPF_DOC_NUMBER = "11955975094";
  public static final String DEFAULT_CUSTOMER_DOC_NUMBER_TYPE = "CPF";
  public static final String DEFAULT_CUSTOMER_STREET = "Av. Lins de Vasconcelos";
  public static final String DEFAULT_CUSTOMER_ADDRESS_NUMBER = "1222";
  public static final String DEFAULT_CUSTOMER_NEIGHBORHOOD = "Cambuci";
  public static final String DEFAULT_CUSTOMER_CITY = "São Paulo";
  public static final String DEFAULT_CUSTOMER_STATE = "SP";
  public static final String DEFAULT_CUSTOMER_COUNTRY = "Brasil";
  public static final String DEFAULT_CUSTOMER_POSTAL_CODE = "01538001";


  public static Order createNewOrder() {
    var orderItems = new ArrayList<OrderItem>();
    var totalOrderAmount = DEFAULT_PRODUCT_QUANTITY.multiply(DEFAULT_PRODUCT_PRICE);

    var order = Order.builder()
        .companyId(DEFAULT_COMPANY_UUID)
        .customerId(DEFAULT_CUSTOMER_UUID)
        .customerName(DEFAULT_CUSTOMER_NAME)
        .customerEmail(DEFAULT_CUSTOMER_EMAIL)
        .customerDocNumber(DEFAULT_CUSTOMER_CPF_DOC_NUMBER)
        .customerDocNumberType(DEFAULT_CUSTOMER_DOC_NUMBER_TYPE)
        .customerStreet(DEFAULT_CUSTOMER_STREET)
        .customerNumber(DEFAULT_CUSTOMER_ADDRESS_NUMBER)
        .customerNeighborhood(DEFAULT_CUSTOMER_NEIGHBORHOOD)
        .customerCity(DEFAULT_CUSTOMER_CITY)
        .customerState(DEFAULT_CUSTOMER_STATE)
        .customerCountry(DEFAULT_CUSTOMER_COUNTRY)
        .customerPostalCode(DEFAULT_CUSTOMER_POSTAL_CODE)
        .orderStatus(OrderStatus.AGUARDANDO_PAGAMENTO)
        .orderDate(LocalDateTime.now())
        .orderItems(orderItems)
        .orderTotalAmount(totalOrderAmount)
        .build();

    var orderItem = OrderItem.builder()
        .order(order)
        .productId(DEFAULT_PRODUCT_UUID)
        .productSku(DEFAULT_PRODUCT_SKU)
        .productDescription(DEFAULT_PRODUCT_DESCRIPTION)
        .quantity(DEFAULT_PRODUCT_QUANTITY)
        .price(DEFAULT_PRODUCT_PRICE)
        .totalAmount(totalOrderAmount)
        .build();
    orderItems.add(orderItem);
    return order;
  }

  public static Order createOrder() {
    var order = createNewOrder();
    order.setId(UUID.randomUUID());
    order.getOrderItems().get(0).setId(UUID.randomUUID());
    return order;
  }

  public static OrderInputDto createNewOrderInputDto() {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_DESCRIPTION, DEFAULT_PRODUCT_QUANTITY, DEFAULT_PRODUCT_PRICE);
    var orderItemsInputDto = List.of(orderItemInputDto);

    return new OrderInputDto(DEFAULT_COMPANY_ID,
        DEFAULT_CUSTOMER_ID,
        DEFAULT_CUSTOMER_NAME,
        DEFAULT_CUSTOMER_EMAIL,
        DEFAULT_CUSTOMER_CPF_DOC_NUMBER,
        DEFAULT_CUSTOMER_DOC_NUMBER_TYPE,
        DEFAULT_CUSTOMER_STREET,
        DEFAULT_CUSTOMER_ADDRESS_NUMBER,
        DEFAULT_CUSTOMER_NEIGHBORHOOD,
        DEFAULT_CUSTOMER_CITY,
        DEFAULT_CUSTOMER_STATE,
        DEFAULT_CUSTOMER_COUNTRY,
        DEFAULT_CUSTOMER_POSTAL_CODE,
        DEFAULT_COMPANY_LATITUDE,
        DEFAULT_COMPANY_LONGITUDE,
        orderItemsInputDto);
  }

  private OrderTestData() {
  }
}
