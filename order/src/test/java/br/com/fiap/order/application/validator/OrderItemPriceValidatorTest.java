package br.com.fiap.order.application.validator;

import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_ITEM_PRICE_INVALID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_QUANTITY;
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
class OrderItemPriceValidatorTest {

  @Spy
  private OrderItemPriceValidator orderItemPriceValidator;

  @ParameterizedTest
  @ValueSource(strings = {"1", "1.25"})
  void shouldValidateOrderItemWhenItHasPriceGreaterThanZero(String value) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_QUANTITY, new BigDecimal(value));
    var orderItemsInputDto = List.of(orderItemInputDto);

    assertThatCode(() -> orderItemPriceValidator.validate(orderItemsInputDto))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "0"})
  void shouldThrowExceptionWhenOrderItemPriceIsInvalid(String value) {
    var orderItemInputDto = new OrderItemInputDto(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_SKU,
        DEFAULT_PRODUCT_QUANTITY, new BigDecimal(value));
    var orderItemsInputDto = List.of(orderItemInputDto);

    assertThatThrownBy(() -> orderItemPriceValidator.validate(orderItemsInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(ORDER_ITEM_PRICE_INVALID.formatted(value));
  }
}
