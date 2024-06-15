package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_ADDRESS_NUMBER;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_CITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_CNPJ_DOC_NUMBER;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_COUNTRY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_DOC_NUMBER_TYPE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_EMAIL;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_LATITUDE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_LONGITUDE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_NAME;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_NEIGHBORHOOD;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_POSTAL_CODE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_STATE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_STREET;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_ADDRESS_NUMBER;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_CITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_COUNTRY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_CPF_DOC_NUMBER;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_DOC_NUMBER_TYPE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_EMAIL;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_NAME;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_NEIGHBORHOOD;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_POSTAL_CODE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_STATE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_STREET;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_DESCRIPTION;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_QUANTITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_SKU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.OrderItemPriceValidator;
import br.com.fiap.order.application.validator.OrderItemQuantityValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import br.com.fiap.order.presentation.api.dto.OrderItemInputDto;
import br.com.fiap.order.shared.testdata.OrderTestData;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

  @Mock
  private OrderService orderService;
  @Mock
  private OrderItemQuantityValidator orderItemQuantityValidator;
  @Mock
  private OrderItemPriceValidator orderItemPriceValidator;
  @InjectMocks
  private CreateOrderUseCase createOrderUseCase;

  @Test
  void shouldCreateOrder() {
    var orderInputDto = OrderTestData.createNewOrderInputDto();
    var orderWithId = OrderTestData.createOrder();
    when(orderService.save(any(Order.class))).thenReturn(orderWithId);

    var orderSaved = createOrderUseCase.execute(orderInputDto);

    assertThat(orderSaved).isNotNull();
    assertThat(orderSaved.getId()).isNotNull().isEqualTo(orderWithId.getId());
    assertThat(orderSaved.getCompanyId()).isNotNull().isEqualTo(orderWithId.getCompanyId());
    assertThat(orderSaved.getCustomerId()).isNotNull().isEqualTo(orderWithId.getCustomerId());
    assertThat(orderSaved.getOrderItems()).isNotEmpty();
    assertThat(orderSaved.getOrderItems()).hasSize(orderWithId.getOrderItems().size());
    assertThat(orderSaved.getOrderItems().get(0).getId()).isNotNull()
        .isEqualTo(orderWithId.getOrderItems().get(0).getId());
    assertThat(orderSaved.getOrderItems().get(0).getProductId()).isNotNull()
        .isEqualTo(orderWithId.getOrderItems().get(0).getProductId());
    assertThat(orderSaved.getOrderItems().get(0).getQuantity()).isNotNull()
        .isEqualTo(orderWithId.getOrderItems().get(0).getQuantity());
    assertThat(orderSaved.getOrderItems().get(0).getPrice()).isNotNull()
        .isEqualTo(orderWithId.getOrderItems().get(0).getPrice());
    verify(orderItemQuantityValidator).validate(orderInputDto.orderItems());
    verify(orderItemPriceValidator).validate(orderInputDto.orderItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "0"})
  void shouldThrowExceptionWhenOrderItemQuantityIsInvalid(String quantity) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_DESCRIPTION, new BigDecimal(quantity), DEFAULT_PRODUCT_PRICE);
    var orderItemsInputDto = List.of(orderItemInputDto);
    var orderInputDto = new OrderInputDto(DEFAULT_COMPANY_ID,
        DEFAULT_COMPANY_NAME,
        DEFAULT_COMPANY_EMAIL,
        DEFAULT_COMPANY_CNPJ_DOC_NUMBER,
        DEFAULT_COMPANY_DOC_NUMBER_TYPE,
        DEFAULT_COMPANY_STREET,
        DEFAULT_COMPANY_ADDRESS_NUMBER,
        DEFAULT_COMPANY_NEIGHBORHOOD,
        DEFAULT_COMPANY_CITY,
        DEFAULT_COMPANY_STATE,
        DEFAULT_COMPANY_COUNTRY,
        DEFAULT_COMPANY_POSTAL_CODE,
        DEFAULT_COMPANY_LATITUDE,
        DEFAULT_COMPANY_LONGITUDE,
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

    doThrow(ValidatorException.class).when(orderItemQuantityValidator).validate(orderItemsInputDto);

    assertThatThrownBy(() -> createOrderUseCase.execute(orderInputDto))
        .isInstanceOf(ValidatorException.class);

    verify(orderItemPriceValidator, never()).validate(orderInputDto.orderItems());
    verify(orderService, never()).save(any(Order.class));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "0"})
  void shouldThrowExceptionWhenOrderItemPriceIsInvalid(String price) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_DESCRIPTION, DEFAULT_PRODUCT_QUANTITY, new BigDecimal(price));
    var orderItemsInputDto = List.of(orderItemInputDto);
    var orderInputDto = new OrderInputDto(DEFAULT_COMPANY_ID,
        DEFAULT_COMPANY_NAME,
        DEFAULT_COMPANY_EMAIL,
        DEFAULT_COMPANY_CNPJ_DOC_NUMBER,
        DEFAULT_COMPANY_DOC_NUMBER_TYPE,
        DEFAULT_COMPANY_STREET,
        DEFAULT_COMPANY_ADDRESS_NUMBER,
        DEFAULT_COMPANY_NEIGHBORHOOD,
        DEFAULT_COMPANY_CITY,
        DEFAULT_COMPANY_STATE,
        DEFAULT_COMPANY_COUNTRY,
        DEFAULT_COMPANY_POSTAL_CODE,
        DEFAULT_COMPANY_LATITUDE,
        DEFAULT_COMPANY_LONGITUDE,
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

    doThrow(ValidatorException.class).when(orderItemPriceValidator).validate(orderItemsInputDto);

    assertThatThrownBy(() -> createOrderUseCase.execute(orderInputDto))
        .isInstanceOf(ValidatorException.class);

    verify(orderItemPriceValidator).validate(orderInputDto.orderItems());
    verify(orderService, never()).save(any(Order.class));
  }
}
