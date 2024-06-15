package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ITEM_QUANTITY_INVALID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_DESCRIPTION;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_SKU;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.presentation.api.dto.OrderItemInputDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderItemQuantityValidatorTest {

  @Spy
  private OrderItemQuantityValidator orderItemQuantityValidator;

  @ParameterizedTest
  @ValueSource(strings = {"1", "1.25"})
  void shouldValidateOrderItemWhenItHasQuantityGreaterThanZero(String value) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_DESCRIPTION, new BigDecimal(value), DEFAULT_PRODUCT_PRICE);
    var orderItemsInputDto = List.of(orderItemInputDto);

    assertThatCode(() -> orderItemQuantityValidator.validate(orderItemsInputDto))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "0"})
  void shouldThrowExceptionWhenOrderItemQuantityIsInvalid(String value) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_DESCRIPTION, new BigDecimal(value), DEFAULT_PRODUCT_PRICE);
    var orderItemsInputDto = List.of(orderItemInputDto);

    assertThatThrownBy(() -> orderItemQuantityValidator.validate(orderItemsInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_ITEM_QUANTITY_INVALID.formatted(value));
  }
}
