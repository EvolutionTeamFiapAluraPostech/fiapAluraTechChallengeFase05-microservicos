package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_COMPANY_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_CUSTOMER_ID;
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

import br.com.fiap.order.application.validator.CompanyExistsValidator;
import br.com.fiap.order.application.validator.CustomerExistsValidator;
import br.com.fiap.order.application.validator.OrderItemPriceValidator;
import br.com.fiap.order.application.validator.OrderItemQuantityValidator;
import br.com.fiap.order.application.validator.ProductExistsValidator;
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
  private CompanyExistsValidator companyExistsValidator;
  @Mock
  private CustomerExistsValidator customerExistsValidator;
  @Mock
  private ProductExistsValidator productExistsValidator;
  @Mock
  private OrderItemQuantityValidator orderItemQuantityValidator;
  @Mock
  private OrderItemPriceValidator orderItemPriceValidator;
  @InjectMocks
  private CreateOrderUseCase createOrderUseCase;

  private List<String> getProductsIdListFrom(OrderInputDto orderInputDto) {
    return orderInputDto.orderItems().stream().map(OrderItemInputDto::productId).toList();
  }

  @Test
  void shouldCreateOrder() {
    var orderInputDto = OrderTestData.createNewOrderInputDto();
    var orderWithId = OrderTestData.createOrder();
    var productsIdListFrom = getProductsIdListFrom(orderInputDto);
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

    verify(companyExistsValidator).validate(orderInputDto.companyId());
    verify(customerExistsValidator).validate(orderInputDto.customerId());
    verify(productExistsValidator).validate(productsIdListFrom);
    verify(orderItemQuantityValidator).validate(orderInputDto.orderItems());
    verify(orderItemPriceValidator).validate(orderInputDto.orderItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "0"})
  void shouldThrowExceptionWhenOrderItemQuantityIsInvalid(String quantity) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        new BigDecimal(quantity), DEFAULT_PRODUCT_PRICE);
    var orderItemsInputDto = List.of(orderItemInputDto);
    var orderInputDto = new OrderInputDto(DEFAULT_COMPANY_ID,
        DEFAULT_CUSTOMER_ID,
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
        DEFAULT_PRODUCT_QUANTITY, new BigDecimal(price));
    var orderItemsInputDto = List.of(orderItemInputDto);
    var orderInputDto = new OrderInputDto(DEFAULT_COMPANY_ID,
        DEFAULT_CUSTOMER_ID,
        orderItemsInputDto);

    doThrow(ValidatorException.class).when(orderItemPriceValidator).validate(orderItemsInputDto);

    assertThatThrownBy(() -> createOrderUseCase.execute(orderInputDto))
        .isInstanceOf(ValidatorException.class);

    verify(orderItemPriceValidator).validate(orderInputDto.orderItems());
    verify(orderService, never()).save(any(Order.class));
  }
}
