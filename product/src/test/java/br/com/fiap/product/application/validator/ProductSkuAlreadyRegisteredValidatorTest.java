package br.com.fiap.product.application.validator;

import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE;
import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.product.domain.exception.DuplicatedException;
import br.com.fiap.product.domain.service.ProductService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSkuAlreadyRegisteredValidatorTest {

  @Mock
  private ProductService productService;
  @InjectMocks
  private ProductSkuAlreadyRegisteredValidator productSkuAlreadyRegisteredValidator;

  @Test
  void shouldValidateWhenProductSkuDoesNotExist() {
    var product = createProduct();
    when(productService.findBySku(product.getSku())).thenReturn(Optional.empty());
    assertThatCode(() -> productSkuAlreadyRegisteredValidator.validate(product.getSku()))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenProductSkuAlreadyExists() throws Exception {
    var product = createNewProduct();
    when(productService.findBySku(product.getSku())).thenReturn(Optional.of(product));

    assertThatThrownBy(() -> productSkuAlreadyRegisteredValidator.validate(product.getSku()))
        .isInstanceOf(DuplicatedException.class)
        .hasMessage(PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE.formatted(product.getSku()));
  }
}
